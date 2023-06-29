/*
 * Created on 18.04.2008
 */
package eionet.gdem.validation;

import eionet.gdem.exceptions.DCMException;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS InputAnalyserTest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class InputAnalyserTest {

    /**
     * Test if the method is able to extract XML schema from the XML file
     * 
     * @throws Exception
     */
    @Test
    public void testSchemaFinder() throws Exception {
        InputAnalyser analyser = new InputAnalyser();
        analyser.parseXML(TestUtils.getSeedURL(TestConstants.SEED_GW_VALID_XML, this));

        assertEquals(analyser.getNamespace(), "http://dd.eionet.europa.eu/namespace.jsp?ns_id=8");
        assertEquals(analyser.getRootElement(), "GeneralCharacterisation");

        assertEquals(analyser.getSchemas().get(0), "http://dd.eionet.europa.eu/GetSchema?id=TBL4564");
    }

    /**
     * Tests if xmlconv is able to find schema from xsi:schemaLocation
     * @throws Exception
     */
    @Test
    public void testSchemaLocationFinder() throws DCMException {
        InputAnalyser analyzer = new InputAnalyser();
        analyzer.parseXML(TestUtils.getSeedURL(TestConstants.AQD_SCHEMALOCATION, this));
        assertEquals(analyzer.getSchemas().get(0), "http://dd.eionet.europa.eu/schemas/id2011850eu-1.0/AirQualityReporting.xsd");
    }
}
