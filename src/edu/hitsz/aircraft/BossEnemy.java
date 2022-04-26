package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;

import java.util.List;

public class BossEnemy extends AbstractAircraft {

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(EnemyBullet.class, locationX, locationY, 1, 20, 0, shootNum);
    }

    @Override
    public AbstractProp produceProp() {
        return null;
    }

    @Override
    public void update(Class<? extends AbstractProp> propClass) {
        if (BombProp.class.equals(propClass)) {
            this.hp -= 100;
        }
    }
}
