package edu.hitsz.factory.impl;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.factory.EnemyFactory;

public class EliteEnemyFactory implements EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        int locationX = (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth()) + ImageManager.MOB_ENEMY_IMAGE.getWidth() / 2) * 1;
        int locationY = (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2) * 1;
        int speedX = 0;
        int speedY = 10;
        int hp = 30;
        return new EliteEnemy(locationX, locationY, speedX, speedY, hp);
    }
}
