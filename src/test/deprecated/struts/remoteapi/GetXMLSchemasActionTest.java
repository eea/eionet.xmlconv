/*
 * Created on 18.03.2008
 */
package eionet.gdem.web.struts.remoteapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;

import eionet.gdem.utils.xml.XPathQuery;
import eionet.gdem.utils.xml.dom.DomContext;
import eionet.gdem.utils.xml.tiny.TinyTreeContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.dcm.remote.GetXMLSchemasResult;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockServletOutputStream;
import eionet.gdem.test.mocks.MockServletResponse;
import eionet.gdem.utils.xml.IXmlCtx;

import javax.sql.DataSource;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS GetXMLSchemasActionTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class GetXMLSchemasActionTest {

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
     * Tests action execution
     */
    @Test
    public void testExecute() throws Exception {

        // call the request
        MockServletResponse response = TestUtils.executeAction(new GetXMLSchemasAction(), new HashMap());

        assertEquals(TestConstants.XML_CONTENTYPE_RESULT, response.getContentType());
        assertEquals(200, response.getStatus());

        // System.out.println(response.getOutputStream().toString());

        // check if the result is well-formed XML
        IXmlCtx x = new DomContext();
        x.setWellFormednessChecking();
        x.checkFromInputStream(new ByteArrayInputStream(((MockServletOutputStream) response.getOutputStream()).toByteArray()));

        // count the schemas found from returned XML
        XPathQuery xQuery = x.getQueryManager();
        List schemas = xQuery.getElementValues(GetXMLSchemasResult.SCHEMA_TAG);
        assertTrue(schemas.size() > 36);
    }

}
