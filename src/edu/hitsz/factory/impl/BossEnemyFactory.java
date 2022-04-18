package edu.hitsz.factory.impl;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.factory.EnemyFactory;
import edu.hitsz.strategy.ScatteringShootStrategy;

public class BossEnemyFactory implements EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        AbstractAircraft boss = new BossEnemy(Main.WINDOW_WIDTH / 2, ImageManager.BOSS_IMAGE.getHeight(),
                0, 0, 600);
        boss.setShootStrategy(new ScatteringShootStrategy());
        return boss;
    }
}
