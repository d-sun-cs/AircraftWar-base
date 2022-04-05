package edu.hitsz.factory.impl;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.factory.EnemyFactory;

public class EliteEnemyFactory implements EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        int locationX = (int) (Math.random() *
                (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())
                + ImageManager.ELITE_ENEMY_IMAGE.getWidth() / 2);
        int locationY = (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2);
        int speedX = System.currentTimeMillis() % 2 == 0 ? 3 : -3;
        int speedY = 10;
        int hp = 30;
        return new EliteEnemy(locationX, locationY, speedX, speedY, hp);
    }
}
