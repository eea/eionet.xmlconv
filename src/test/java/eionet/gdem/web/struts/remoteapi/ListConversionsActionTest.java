/*
 * Created on 18.03.2008
 */
package eionet.gdem.web.struts.remoteapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.dcm.remote.ListConversionsResult;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockServletOutputStream;
import eionet.gdem.test.mocks.MockServletResponse;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS testListConversionsTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ListConversionsActionTest {

    @Autowired
    private IDatabaseTester databaseTester;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDefaultDatabaseTester(databaseTester, TestConstants.SEED_DATASET_CONVERSIONS_XML);
    }

    /**
     * Tests action execution
     */
    @Test
    public void testExecute() throws Exception {
        // call the request
        MockServletResponse response = TestUtils.executeAction(new ListConversionsAction(), new HashMap());

        assertEquals(TestConstants.XML_CONTENTYPE_RESULT, response.getContentType());
        assertEquals(200, response.getStatus());

        // System.out.println(response.getOutputStream().toString());

        // check if the result is well-formed XML
        IXmlCtx x = new XmlContext();
        x.setWellFormednessChecking();
        x.checkFromInputStream(new ByteArrayInputStream(((MockServletOutputStream) response.getOutputStream()).toByteArray()));

        // count the conversions found from returned XML
        IXQuery xQuery = x.getQueryManager();
        List convertIds = xQuery.getElementValues(ListConversionsResult.CONVERT_ID_TAG);
        assertTrue(convertIds.size() > 83);
    }

    /**
     * Tests action execution with parameters
     */
    @Test
    public void testExecuteWithParams() throws Exception {

        String schema = "http://waste.eionet.europa.eu/schemas/waste/schema.xsd";
        String param1[] = { schema };
        Map map = new HashMap();
        map.put(ListConversionsAction.SCHEMA_PARAM_NAME, param1);

        // call the request
        MockServletResponse response = TestUtils.executeAction(new ListConversionsAction(), map);

        assertEquals(TestConstants.XML_CONTENTYPE_RESULT, response.getContentType());
        assertEquals(200, response.getStatus());

        // System.out.println(response.getOutputStream().toString());

        // check if the result is well-formed XML
        IXmlCtx x = new XmlContext();
        x.setWellFormednessChecking();
        x.checkFromInputStream(new ByteArrayInputStream(((MockServletOutputStream) response.getOutputStream()).toByteArray()));

        // There should be 3 conversions
        IXQuery xQuery = x.getQueryManager();
        List conversions = xQuery.getElements(ListConversionsResult.CONVERSION_TAG);
        assertEquals(3, conversions.size());

        // validate the converison Ids
        List converIds = xQuery.getElementValues(ListConversionsResult.CONVERT_ID_TAG);
        assertEquals("169", converIds.get(0));
        assertEquals("171", converIds.get(1));

        // validate the xsl file names
        List xsl = xQuery.getElementValues(ListConversionsResult.XSL_TAG);
        assertEquals("dir75442_excel.xsl", xsl.get(0));
        assertEquals("dir75442_html.xsl", xsl.get(1));

        // validate content types
        List contentTypes = xQuery.getElementValues(ListConversionsResult.CONTENT_TYPE_TAG);
        assertEquals(TestConstants.EXCEL_CONTENTYPE_RESULT, contentTypes.get(0));
        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, contentTypes.get(1));

        // validate result types
        List resultTypes = xQuery.getElementValues(ListConversionsResult.RESULT_TYPE_TAG);
        assertEquals("EXCEL", resultTypes.get(0));
        assertEquals("HTML", resultTypes.get(1));

        // validate schemas
        List schemas = xQuery.getElementValues(ListConversionsResult.XML_SCHEMA_TAG);
        assertEquals(schema, schemas.get(0));
        assertEquals(schema, schemas.get(1));
    }
}
