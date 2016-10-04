package eionet.gdem.utils.xml;

import eionet.gdem.utils.xml.dom.XmlCommon;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests XML parsing
 * @author George Sofianos
 */
public class XmlCommonTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void checkFromString() throws Exception {
        XmlCommon common = new XmlCommon();
        common.checkFromString("<div>test</div>");
    }

    @Test
    public void checkMalformedXmlFromString() throws Exception {
        exception.expect(XmlException.class);
        XmlCommon common = new XmlCommon();
        common.checkFromString("<div>test<div>");
    }

}