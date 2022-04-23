package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.HeroAircraft;
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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
public class Game extends JPanel {

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
    private AbstractAircraft boss;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;

    /**
     * 敌机和道具的工厂
     */
    private final EnemyFactory mobEnemyFactory;
    private final EnemyFactory eliteEnemyFactory;
    private final EnemyFactory bossEnemyFactory;

    private int enemyMaxNumber = 5;

    private boolean gameOverFlag = false;
    private int score = 0;

    public int getScore() {
        return score;
    }

    private int step = 200;
    private int time = 0;
    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;

    /**
     * 难度与音效
     */
    //UserDao userDao = new UserDaoImpl();
    private Integer difficulty = 0;
    /**
     * 7个位置分别管理7个音频
     * 0:bgm
     * 1:bgm_boss
     * 2:bomb_explosion
     * 3:bullet
     * 4:bullet_hit
     * 5:game_over
     * 6:get_supply
     */
    private MusicThread[] musicThreads = new MusicThread[7];
    private boolean useMusic = true;

    public void setUseMusic(boolean useMusic) {
        this.useMusic = useMusic;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * 射击相关，暂时写在Game中，之后使用观察者模式会放在道具中
     */
    private boolean isScattering = false;
    private Thread lastThread;

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

    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

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
            if (useMusic) {
                musicCheck();
            }

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            // 游戏结束检查
            if (!gameOverFlag && heroAircraft.getHp() <= 0) {
                // 游戏结束
                //播放游戏结束音频
                if (useMusic) {
                    MusicThread gameOverMusic = new MusicThread("src/videos/game_over.wav");
                    musicThreads[5] = gameOverMusic;
                    gameOverMusic.start();
                }
                executorService.shutdown();
                gameOverFlag = true;
                System.out.println("Game Over!");
                synchronized (Status.class) {
                    Status.gameOver = true;
                    Status.class.notifyAll();
                }
                for (int i = 0; i < 7; i++) {
                    if (i == 5) {
                        try {
                            musicThreads[i].join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (Objects.nonNull(musicThreads[i])) {
                        musicThreads[i].setStop(true);
                    }
                }
                //showRankingList();
            }

        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

        if (useMusic) {
            MusicThread bgm = new MusicThread("src/videos/bgm.wav");
            musicThreads[0] = bgm;
            bgm.start();
        }
    }

    private void musicCheck() {
        //背景音乐循环播放
        if (!musicThreads[0].isAlive()) {
            MusicThread bgm = new MusicThread("src/videos/bgm.wav");
            musicThreads[0] = bgm;
            bgm.start();
        }
        //boss音乐
        if (Objects.isNull(boss) || boss.notValid()) {
            if (Objects.nonNull(musicThreads[1])) {
                musicThreads[1].setStop(true);
                musicThreads[1] = null;
            }
        } else {
            if (Objects.isNull(musicThreads[1]) || !musicThreads[1].isAlive()) {
                MusicThread bossBgm = new MusicThread("src/videos/bgm_boss.wav");
                musicThreads[1] = bossBgm;
                bossBgm.start();
            }
        }
    }

    /**
     * 展示用户排行榜
     */
    /*private void showRankingList() {
        User user = new User();
        user.setCreateTime(System.currentTimeMillis());
        user.setScore(score);
        user.setName("testUserName");
        user.setDifficulty(0);
        userDao.saveUser(user);
        List<User> userList = userDao.selectUsersOrderByScoreDesc();
        System.out.println("**************************************************");
        System.out.println("********************得分排行榜*********************");
        System.out.println("**************************************************");
        for (int i = 0; i < userList.size(); i++) {
            User usr = userList.get(i);
            System.out.printf("第%d名：" + usr.getName() + ", %d, "
                    , i + 1, usr.getScore());
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            String timeStr = sdf.format(new Date(usr.getCreateTime()));
            System.out.println(timeStr);
        }
    }*/

    /**
     * 以一定概率产生普通敌机或精英敌机
     *
     * @return 产生的敌机
     */
    private AbstractAircraft produceEnemy() {
        //三分之一的概率产生精英敌机
        if (System.currentTimeMillis() % EliteEnemy.PROBABILITY == 0) {
            return eliteEnemyFactory.createEnemy();
        } else {
            return mobEnemyFactory.createEnemy();
        }
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

    private void shootAction() {
        if (useMusic) {
            //播放子弹音频
            MusicThread bulletMusic = new MusicThread("src/videos/bullet.wav");
            bulletMusic.start();
            musicThreads[3] = bulletMusic;
        }
        // TODO 敌机射击
        //普通敌机射出的子弹数目为0，不影响结果
        List<BaseBullet> enemyBullets0 = enemyAircrafts.stream()
                .flatMap(abstractAircraft -> abstractAircraft.shoot().stream())
                .collect(Collectors.toList());
        enemyBullets.addAll(enemyBullets0);
        if (Objects.nonNull(boss)) {
            enemyBullets.addAll(boss.shoot());
        }

        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
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
     * TODO：道具移动
     */
    private void propsMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }


    /**
     * 检查得分是否到达阈值，适时产生或刷新boss机
     */
    private void scoreCheck() {
        if (score >= step) {
            //没有boss则产生boss，已经有boss则刷新boss
            if (Objects.nonNull(boss)) {
                boss.vanish();
                System.out.println("boss刷新");
            }
            boss = bossEnemyFactory.createEnemy();
            step += 200;
            if (useMusic) {
                //如果此前没有播放boss音乐，则开放播放boss音乐
                if (Objects.isNull(musicThreads[1]) || !musicThreads[1].isAlive()) {
                    MusicThread bossBgm = new MusicThread("src/videos/bgm_boss.wav");
                    musicThreads[1] = bossBgm;
                    bossBgm.start();
                }
            }
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
                if (useMusic) {
                    //播放子弹击中音频
                    MusicThread bulletHit = new MusicThread("src/videos/bullet_hit.wav");
                    musicThreads[4] = bulletHit;
                    bulletHit.start();
                }
                // 英雄机撞击到敌机子弹
                // 英雄机损失一定生命值
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
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
                    if (useMusic) {
                        //播放子弹击中音频
                        MusicThread bulletHit = new MusicThread("src/videos/bullet_hit.wav");
                        musicThreads[4] = bulletHit;
                        bulletHit.start();
                    }
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    //如果英雄子弹消灭了敌机
                    if (enemyAircraft.notValid()) {
                        // TODO 获得分数，一定概率在同一位置产生道具补给
                        score += 20;
                        //如果消灭的是精英机则一定概率掉落道具
                        AbstractProp abstractProp = enemyAircraft.produceProp();
                        //如果返回null代表没掉落道具
                        if (!Objects.isNull(abstractProp)) {
                            props.add(abstractProp);
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
                    if (useMusic) {
                        //播放子弹击中音频
                        MusicThread bulletHit = new MusicThread("src/videos/bullet_hit.wav");
                        musicThreads[4] = bulletHit;
                        bulletHit.start();
                    }
                    boss.decreaseHp(bullet.getPower());
                    bullet.vanish();
                }
                /*if (boss.notValid()) {
                    score += 10;
                }*/
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
                /*
                 * 注：之后要改成观察者模式，使用prop.notifyObservers();目前暂时写到Game类中
                 */
                if (prop instanceof BloodProp) {
                    if (useMusic) {
                        //播放游戏supply音频
                        MusicThread getSupplyMusic = new MusicThread("src/videos/get_supply.wav");
                        musicThreads[6] = getSupplyMusic;
                        getSupplyMusic.start();
                    }
                    heroAircraft.increaseHp(100);
                } else if (prop instanceof BulletProp) {
                    //之后再改进
                    if (isScattering) {
                        lastThread.interrupt();
                    }
                    heroAircraft.setShootStrategy(new ScatteringShootStrategy());
                    heroAircraft.setShootNum(5);
                    isScattering = true;
                    lastThread = new Thread(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                            heroAircraft.setShootStrategy(new StraightShootStrategy());
                            heroAircraft.setShootNum(2);
                            isScattering = false;
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    });
                    lastThread.start();
                } else if (prop instanceof BombProp) {
                    System.out.println("BombSupply active!");
                    if (useMusic) {
                        MusicThread bombMusic = new MusicThread("src/videos/bomb_explosion.wav");
                        bombMusic.start();
                        musicThreads[2] = bombMusic;
                    }
                }
                prop.vanish();
            }
        }

    }


    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * 4. 删除已经被英雄碰到的道具
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
        if (Objects.nonNull(boss) && boss.notValid()) {
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


        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

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
