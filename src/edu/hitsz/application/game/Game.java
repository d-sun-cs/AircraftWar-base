package edu.hitsz.application.game;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.*;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.dao.UserDao;
import edu.hitsz.dao.impl.UserDaoImpl;
import edu.hitsz.dao.pojo.User;
import edu.hitsz.factory.EnemyFactory;
import edu.hitsz.factory.PropFactory;
import edu.hitsz.factory.impl.*;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BloodProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.BulletProp;
import edu.hitsz.strategy.ScatteringShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public abstract class Game extends JPanel {

    private int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    /**
     * 飞机、道具、子弹实体类
     */
    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    protected AbstractAircraft boss;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;

    /**
     * 敌机和道具的工厂
     */
    private final EnemyFactory mobEnemyFactory;
    private final EnemyFactory eliteEnemyFactory;
    protected final EnemyFactory bossEnemyFactory;

    protected int enemyMaxNumber = 5;

    private boolean gameOverFlag = false;
    protected static int score = 0;


    /**
     * 下一次产生boss的分数阈值（如果要产生boss的话）
     */
    protected int step = 300;


    private int time = 0;
    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;

    /**
     * 周期（ms)
     * 指示难度提升频率
     */
    private int cycleDurationForGame = 20000;
    private int cycleTimeForGame = 0;

    /**
     * 难度与音效
     */
    private static Integer difficulty = 0;
    MusicThread bgm;
    MusicThread bossBgm;
    private static boolean needMusic = true;


    public Game() {
        //初始化飞机、子弹、道具实体类(集合)
        heroAircraft = HeroAircraft.getInstance();
        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();
        //初始化敌机和道具工厂
        mobEnemyFactory = new MobEnemyFactory();
        eliteEnemyFactory = new EliteEnemyFactory();
        bossEnemyFactory = new BossEnemyFactory();

        //Scheduled 线程池，用于定时任务调度
        executorService = new ScheduledThreadPoolExecutor(1, (ThreadFactory) Thread::new);

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

        //彩蛋监听器
        new BonusSceneController(this);

    }

    public static int getScore() {
        return score;
    }

    public static void addScore(int increment) {
        if (increment > 0) {
            score += increment;
        }
    }

    public static void setNeedMusic(boolean useMusic) {
        Game.needMusic = useMusic;
    }

    public static Integer getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(Integer difficulty) {
        Game.difficulty = difficulty;
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public final void action() {

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
                // 新敌机产生
                if (enemyAircrafts.size() < enemyMaxNumber) {
                    AbstractAircraft enemyAircraft = produceEnemy();
                    enemyAircrafts.add(enemyAircraft);
                }
                // 飞机射出子弹
                shootAction();
                //注：如果精英敌机随机出生在英雄子弹上，也会立刻发射一枚子弹又消失，看起来像凭空产生子弹
                //解决方式：让精英敌机的生命值大于一颗英雄机子弹的伤害
            }

            if (timeCountAndNewCycleJudgeForGameDifficulty()) {
                increaseDifficulty();
            }

            // 子弹移动
            bulletsMoveAction();

            // 敌机移动
            aircraftsMoveAction();

            //道具移动
            propsMoveAction();

            // 撞击检测
            crashCheckAction();

            //检测分数是否达到产生boss机的阈值
            scoreCheck();

            //检查音乐
            if (needMusic) {
                musicCheck();
            }

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            // 游戏结束检查
            if (!gameOverFlag && heroAircraft.getHp() <= 0) {
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;
                System.out.println("Game Over!");
                //停止播放音乐
                if (needMusic) {
                    if (Objects.nonNull(bgm)) {
                        bgm.setStop(true);
                    }
                    if (Objects.nonNull(bossBgm)) {
                        bossBgm.setStop(true);
                    }
                    MusicThread gameOverMusic = new MusicThread("src/videos/game_over.wav");
                    gameOverMusic.start();
                    try {
                        gameOverMusic.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronized (Status.class) {
                    Status.gameOver = true;
                    Status.class.notifyAll();
                }
            }

        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 提升游戏难度
     */
    protected abstract void increaseDifficulty();

    /**
     * 控制敌机在每周期是否射击的钩子
     * @return
     */
    protected boolean canEnemyShoot() {
        return true;
    }

    /**
     * 控制英雄机在每周期是否射击的钩子
     * @return
     */
    protected boolean canHeroShoot() {
        return true;
    }

    /**
     * 用于改变敌机属性的钩子
     * @param enemy
     */
    protected void increaseEnemyProperty(AbstractAircraft enemy) {}

    /**
     * 检查得分是否到达阈值，适时产生或刷新boss机
     */
    protected abstract void scoreCheck();

    /**
     * 切换背景音乐与boss音乐，并实现循环播放
     */
    private void musicCheck() {
        //背景音乐循环播放(在boss不存在的情况下才有必要播放)
        if ((bgm == null || !bgm.isAlive())
                && (Objects.isNull(boss) || boss.notValid())) {
            bgm = new MusicThread("src/videos/bgm.wav");
            bgm.start();
        }
        //boss音乐
        if (Objects.isNull(boss) || boss.notValid()) {
            if (Objects.nonNull(bossBgm)) {
                bossBgm.setStop(true);
            }
        } else {
            //如果有boss则停止播放原背景音乐
            bgm.setStop(true);
            if (Objects.isNull(bossBgm) || !bossBgm.isAlive()) {
                bossBgm = new MusicThread("src/videos/bgm_boss.wav");
                bossBgm.start();
            }
        }
    }

    /**
     * 以一定概率产生普通敌机或精英敌机
     *
     * @return 产生的敌机
     */
    private AbstractAircraft produceEnemy() {
        AbstractAircraft enemy;
        if (System.currentTimeMillis() % EliteEnemy.probability == 0) {
            enemy = eliteEnemyFactory.createEnemy();
        } else {
            enemy = mobEnemyFactory.createEnemy();
        }
        for (AbstractProp prop : props) {
            prop.subscribe(enemy);
        }
        increaseEnemyProperty(enemy);
        return enemy;
    }

    /**
     * 为了实现每隔一定时间提升游戏难度，进行时间周期的记录
     * @return
     */
    private boolean timeCountAndNewCycleJudgeForGameDifficulty() {
        cycleTimeForGame += timeInterval;
        if (cycleTimeForGame >= cycleDurationForGame && cycleTimeForGame - timeInterval < cycleTimeForGame) {
            // 跨越到新的周期
            cycleTimeForGame %= cycleDurationForGame;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 射击动作
     */
    private void shootAction() {
        // TODO 敌机射击
        //普通敌机射出的子弹数目为0，不影响结果
        if (canEnemyShoot()) {
            List<BaseBullet> enemyBullets0 = enemyAircrafts.stream()
                    .flatMap(abstractAircraft -> abstractAircraft.shoot().stream())
                    .collect(Collectors.toList());
            enemyBullets.addAll(enemyBullets0);
            if (Objects.nonNull(boss)) {
                enemyBullets.addAll(boss.shoot());
            }
        }

        // 英雄射击
        if (canHeroShoot()) {
            heroBullets.addAll(heroAircraft.shoot());
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    /**
     * 道具移动
     */
    private void propsMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }

    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                // 英雄机撞击到敌机子弹
                // 英雄机损失一定生命值
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish(needMusic);
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            //对普通敌机和精英敌机的检测
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish(needMusic);
                    //如果英雄子弹消灭了敌机
                    if (enemyAircraft.notValid()) {
                        // 获得分数，一定概率在同一位置产生道具补给
                        score += 20;
                        //如果消灭的是精英机则一定概率掉落道具
                        AbstractProp prop = enemyAircraft.produceProp();
                        //如果返回null代表没掉落道具
                        if (!Objects.isNull(prop)) {
                            //飞机订阅道具
                            prop.subscribe(enemyAircrafts);
                            prop.subscribe(heroAircraft);
                            prop.subscribe(boss);
                            props.add(prop);
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
            //对Boss机的检测
            if (Objects.nonNull(boss)) {
                if (boss.crash(bullet)) {
                    boss.decreaseHp(bullet.getPower());
                    bullet.vanish(needMusic);
                }
                if (boss.crash(heroAircraft) || heroAircraft.crash(boss)) {
                    boss.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }


        // Todo: 我方获得道具，道具生效
        for (AbstractProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (heroAircraft.crash(prop)) {
                // 英雄机碰到道具、产生道具效果
                prop.notifyObservers();
                prop.vanish(needMusic);
            }
        }

    }


    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * 4. 删除已经被英雄碰到的道具
     * 5. 删除无效的boss
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            if (enemyAircraft.notValid()) {
                for (AbstractProp prop : props) {
                    prop.unsubscribe(enemyAircraft);
                }
            }
        }
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
        if (Objects.nonNull(boss) && boss.notValid()) {
            for (AbstractProp prop : props) {
                prop.unsubscribe(boss);
            }
            //boss与null则代表目前游戏中没有boss
            boss = null;
        }
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param g
     */
    //用于触发hit校徽彩蛋
    public static boolean bonusScene = false;
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        //TODO 绘制道具，显示在最下层
        paintImageWithPositionRevised(g, props);
        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);

        paintImageWithPositionRevised(g, enemyAircrafts);

        //绘制boss
        if (!Objects.isNull(boss)) {
            BufferedImage image = boss.getImage();
            assert image != null : boss.getClass().getName() + " has no image! ";
            g.drawImage(image, boss.getLocationX() - image.getWidth() / 2,
                    boss.getLocationY() - image.getHeight() / 2, null);
        }


        g.drawImage(ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        if (bonusScene) {
            try {
                g.drawImage(ImageIO.read(new FileInputStream("src/images/hit.png")),
                        400,
                        10,
                        null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }


}
