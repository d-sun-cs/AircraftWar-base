package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 *
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {
    /**
     * 单例
     */
    private static HeroAircraft singleton;

    /**
     * 使用静态代码块的方式，在类加载时初始化单例（饿汉式）
     */
    static {
        int locationX = Main.WINDOW_WIDTH / 2;
        int locationY = Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight();
        int speedX = 0;
        int speedY = 0;
        int hp = 100;
        singleton = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
    }

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
    private int shootNum = 1;     //子弹一次发射数量
    private int power = 30;       //子弹伤害
    private int direction = -1;  //子弹射击方向 (向上发射：1，向下发射：-1)

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
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction * 5;
        BaseBullet baseBullet;
        for (int i = 0; i < shootNum; i++) {
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            baseBullet = new HeroBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX, speedY, power);
            res.add(baseBullet);
        }
        return res;
    }

    /**
     * 英雄机碰到回复HP道具时增加HP
     * @param increase
     */
    public void increaseHp(int increase){
        //如果传入的increase不合法，直接返回
        if (increase < 0) return;
        //防止HP溢出
        if (hp >= maxHp - increase) hp = maxHp;
        else hp += increase;
    }

}
