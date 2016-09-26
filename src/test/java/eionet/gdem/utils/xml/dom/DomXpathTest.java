package eionet.gdem.utils.xml.dom;

import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XPathQuery;
import eionet.gdem.utils.xml.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.stream.StreamSource;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests XPath methods on DOM.
 * @author George Sofianos
 */
public class DomXpathTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getElementValuesTest() throws XmlException {
        IXmlCtx context = new DomContext();
        context.checkFromString("<xml><test>111</test><test>222</test></xml>");
        XPathQuery xpath = context.getQueryManager();
        List<String> list = xpath.getElementValues("test");
        assertEquals("Wrong list size", 2, list.size());
    }

}