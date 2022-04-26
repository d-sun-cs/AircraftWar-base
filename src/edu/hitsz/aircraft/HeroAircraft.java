package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BloodProp;
import edu.hitsz.prop.BulletProp;
import edu.hitsz.strategy.ScatteringShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 英雄飞机，游戏玩家操控
 *
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {
    /**
     * 单例T
     */
    private static final HeroAircraft singleton;

    /**
     * 使用静态代码块的方式，在类加载时初始化单例（饿汉式）
     */
    static {
        int locationX = Main.WINDOW_WIDTH / 2;
        int locationY = Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight();
        int speedX = 0;
        int speedY = 0;
        int hp = 10000;
        singleton = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
        singleton.setShootStrategy(new StraightShootStrategy());
    }

    private boolean isScattering = false;
    private Thread scatteringThread;

    /**
     * 对外提供获得单例的方法
     *
     * @return 单例
     */
    public static HeroAircraft getInstance() {
        return singleton;
    }

    /**
     * 单例模式下，私有化的构造方法
     *
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX    英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY    英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp        初始生命值
     */
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    /**
     * 攻击方式
     */
    private int shootNum = 2;     //子弹一次发射数量
    //子弹伤害
    private int power = 30;
    //子弹射击方向 (向上发射：1，向下发射：-1)
    private int direction = -1;

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * (之后要将该方法移交给一个射击策略类)
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(HeroBullet.class, this.getLocationX(), this.getLocationY(), direction, power, speedY, shootNum);
    }

    /**
     * 不掉落道具，空实现(目前不太符合接口隔离原则，待改进)
     * @return null
     */
    @Override
    public AbstractProp produceProp() {
        return null;
    }

    @Override
    public void update(Class<? extends AbstractProp> propClass) {
        if (BloodProp.class.equals(propClass)) {
            this.hp += 100;
            if (this.hp > 10000) {
                this.hp = 10000;
            }
        } else if (BulletProp.class.equals(propClass)) {
            if (isScattering) {
                scatteringThread.interrupt();
            }
            this.shootStrategy = new ScatteringShootStrategy();
            this.shootNum = 5;
            isScattering = true;
            scatteringThread = new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(8);
                    this.shootStrategy = new StraightShootStrategy();
                    this.shootNum = 2;
                    isScattering = false;
                } catch (InterruptedException e) {
                    System.out.println("子弹道具刷新");
                }
            });
            scatteringThread.start();
        }
    }

}
