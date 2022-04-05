package edu.hitsz.prop;

import edu.hitsz.factory.PropFactory;
import edu.hitsz.factory.impl.BloodPropFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class BloodPropTest {
    static PropFactory propFactory;
    AbstractProp bloodProp;

    @BeforeAll
    static void beforeAll() {
        propFactory = new BloodPropFactory();
    }

    @BeforeEach
    void setUp() {
        bloodProp = propFactory.createProp(200, 400);
    }

    @Test
    void forward() {
        int beforeX = bloodProp.getLocationX();
        int beforeY = bloodProp.getLocationY();
        bloodProp.forward();
        int afterX = bloodProp.getLocationX();
        int afterY = bloodProp.getLocationY();
        Assertions.assertEquals(beforeX, afterX);
        Assertions.assertTrue(afterY > beforeY);
    }

    @Test
    void vanish() {
        bloodProp.vanish();
        Assertions.assertTrue(bloodProp.notValid());
    }
}