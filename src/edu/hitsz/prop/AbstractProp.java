package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author SunDocker
 */
public abstract class AbstractProp extends AbstractFlyingObject {
    /**
     * 产生道具的概率为：3 / PROBABILITY
     */
    public static final int PROBABILITY = 4;

    //观察者集合（后面实验再用）
    protected List<AbstractAircraft> observers = new ArrayList<>();

    public AbstractProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    abstract public void vanish(boolean needMusic);

    /**
     * 观察者模式的通知方法
     * （现在还没实现观察者模式，只是定义方法）
     */
    public void notifyObservers() {
        for (AbstractAircraft observer : observers) {
            observer.update(this.getClass());
        }
    }

    public void subscribe(List<AbstractAircraft> aircrafts) {
        if (Objects.nonNull(aircrafts)) {
            observers.addAll(aircrafts);
        }
    }
    public void subscribe(AbstractAircraft aircraft) {
        if (Objects.nonNull(aircraft)) {
            observers.add(aircraft);
        }
    }

    public void unsubscribe(AbstractAircraft aircraft) {
        if (Objects.nonNull(aircraft)) {
            observers.remove(aircraft);
        }
    }
}
