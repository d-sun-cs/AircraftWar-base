package edu.hitsz.prop;

/**
 * @author SunDocker
 */
public class BulletProp extends AbstractProp{
    /**
     * 子弹道具随机生成时需要用到的常量
     */
    public static final int CHOICE = 3;
    public BulletProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }


}
