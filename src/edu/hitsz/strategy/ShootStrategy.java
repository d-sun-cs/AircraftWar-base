package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;

import java.util.List;

/**
 * @author SunDocker
 */
public interface ShootStrategy {
    /**
     * 射击策略
     * @param bulletClass
     * @param locationX 子弹X坐标
     * @param locationY 子弹Y坐标
     * @param direction 子弹方向
     * @param power 子弹威力
     * @param speedY 子弹Y方向初始速度
     * @return
     */
    List<BaseBullet> shoot(Class<? extends BaseBullet> bulletClass,
                           int locationX, int locationY,
                           int direction, int power, int speedY, int shootNum);
}
