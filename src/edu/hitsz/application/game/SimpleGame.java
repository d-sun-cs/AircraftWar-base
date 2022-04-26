package edu.hitsz.application.game;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.application.ImageManager;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author SunDocker
 */
public class SimpleGame extends Game {
    private boolean hasChangedBackground = false;

    @Override
    protected void increaseDifficulty() {
        System.out.println("普通模式提升难度");
        System.out.println("敌机数量最大值加1");
        enemyMaxNumber++;
        System.out.println("精英敌机产生概率增加(不超过三分之一)");
        EliteEnemy.probability = EliteEnemy.probability > 2 ? EliteEnemy.probability - 1 : EliteEnemy.probability;
    }

    @Override
    protected void scoreCheck() {
        if (score >= step) {
            //没有boss则产生boss，已经有boss则刷新boss
            if (Objects.nonNull(boss)) {
                boss.vanish();
                System.out.println("boss刷新");
            }
            boss = bossEnemyFactory.createEnemy();
            boss.setShootNum(3);
            System.out.println("boss产生，普通模式不改变血量");
            step += step;
        }
        //分数到达2400后，切换背景
        if (score >= 1200 && !hasChangedBackground) {
            hasChangedBackground = true;
            System.out.println("切换背景");
            try {
                ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg3.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 相比于简单模式，普通模式每个敌机的血量提升10，Y速度提升2
     *
     * @param enemy
     */
    @Override
    protected void increaseEnemyProperty(AbstractAircraft enemy) {
        enemy.increaseHp(10);
        enemy.increaseSpeedY(2);
    }


}
