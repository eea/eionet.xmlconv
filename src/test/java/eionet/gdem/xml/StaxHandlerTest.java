package eionet.gdem.xml;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;

/**
 * Tests methods for the StAX handler for xml files.
 * @author George Sofianos
 */
public class StaxHandlerTest {
    @Test
    public void parseString() throws Exception {
        StaxHandler stax = new StaxHandler();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean result = stax.parseString("<test></test>", out);
        System.out.println(out.toByteArray());
        assertEquals("Error occured", true, result);
    }

}