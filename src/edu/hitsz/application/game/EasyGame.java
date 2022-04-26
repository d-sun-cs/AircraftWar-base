package edu.hitsz.application.game;

/**
 * @author SunDocker
 */
public class EasyGame extends Game {
    private boolean canEnemyShootFlag = false;

    @Override
    protected void increaseDifficulty() {
        System.out.println("简单模式不随时间增加游戏难度");
    }

    /**
     * 简单模式下精英敌机的射击频率减半
     * @return
     */
    @Override
    protected boolean canEnemyShoot() {
        return canEnemyShootFlag = !canEnemyShootFlag;
    }

    /**
     * 简单模式不产生boss
     */
    @Override
    protected void scoreCheck() {}
}
