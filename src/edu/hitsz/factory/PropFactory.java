package edu.hitsz.factory;

import edu.hitsz.prop.AbstractProp;

public interface PropFactory {
    AbstractProp createProp(int locationX, int locationY);
}
