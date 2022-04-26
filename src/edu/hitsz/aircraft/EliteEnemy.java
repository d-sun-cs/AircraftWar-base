package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.PropFactory;
import edu.hitsz.factory.impl.BloodPropFactory;
import edu.hitsz.factory.impl.BombPropFactory;
import edu.hitsz.factory.impl.BulletPropFactory;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BloodProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.BulletProp;

import java.util.List;

/**
 * @author SunDocker
 */
public class EliteEnemy extends MobEnemy {
    /**
     * 精英敌机产生概率的倒数，默认是5
     */
    public static int probability = 5;
    /**
     * 攻击方式——子弹一次发射数量
     */
    private int shootNum = 1;
    /**
     * 攻击方式——子弹伤害
     */
    private int power = 20;
    /**
     * 攻击方式——子弹射击方向 (向上发射：1，向下发射：-1)
     */
    private int direction = 1;

    private PropFactory propFactory;

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    /**
     * 通过射击产生子弹
     * (之后要将该方法移交给一个射击策略类)
     *
     * @return 射击出的子弹List
     */
    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(EnemyBullet.class, locationX, locationY, direction, power, speedY, shootNum);
    }

    /**
     * 掉落道具
     * 之所以在这个方法里new工厂，是因为这个工厂一定是只被使用一次的，
     * 因为掉落道具意味着敌机已经坠毁了
     * @return 道具
     */
    @Override
    public AbstractProp produceProp() {
        long rand = System.currentTimeMillis();
        //随机产生三种道具之一
        //75%的概率产生道具（现在设置太低了不好测试）
        if (rand % AbstractProp.PROBABILITY == BloodProp.CHOICE) {
            propFactory = new BloodPropFactory();
        } else if (rand % AbstractProp.PROBABILITY == BombProp.CHOICE) {
            propFactory = new BombPropFactory();
        } else if (rand % AbstractProp.PROBABILITY == BulletProp.CHOICE) {
            propFactory = new BulletPropFactory();
        } else {
            //返回null代表没掉落道具
            return null;
        }
        return propFactory.createProp(locationX, locationY);
    }
}
