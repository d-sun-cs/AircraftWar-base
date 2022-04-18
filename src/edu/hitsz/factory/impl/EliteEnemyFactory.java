package edu.hitsz.factory.impl;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.factory.EnemyFactory;
import edu.hitsz.strategy.ScatteringShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;

public class EliteEnemyFactory implements EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        int locationX = (int) (Math.random() *
                (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())
                + ImageManager.ELITE_ENEMY_IMAGE.getWidth() / 2);
        int locationY = (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2);
        int speedX = System.currentTimeMillis() % 2 == 0 ? 3 : -3;
        int speedY = 8;
        int hp = 60;
        EliteEnemy eliteEnemy = new EliteEnemy(locationX, locationY, speedX, speedY, hp);
        eliteEnemy.setShootStrategy(new StraightShootStrategy());
        return eliteEnemy;
    }
}
