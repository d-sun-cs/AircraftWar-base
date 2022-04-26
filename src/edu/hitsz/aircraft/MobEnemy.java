package edu.hitsz.aircraft;

import edu.hitsz.application.game.Game;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    /**
     * 通过射击产生子弹（实际上并没有产生子弹）
     *
     * @return 射击出的子弹List
     */
    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }

    @Override
    public AbstractProp produceProp() {
        return null;
    }

    @Override
    public void update(Class<? extends AbstractProp> propClass) {
        if (BombProp.class.equals(propClass)) {
            this.vanish();
            Game.addScore(20);
        }
    }
}
