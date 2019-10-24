package eionet.gdem.utils.xml;

import org.exolab.castor.dsml.XML;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
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

    @Test
    public void testRemoveEmptyElements(){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Characterisation xmlns=\"http://dd.eionet.europa.eu/namespaces/928\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://dd.eionet.europa.eu/namespaces/928  http://dd.eionet.europa.eu/v2/dataset/3442/schema-tbl-11542.xsd\"><Row><season>2018</season><bathingWaterIdentifier>IT001001013002</bathingWaterIdentifier><groupIdentifier></groupIdentifier><qualityClass>1</qualityClass><geographicalConstraint>false</geographicalConstraint><link>http://www.portaleacque.salute.gov.it/PortaleAcquePubblico/rest/download/sintesi/4360</link><Remarks>Bathing water area changed the latitude from 45.0642 Bathing water area changed the longitude from 7.3806</Remarks></Row></Characterisation>";
        String result = XMLUtils.removeEmptyElements(xml);
        String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Characterisation xmlns=\"http://dd.eionet.europa.eu/namespaces/928\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://dd.eionet.europa.eu/namespaces/928  http://dd.eionet.europa.eu/v2/dataset/3442/schema-tbl-11542.xsd\"><Row><season>2018</season><bathingWaterIdentifier>IT001001013002</bathingWaterIdentifier><qualityClass>1</qualityClass><geographicalConstraint>false</geographicalConstraint><link>http://www.portaleacque.salute.gov.it/PortaleAcquePubblico/rest/download/sintesi/4360</link><Remarks>Bathing water area changed the latitude from 45.0642 Bathing water area changed the longitude from 7.3806</Remarks></Row></Characterisation>";
        assertThat(result,equalTo(expectedResult));
    }

}