package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.EnemyFactory;
import edu.hitsz.factory.impl.EliteEnemyFactory;
import edu.hitsz.factory.impl.MobEnemyFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.List;

class HeroAircraftTest {

    static HeroAircraft heroAircraft;
    static EnemyFactory enemyFactory;

    AbstractAircraft enemy;
    BaseBullet bullet;

    @BeforeAll
    static void beforeAll() {
        heroAircraft = HeroAircraft.getInstance();
        enemyFactory = new MobEnemyFactory();
    }

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        heroAircraft.setLocation(300, 600);
        enemy = enemyFactory.createEnemy();
        bullet = new EnemyBullet(0, 0, 0, 0, 0);
    }

    @DisplayName("Test heroAircraft decreaseHp Method")
    @ParameterizedTest
    @ValueSource(ints = {10, 20, -40, -10, 100, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void decreaseHp(int deltaHp) {
        int beforeHp = heroAircraft.getHp();
        heroAircraft.decreaseHp(deltaHp);
        int afterHp = heroAircraft.getHp();
        if (deltaHp < 0) {
            Assertions.assertEquals(beforeHp, afterHp);
        } else {
            Assertions.assertEquals(Math.max(beforeHp - deltaHp, 0), afterHp);
        }

    }


    @DisplayName("Test heroAircraft crash method")
    @ParameterizedTest
    @CsvSource({"300, 600, 300, 600", "290, 610, 290, 610", "310, 590, 310, 590"})
    void crash(int enemyX, int enemyY, int bulletX, int bulletY) {

        enemy.setLocation(enemyX, enemyY);
        bullet.setLocation(bulletX, bulletY);

        Assumptions.assumeTrue(heroAircraft.crash(enemy));
        Assumptions.assumeTrue(heroAircraft.crash(bullet));
    }

}