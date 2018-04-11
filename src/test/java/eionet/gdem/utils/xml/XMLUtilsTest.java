package eionet.gdem.utils.xml;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class XMLUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getAttribute() {
        byte[] xml = "<test test=\"1\"></test>".getBytes();
        String result = XMLUtils.getXpathText(xml, "/test/@test");
        assertEquals("1", result);
    }

    @Test
    public void getEmptyResult() {
        byte[] xml = "<test test=\"1\"></test>".getBytes();
        String result = XMLUtils.getXpathText(xml, "/test/@empty");
        assertEquals("", result);
    }
}