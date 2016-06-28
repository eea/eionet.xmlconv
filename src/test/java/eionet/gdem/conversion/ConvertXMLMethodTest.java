/*
 * Created on 17.03.2008
 */
package eionet.gdem.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;

import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.utils.Utils;

import javax.sql.DataSource;

/**
 * This unittest tests the Conversion Service convert method.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS ConvertXmlMethodTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ConvertXMLMethodTest{

    @Autowired
    private DataSource db;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_CONVERSIONS_XML);
    }


    /**
     * Tests convert method - validate the result file and metadata( content type and file name)
     */
    @Test
    public void testConvert() throws Exception {
        ConversionService cs = new ConversionService();
        Hashtable h = cs.convert(TestUtils.getSeedURL(TestConstants.SEED_GENERAL_REPORT_XML, this), "168");

        // test if the returned hastable contains all the keys and correct values
        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, h.get(ConvertXMLMethod.CONTENTTYPE_KEY));
        assertEquals(TestConstants.GR_HTML_FILENAME_RESULT, h.get(ConvertXMLMethod.FILENAME_KEY));
        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the converion result contains some text from seed..xml file
        assertTrue(strContent.indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Tests convert method with generated DD stylehseets - validate the result file and metadata( content type and file name)
     */
    @Test
    public void testConvertDDTableHTML() throws Exception {

        ConversionService cs = new ConversionService();
        Hashtable h = cs.convert(TestUtils.getSeedURL(TestConstants.SEED_OZONE_STATION_XML, this), "DD_TBL3453_CONV5");

        // test if the returned hastable contains all the keys and correct values
        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, h.get(ConvertXMLMethod.CONTENTTYPE_KEY));
        assertEquals(TestConstants.OZ_HTML_FILENAME_RESULT, h.get(ConvertXMLMethod.FILENAME_KEY));
        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the converion result contains some text from seed..xml file
        assertTrue(strContent.indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Tests convert method with generated DD stylehseets - validate the result file and metadata( content type and file name)
     */
    @Test
    public void testConvertDDTableSQL() throws Exception {
        ConversionService cs = new ConversionService();
        Hashtable h = cs.convert(TestUtils.getSeedURL(TestConstants.SEED_OZONE_STATION_XML, this), "DD_TBL3453_CONV1");

        // test if the returned hastable contains all the keys and correct values
        assertEquals(TestConstants.TEXT_CONTENTYPE_RESULT, h.get(ConvertXMLMethod.CONTENTTYPE_KEY));
        assertEquals(TestConstants.OZ_SQL_FILENAME_RESULT, h.get(ConvertXMLMethod.FILENAME_KEY));
        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the converion result contains some text from seed..xml file
        assertTrue(strContent.indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Tests convertPush method with XML file. Validate the result file and metadata( content type and file name)
     */
    @Test
    public void testConvertPush() throws Exception {
        ConversionService cs = new ConversionService();
        byte[] bytes = Utils.fileToBytes(getClass().getClassLoader().getResource(TestConstants.SEED_GENERAL_REPORT_XML).getFile());
        Hashtable h = cs.convertPush(bytes, "168", TestConstants.GR_HTML_FILENAME_RESULT);

        // test if the returned hastable contains all the keys and correct values
        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, h.get(ConvertXMLMethod.CONTENTTYPE_KEY));
        assertEquals(TestConstants.GR_HTML_FILENAME_RESULT, h.get(ConvertXMLMethod.FILENAME_KEY));
        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the converion result contains some text from seed..xml file
        assertTrue(strContent.indexOf(TestConstants.STRCONTENT_RESULT) > 0);

    }

    /**
     * Tests convertPush method with ZIP file. Validate the result file and metadata( content type and file name)
     */
    @Test
    public void testConvertPushZip() throws Exception {
        ConversionService cs = new ConversionService();
        byte[] bytes = Utils.fileToBytes(getClass().getClassLoader().getResource(TestConstants.SEED_GENERAL_REPORT_ZIP).getFile());
        Hashtable h = cs.convertPush(bytes, "168", "seed-general-report.html");

        // test if the returned hastable contains all the keys and correct values
        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, h.get(ConvertXMLMethod.CONTENTTYPE_KEY));
        assertEquals(TestConstants.GR_HTML_FILENAME_RESULT, h.get(ConvertXMLMethod.FILENAME_KEY));
        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the converion result contains some text from seed..xml file
        assertTrue(strContent.indexOf(TestConstants.STRCONTENT_RESULT) > 0);

    }

    /**
     * Tests convertPush method with XML file. Validate if the URL from file name is in the result of HTML.
     */
    @Test
    public void testConvertPushWithURLinFileName() throws Exception {
        String envelopeUrl = "http://cdrtest.eionet.europa.eu/envelope";
        ConversionService cs = new ConversionService();
        byte[] bytes = Utils.fileToBytes(getClass().getClassLoader().getResource(TestConstants.SEED_GENERAL_REPORT_XML).getFile());
        Hashtable<String, Object> h = cs.convertPush(bytes, "168", envelopeUrl + "/" + TestConstants.GR_HTML_FILENAME_RESULT);

        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the conversion result contains envelope URL passed with parameter.
        assertTrue(strContent.indexOf("envelope: " + envelopeUrl) > 0);

    }
}
