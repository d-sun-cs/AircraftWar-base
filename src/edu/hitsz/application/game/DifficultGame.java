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
public class DifficultGame extends Game {
    private int hpIncrement = 100;
    private int bossShootNum = 3;
    private boolean canHeroShootFlag = false;
    private boolean hasChangedBackground = false;

    @Override
    protected void increaseDifficulty() {
        System.out.println("困难模式提升难度");
        System.out.println("敌机数量最大值加2");
        enemyMaxNumber += 2;
        System.out.println("精英敌机产生概率增加(不超过50%)");
        EliteEnemy.probability = EliteEnemy.probability > 2? EliteEnemy.probability - 1: EliteEnemy.probability;
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
            boss.increaseHp(hpIncrement);
            hpIncrement += 100;
            boss.setShootNum(bossShootNum);
            bossShootNum = bossShootNum < 6? bossShootNum + 1: bossShootNum;
            System.out.println("boss产生，困难模式每次产生提升血量，且子弹数散射条数变多(最多6)");
            step += step;
        }
        //分数到达2400后，切换背景
        if (score >= 1200 && !hasChangedBackground) {
            hasChangedBackground = true;
            System.out.println("切换背景");
            try {
                ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg5.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 相比于简单模式，困难模式敌机的血量提升30，Y速度提升4
     * @param enemy
     */
    @Override
    protected void increaseEnemyProperty(AbstractAircraft enemy) {
        enemy.increaseHp(30);
        enemy.increaseSpeedY(4);
    }

    /**
     * 困难模式射击频率降低
     * @return
     */
    @Override
    protected boolean canHeroShoot() {
        return canHeroShootFlag = !canHeroShootFlag;
    }
}
