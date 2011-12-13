/*
 * Created on 12.03.2008
 */
package eionet.gdem.conversion;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.dcm.remote.ListConversionsResult;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * This unittest tests the Conversion Service listConversions method
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS ListConversionsMethodTest
 */

public class ListConversionsMethodTest extends DBTestCase {

    /**
     * Provide a connection to the database.
     */
    public ListConversionsMethodTest(String name) {
        super(name);
        DbHelper.setUpConnectionProperties();
    }

    /**
     * Set up test case properties
     */
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.setUpProperties(this);
    }

    /**
     * Load the data which will be inserted for the test
     */
    protected IDataSet getDataSet() throws Exception {
        IDataSet loadedDataSet =
                new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_CONVERSIONS_XML));
        return loadedDataSet;
    }

    /**
     * Tests that the result of listConversions method contains the right data as defined in seed xml file.
     */
    public void testListConversions() throws Exception {

        ConversionService cs = new ConversionService();
        // get all conversions
        Vector v = cs.listConversions();
        // we don't know the exact number of schemas will be returned, because DD stylesheets has been created dynamically
        assertTrue(v.size() > 83);
    }

    /**
     * Tests that the result of listConversions method contains the right data as defined in seed xml file.
     */
    public void testListConversionsWithSchema() throws Exception {
        // get conversions for 1 schema
        ConversionService cs = new ConversionService();
        String schema = "http://waste.eionet.europa.eu/schemas/waste/schema.xsd";
        Vector v = cs.listConversions(schema);
        assertEquals(3, v.size());

        // analyze the conversion hashtable at index 0
        Hashtable h = (Hashtable) v.get(0);
        String convert_id = (String) h.get(ListConversionsResult.CONVERT_ID_TAG);
        String xsl = (String) h.get(ListConversionsResult.XSL_TAG);
        String content_type_out = (String) h.get(ListConversionsResult.CONTENT_TYPE_TAG);
        String result_type = (String) h.get(ListConversionsResult.RESULT_TYPE_TAG);
        String xml_schema = (String) h.get(ListConversionsResult.XML_SCHEMA_TAG);

        assertEquals("169", convert_id);
        assertEquals("dir75442_excel.xsl", xsl);
        assertEquals("application/vnd.ms-excel", content_type_out);
        assertEquals("EXCEL", result_type);
        assertEquals(schema, xml_schema);

        // analyze the conversion hashtable at index 1
        Hashtable h2 = (Hashtable) v.get(1);
        String convert_id2 = (String) h2.get(ListConversionsResult.CONVERT_ID_TAG);
        String xsl2 = (String) h2.get(ListConversionsResult.XSL_TAG);
        String content_type_out2 = (String) h2.get(ListConversionsResult.CONTENT_TYPE_TAG);
        String result_type2 = (String) h2.get(ListConversionsResult.RESULT_TYPE_TAG);
        String xml_schema2 = (String) h2.get(ListConversionsResult.XML_SCHEMA_TAG);

        assertEquals("171", convert_id2);
        assertEquals("dir75442_html.xsl", xsl2);
        assertEquals("text/html;charset=UTF-8", content_type_out2);
        assertEquals("HTML", result_type2);
        assertEquals(schema, xml_schema2);
    }

    /**
     * Tests that the result of getXMLSchemas method contains the right data as defined in seed xml file.
     */
    public void testGetXMLSchemas() throws Exception {

        ConversionService cs = new ConversionService();
        // test getXMLSchemas method, that is part of ListConversionsMethod class
        List schemas = cs.getXMLSchemas();
        // we don't know the exact number of schemas will be returned, because DD stylesheets has been created dynamically
        assertTrue(schemas.size() > 36);
    }

}
