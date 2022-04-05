package edu.hitsz.prop;


/**
 * @author SunDocker
 */
public class BombProp extends AbstractProp {
    /**
     * 炸弹道具随机生成时需要用到的常量
     */
    public static final int CHOICE = 2;

    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }


}
