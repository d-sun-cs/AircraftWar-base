package edu.hitsz.prop;


import edu.hitsz.application.MusicThread;

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

    @Override
    public void vanish(boolean needMusic) {
        super.vanish();
        //播放游戏bomb_explosion音频
        if (needMusic) {
            new MusicThread("src/videos/bomb_explosion.wav").start();
        }
    }


}
