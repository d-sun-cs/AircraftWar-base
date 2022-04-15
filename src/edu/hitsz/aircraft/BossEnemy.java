package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;

import java.util.List;

public class BossEnemy extends AbstractAircraft{
    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(EnemyBullet.class, locationX, locationY, 1, 20, 0);
    }

    @Override
    public AbstractProp produceProp() {
        return null;
    }
}
