package edu.hitsz.factory.impl;

import edu.hitsz.factory.PropFactory;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;

public class BombPropFactory implements PropFactory {
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        int speedX = 0;
        int speedY = 1;
        return new BombProp(locationX, locationY, speedX, speedY);
    }
}
