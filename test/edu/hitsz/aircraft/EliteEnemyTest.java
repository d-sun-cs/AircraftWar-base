package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.factory.EnemyFactory;
import edu.hitsz.factory.impl.EliteEnemyFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EliteEnemyTest {
    static EnemyFactory enemyFactory;
    AbstractAircraft eliteEnemy;

    @BeforeAll
    static void beforeAll() {
        enemyFactory = new EliteEnemyFactory();
    }
    @BeforeEach
    void setUp() {
        eliteEnemy = enemyFactory.createEnemy();
    }


    @DisplayName("Test EliteEnemy forward method")
    @Test
    void forward() {
        int beforeY = eliteEnemy.getLocationY();
        for (int i = 0; i < 10000; i++) {
            eliteEnemy.forward();
        }
        int afterX = eliteEnemy.getLocationX();
        int afterY = eliteEnemy.getLocationY();
        Assertions.assertTrue(afterY > beforeY);
        Assertions.assertTrue(afterX < Main.WINDOW_WIDTH);
        Assertions.assertTrue(afterX > 0);
    }

    @DisplayName("Test EliteEnemy shoot method")
    @Test
    void shoot() {
        List<BaseBullet> bullets = eliteEnemy.shoot();
        Assertions.assertNotNull(bullets);
        bullets.forEach(bullet -> Assertions.assertTrue(bullet.getSpeedY() > 0));
    }
}