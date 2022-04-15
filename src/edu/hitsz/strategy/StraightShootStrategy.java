package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

/**
 * @author SunDocker
 */
public class StraightShootStrategy implements ShootStrategy {
    @Override
    public List<BaseBullet> shoot(Class<? extends BaseBullet> bulletClass, int locationX, int locationY, int direction, int power, int speedY) {
        List<BaseBullet> res = new LinkedList<>();
        int x = locationX;
        int y = locationY + direction * 2;
        int speedX = 0;
        speedY = speedY + direction * 6;
        //for (int i = 0; i < 3; i++, speedX += 5) {
        // 子弹发射位置相对飞机位置向前偏移
        // 多个子弹横向分散
        Constructor<? extends BaseBullet> constructor = null;
        BaseBullet baseBullet = null;
        try {
            constructor = bulletClass.getDeclaredConstructor(int.class, int.class, int.class, int.class, int.class);
            baseBullet = constructor.newInstance(x, y, speedX, speedY, power);
        } catch (Exception e) {
            e.printStackTrace();
        }
        res.add(baseBullet);
        //}
        return res;
    }
}
