package edu.hitsz.prop;

import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;

/**
 * @author SunDocker
 */
public abstract class AbstractProp extends AbstractFlyingObject {
    /**
     * 产生道具的概率为：3 / PROBABILITY
     */
    public static final int PROBABILITY = 6;

    //观察者集合（后面实验再用）
    //protected List<FlyingObject> observers;

    public AbstractProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }
    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    /**
     * 观察者模式的通知方法
     * （现在还没实现观察者模式，只是定义方法）
     */
    //public abstract void notifyObservers();
}
