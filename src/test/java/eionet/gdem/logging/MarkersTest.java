package eionet.gdem.logging;

import eionet.gdem.test.AssertUtils;
import org.junit.Test;


import java.lang.reflect.InvocationTargetException;
import static org.junit.Assert.*;

/**
 * @author George Sofianos
 */
public class MarkersTest {

    @Test
    public void finalClassTest() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        AssertUtils.assertUtilityClassWellDefined(Markers.class);
    }

}