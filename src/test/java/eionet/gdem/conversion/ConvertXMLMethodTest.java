/*
 * Created on 17.03.2008
 */
package eionet.gdem.conversion;

import java.util.Hashtable;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.utils.Utils;

/**
 * This unittest tests the Conversion Service convert method
 *
 * @author Enriko Käsper, TietoEnator Estonia AS ConvertXmlMethodTest
 */

public class ConvertXMLMethodTest extends DBTestCase {

    /**
     * Provide a connection to the database.
     */
    public ConvertXMLMethodTest(String name) {
        super(name);
        DbHelper.setUpConnectionProperties();
    }

    /**
     * Set up test case properties
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.setUpProperties(this);
    }

    /**
     * Load the data which will be inserted for the test
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet loadedDataSet =
                new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_CONVERSIONS_XML));
        return loadedDataSet;
    }

    /**
     * Tests convert method - validate the result file and metadata( content type and file name)
     */
    public void testConvert() throws Exception {
        ConversionService cs = new ConversionService();
        Hashtable h = cs.convert(TestUtils.getSeedURL(TestConstants.SEED_GENERAL_REPORT_XML, this), "168");

        // test if the returned hastable contains all the keys and correct values
        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, (String) h.get(ConvertXMLMethod.CONTENTTYPE_KEY));
        assertEquals(TestConstants.GR_HTML_FILENAME_RESULT, (String) h.get(ConvertXMLMethod.FILENAME_KEY));
        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the converion result contains some text from seed..xml file
        assertTrue(strContent.indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Tests convert method with generated DD stylehseets - validate the result file and metadata( content type and file name)
     */
    public void testConvertDDTableHTML() throws Exception {

        ConversionService cs = new ConversionService();
        Hashtable h = cs.convert(TestUtils.getSeedURL(TestConstants.SEED_OZONE_STATION_XML, this), "DD_TBL3453_CONV5");

        // test if the returned hastable contains all the keys and correct values
        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, (String) h.get(ConvertXMLMethod.CONTENTTYPE_KEY));
        assertEquals(TestConstants.OZ_HTML_FILENAME_RESULT, (String) h.get(ConvertXMLMethod.FILENAME_KEY));
        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the converion result contains some text from seed..xml file
        assertTrue(strContent.indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Tests convert method with generated DD stylehseets - validate the result file and metadata( content type and file name)
     */
    public void testConvertDDTableSQL() throws Exception {
        ConversionService cs = new ConversionService();
        Hashtable h = cs.convert(TestUtils.getSeedURL(TestConstants.SEED_OZONE_STATION_XML, this), "DD_TBL3453_CONV1");

        // test if the returned hastable contains all the keys and correct values
        assertEquals(TestConstants.TEXT_CONTENTYPE_RESULT, (String) h.get(ConvertXMLMethod.CONTENTTYPE_KEY));
        assertEquals(TestConstants.OZ_SQL_FILENAME_RESULT, (String) h.get(ConvertXMLMethod.FILENAME_KEY));
        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the converion result contains some text from seed..xml file
        assertTrue(strContent.indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Tests convertPush method with XML file. Validate the result file and metadata( content type and file name)
     */
    public void testConvertPush() throws Exception {
        ConversionService cs = new ConversionService();
        byte[] bytes = Utils.fileToBytes(getClass().getClassLoader().getResource(TestConstants.SEED_GENERAL_REPORT_XML).getFile());
        Hashtable h = cs.convertPush(bytes, "168", TestConstants.GR_HTML_FILENAME_RESULT);

        // test if the returned hastable contains all the keys and correct values
        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, (String) h.get(ConvertXMLMethod.CONTENTTYPE_KEY));
        assertEquals(TestConstants.GR_HTML_FILENAME_RESULT, (String) h.get(ConvertXMLMethod.FILENAME_KEY));
        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the converion result contains some text from seed..xml file
        assertTrue(strContent.indexOf(TestConstants.STRCONTENT_RESULT) > 0);

    }

    /**
     * Tests convertPush method with ZIP file. Validate the result file and metadata( content type and file name)
     */
    public void testConvertPushZip() throws Exception {
        ConversionService cs = new ConversionService();
        byte[] bytes = Utils.fileToBytes(getClass().getClassLoader().getResource(TestConstants.SEED_GENERAL_REPORT_ZIP).getFile());
        Hashtable h = cs.convertPush(bytes, "168", "seed-general-report.html");

        // test if the returned hastable contains all the keys and correct values
        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, (String) h.get(ConvertXMLMethod.CONTENTTYPE_KEY));
        assertEquals(TestConstants.GR_HTML_FILENAME_RESULT, (String) h.get(ConvertXMLMethod.FILENAME_KEY));
        byte[] content = (byte[]) h.get(ConvertXMLMethod.CONTENT_KEY);
        String strContent = new String(content, "UTF-8");
        // test if the converion result contains some text from seed..xml file
        assertTrue(strContent.indexOf(TestConstants.STRCONTENT_RESULT) > 0);

    }

    /**
     * Tests convertPush method with XML file. Validate if the URL from file name is in the result of HTML.
     */
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
