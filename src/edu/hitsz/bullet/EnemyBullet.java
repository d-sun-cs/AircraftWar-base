package edu.hitsz.bullet;

/**
 * @Author hitsz
 */
public class EnemyBullet extends Bullet{

    public static final int WIDTH=6;
    public static final int HEIGHT=18;

    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

}
