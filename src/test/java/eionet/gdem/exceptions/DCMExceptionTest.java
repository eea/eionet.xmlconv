package eionet.gdem.exceptions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * DCM Exception Test class.
 */
public class DCMExceptionTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testExceptionConstructors() {
        new DCMException("testCode", "testMessage" );
        new DCMException("testCode");
    }
    @Test
    public void testErrorCode() {
        Exception test = new DCMException("testCode", "testMessage");
        String errorCode = ((DCMException) test).getErrorCode();
        assertEquals("Wrong Code", "testCode", errorCode);
    }

}