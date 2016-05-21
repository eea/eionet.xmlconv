/*
 * Created on 12.03.2008
 */
package eionet.gdem.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.dcm.remote.ListConversionsResult;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

/**
 * This unittest tests the Conversion Service listConversions method
 *
 * @author Enriko Käsper, TietoEnator Estonia AS ListConversionsMethodTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ListConversionsMethodTest {

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
     * Tests that the result of listConversions method contains the right data as defined in seed xml file.
     */
    @Test
    public void testListConversions() throws Exception {

        ConversionService cs = new ConversionService();
        // get all conversions
        Vector<Hashtable<String, String>> v = cs.listConversions();
        // we don't know the exact number of schemas will be returned, because DD stylesheets has been created dynamically
        assertTrue(v.size() > 83);
    }

    /**
     * Tests that the result of listConversions method contains the right data as defined in seed xml file.
     */
    @Test
    public void testListConversionsWithSchema() throws Exception {
        // get conversions for 1 schema
        ConversionService cs = new ConversionService();
        String schema = "http://waste.eionet.europa.eu/schemas/waste/schema.xsd";
        Vector<Hashtable<String, String>> v = cs.listConversions(schema);
        assertEquals(3, v.size());

        // analyze the conversion hashtable at index 0
        Hashtable<String, String> h = v.get(0);
        String convert_id = h.get(ListConversionsResult.CONVERT_ID_TAG);
        String xsl = h.get(ListConversionsResult.XSL_TAG);
        String content_type_out = h.get(ListConversionsResult.CONTENT_TYPE_TAG);
        String result_type = h.get(ListConversionsResult.RESULT_TYPE_TAG);
        String xml_schema = h.get(ListConversionsResult.XML_SCHEMA_TAG);

        assertEquals("169", convert_id);
        assertEquals("dir75442_excel.xsl", xsl);
        assertEquals("application/vnd.ms-excel", content_type_out);
        assertEquals("EXCEL", result_type);
        assertEquals(schema, xml_schema);

        // analyze the conversion hashtable at index 1
        Hashtable<String, String> h2 = v.get(1);
        String convert_id2 = h2.get(ListConversionsResult.CONVERT_ID_TAG);
        String xsl2 = h2.get(ListConversionsResult.XSL_TAG);
        String content_type_out2 = h2.get(ListConversionsResult.CONTENT_TYPE_TAG);
        String result_type2 = h2.get(ListConversionsResult.RESULT_TYPE_TAG);
        String xml_schema2 = h2.get(ListConversionsResult.XML_SCHEMA_TAG);

        assertEquals("171", convert_id2);
        assertEquals("dir75442_html.xsl", xsl2);
        assertEquals("text/html;charset=UTF-8", content_type_out2);
        assertEquals("HTML", result_type2);
        assertEquals(schema, xml_schema2);
    }

    /**
     * Tests that the result of getXMLSchemas method contains the right data as defined in seed xml file.
     */
    @Test
    public void testGetXMLSchemas() throws Exception {

        ConversionService cs = new ConversionService();
        // test getXMLSchemas method, that is part of ListConversionsMethod class
        List schemas = cs.getXMLSchemas();
        // we don't know the exact number of schemas will be returned, because DD stylesheets has been created dynamically
        assertTrue(schemas.size() > 36);
    }

    /**
     * Test getMapFromConversionObject method which serialises the object into correct Hastable structure.
     */
    @Test
    public void testGetMapFromConversionObject() {
        ListConversionsMethod listConvMethod = new ListConversionsMethod();
        ConversionDto conversionObject = new ConversionDto();
        conversionObject.setStylesheet("stylesheet");
        conversionObject.setConvId("11");
        conversionObject.setResultType("RDF");
        conversionObject.setContentType("application/rdf+xml;charset=UTF-8");
        conversionObject.setXmlSchema("schema");

        Hashtable<String, String> h = listConvMethod.getMapFromConversionObject(conversionObject);
        assertEquals("stylesheet", h.get(ListConversionsMethod.KEY_XSL));
        assertEquals("11", h.get(ListConversionsMethod.KEY_CONVERT_ID));
        assertEquals("RDF", h.get(ListConversionsMethod.KEY_RESULT_TYPE));
        assertEquals("application/rdf+xml;charset=UTF-8", h.get(ListConversionsMethod.KEY_CONTENTTYPE_OUT));
        assertEquals("schema", h.get(ListConversionsMethod.KEY_XML_SCHEMA));

    }
    /**
     * Test if listConversions method returns generated conversion if local conversion is available.
     * @throws Exception
     */
    @Test
    public void testGeneratedConversionsIgnoring() throws Exception{
        ListConversionsMethod listConvMethod = new ListConversionsMethod();
        Vector<Hashtable<String, String>> conversions = listConvMethod.listConversions("http://dd.eionet.europa.eu/GetSchema?id=TBL6592");

        int countRdfConversions = 0;
        for (Hashtable<String, String> conversion : conversions){
            if ("RDF".equals(conversion.get(ListConversionsMethod.KEY_RESULT_TYPE))){
                countRdfConversions++;
            }
        }
        assertEquals(1, countRdfConversions);
    }
}
