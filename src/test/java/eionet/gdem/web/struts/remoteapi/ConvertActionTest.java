/*
 * Created on 17.03.2008
 */
package eionet.gdem.web.struts.remoteapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.dcm.remote.XMLErrorResult;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockServletOutputStream;
import eionet.gdem.test.mocks.MockServletResponse;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;

import javax.sql.DataSource;

/**
 * This unittest tests the Struts action that calls Conversion Service convert method
 *
 * @author Enriko Käsper, TietoEnator Estonia AS ConvertActionTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ConvertActionTest {

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
     * Tests execute method - validate the result file and metadata( content type and file name)
     */
    @Test
    public void testExecute() throws Exception {

        String param1[] = { TestUtils.getSeedURL(TestConstants.SEED_GENERAL_REPORT_XML, this) };
        String param2[] = { "168" };
        Map map = new HashMap();
        map.put(ConvertAction.URL_PARAM_NAME, param1);
        map.put(ConvertAction.CONVERT_ID_PARAM_NAME, param2);

        // call the request
        MockServletResponse response = TestUtils.executeAction(new ConvertAction(), map);

        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, response.getContentType());
        assertEquals(new StringBuilder("inline;filename=\"").append(TestConstants.GR_HTML_FILENAME_RESULT).append("\"").toString(), response.getHeader("Content-Disposition"));
        // test if the converion result contains some text from seed..xml file
        assertTrue(response.getOutputStream().toString().indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Tests convert method - validate the result file and metadata( content type and file name)
     */
    @Test
    public void testExecuteDDTableHTML() throws Exception {

        String param1[] = { TestUtils.getSeedURL(TestConstants.SEED_OZONE_STATION_XML, this) };
        String param2[] = { "DD_TBL3453_CONV5" };
        Map map = new HashMap();
        map.put(ConvertAction.URL_PARAM_NAME, param1);
        map.put(ConvertAction.CONVERT_ID_PARAM_NAME, param2);

        // call the request
        MockServletResponse response = TestUtils.executeAction(new ConvertAction(), map);

        assertEquals(TestConstants.HTML_CONTENTYPE_RESULT, response.getContentType());
        assertEquals(new StringBuilder("inline;filename=\"").append(TestConstants.OZ_HTML_FILENAME_RESULT).append("\"").toString(), response.getHeader("Content-Disposition"));
        // test if the converion result contains some text from seed..xml file
        assertTrue(response.getOutputStream().toString().indexOf(TestConstants.STRCONTENT_RESULT) > 0);
    }

    /**
     * Tests convert method - the conversion should fail and result should be error XML
     */
    @Test
    public void testExecuteError() throws Exception {

        String param1[] = { TestUtils.getSeedURL(TestConstants.SEED_GENERAL_REPORT_XML, this) };
        String param2[] = { "-99" };
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put(ConvertAction.URL_PARAM_NAME, param1);
        map.put(ConvertAction.CONVERT_ID_PARAM_NAME, param2);

        // call the request
        MockServletResponse response = TestUtils.executeAction(new ConvertAction(), map);

        assertEquals(TestConstants.XML_CONTENTYPE_RESULT, response.getContentType());
        assertEquals(400, response.getStatus());

        // System.out.println(response.getOutputStream().toString());
        // test if the converion result contains some text from seed..xml file
        assertTrue(response.getOutputStream().toString().indexOf(XMLErrorResult.ERROR_TAG) > 0);

        // check if the result is well-formed XML
        IXmlCtx x = new XmlContext();
        x.setWellFormednessChecking();
        x.checkFromInputStream(new ByteArrayInputStream(((MockServletOutputStream) response.getOutputStream()).toByteArray()));

    }
}
