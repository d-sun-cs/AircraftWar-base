package edu.hitsz.factory.impl;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.factory.EnemyFactory;

public class BossEnemyFactory implements EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        return null;
    }
}
