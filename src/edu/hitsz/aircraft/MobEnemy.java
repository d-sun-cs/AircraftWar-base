package edu.hitsz.aircraft;

import edu.hitsz.application.Game;
import edu.hitsz.bullet.Bullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft {
    /**
     * 获得敌机分数，击毁敌机时，调用该方法获得分数。
     * @return 敌机的分数
     */
    public int score() {
        return 10;
    }

    private static final List<Bullet> EMPTY_BULLETS = new LinkedList<>();

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Game.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public List<Bullet> shoot() {
        return EMPTY_BULLETS;
    }
}
