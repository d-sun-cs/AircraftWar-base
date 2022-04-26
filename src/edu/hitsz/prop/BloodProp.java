package edu.hitsz.prop;

import edu.hitsz.application.MusicThread;

/**
 * @author SunDocker
 */
public class BloodProp extends AbstractProp {
    /**
     * 加血道具随机生成时需要用到的常量
     */
    public static final int CHOICE = 1;

    public BloodProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void vanish(boolean needMusic) {
        super.vanish();
        //播放游戏supply音频
        if (needMusic) {
            new MusicThread("src/videos/get_supply.wav").start();
        }
    }


}
