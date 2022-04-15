package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.ShootStrategy;

import java.util.List;

/**
 * 所有种类飞机的抽象父类：
 * 敌机（BOSS, ELITE, MOB），英雄飞机
 *
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {
    /**
     * 生命值
     */
    protected int maxHp;
    protected int hp;

    /**
     * 射击策略
     */
    protected ShootStrategy shootStrategy;

    public void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    public void decreaseHp(int decrease){
        //如果传入的decrease不合法，直接返回
        if (decrease < 0) {
            return;
        }
        hp -= decrease;
        if(hp <= 0){
            hp=0;
            vanish();
        }
    }

    public int getHp() {
        return hp;
    }


    /**
     * 飞机射击方法，可射击对象必须实现
     * @return
     *  可射击对象需实现，返回子弹
     *  非可射击对象空实现，返回空集合(不是特别符合接口隔离原则，待改进)
     */
    public abstract List<BaseBullet> shoot();

    /**
     * 掉落道具方法
     * @return
     * 敌机掉落道具
     * 英雄机空实现，返回null(不是特别符合接口隔离原则，待改进)
     */
    public abstract AbstractProp produceProp();

    /**
     * 观察者模式中的更新方法（之后实验再实现）
     */
    //public abstract void update();

}


