package eionet.gdem.utils.xml.tiny;

import eionet.gdem.utils.xml.XmlException;
import net.sf.saxon.s9api.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests XPath methods on Tiny Tree.
 * @author George Sofianos
 */
public class TinyTreeXpathTest {

    private Processor processor;
    private StreamSource source;

    @Before
    public void setUp() throws Exception {
        processor = new Processor(false);
    }
    @Test
    public void getElementListTest() throws SaxonApiException, XmlException {
        StringReader reader = new StringReader("<root><xml>1</xml><xml>2</xml></root>");
        source = new StreamSource(reader);
        XdmNode root = processor.newDocumentBuilder().build(source);
        TinyTreeXpath tree = new TinyTreeXpath(processor, root);
        List list = tree.getElementValues("xml");
        assertEquals("Wrong element count", 2, list.size());
    }

    @Test
    public void getElementValuesTest() throws SaxonApiException, XmlException {
        source = new StreamSource(new StringReader("<root><xml name='test1' values='values1'>1</xml><xml name='test2' values='values2'></xml></root>"));
        XdmNode root = processor.newDocumentBuilder().build(source);
        TinyTreeXpath tree = new TinyTreeXpath(processor, root);
        List<Map<String, String>> list = tree.getElementAttributes("xml");
        assertEquals("Wrong element size", 2, list.size());
        assertEquals("Wrong attribute size", 2, list.get(0).size());
        assertEquals("Wrong attribute value", "values2", list.get(1).get("values"));
    }
}