/*
 * Created on 23.04.2008
 */
package eionet.gdem.web.struts.conversion;

import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.Schema;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ListConversionsActionTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ListConversionsActionTest extends MockStrutsTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setContextDirectory(TestUtils.getContextDirectory());
        setInitParameter("validating", "false");

        // setup database and Spring context
        DbHelper.setUpSpringContextWithDatabaseTester(TestConstants.SEED_DATASET_CONVERSIONS_XML);
    }

    /**
     * test if the form is successfully formwarding and stores ConversionForm in session The form should find available conversions
     * for specified URL
     */
    @Test
    public void testSuccessfulForward() {

        String url = TestUtils.getSeedURL(TestConstants.SEED_GENERAL_REPORT_XML, this);

        setRequestPathInfo("/listConversionsByXML");

        addRequestParameter("url", url);
        addRequestParameter("searchAction", "search");
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/listConv.jsp");
        verifyNoActionErrors();

        ConversionForm cForm = (ConversionForm) request.getSession().getAttribute("ConversionForm");
        assertEquals(cForm.getUrl(), url);
        assertTrue(cForm.getSchemas().size() > 0);

        // XMLCONV should find some stylesheets for specified XML
        Schema schema = cForm.getSchemas().get(0);
        assertTrue(schema.getStylesheets().size() > 0);

    }

    /**
     * test if the form is successfully formwarding and stores ConversionForm in session The form should find available conversions
     * for specified schema URL
     */
    @Test
    public void testSuccessfulForwardBySchema() {

        String schemaUrl = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";

        setRequestPathInfo("/listConversionsByXML");

        addRequestParameter("schemaUrl", schemaUrl);
        addRequestParameter("searchAction", "search");
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/listConv.jsp");
        verifyNoActionErrors();

        ConversionForm cForm = (ConversionForm) request.getSession().getAttribute("ConversionForm");
        assertEquals(cForm.getSchemaUrl(), schemaUrl);
        assertTrue(cForm.getSchemas().size() > 0);

        // XMLCONV should find some stylesheets for specified XML
        Schema schema = cForm.getSchemas().get(0);
        assertTrue(schema.getStylesheets().size() > 0);

    }

    /**
     * test if the form is successfully formwarding and stores ConversionForm in session The form should get an error message
     */
    @Test
    public void testFailedForward() {

        String url = "It is not an URL";

        setRequestPathInfo("/listConversionsByXML");

        addRequestParameter("url", url);
        addRequestParameter("searchAction", "search");
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/listConv.jsp");
        String[] errMess = { BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED };
        verifyActionErrors(errMess);

        ConversionForm cForm = (ConversionForm) request.getSession().getAttribute("ConversionForm");
        assertEquals(cForm.getUrl(), url);
        assertTrue(cForm.getSchemas().size() == 0);

    }

    /**
     * test if the form is successfully formwarding and stores ConversionForm in session The form should find available conversions
     * for specified URL
     */
    @Test
    public void testSuccessfulForwardConvert() {

        String url = TestUtils.getSeedURL(TestConstants.SEED_GENERAL_REPORT_XML, this);

        setRequestPathInfo("/listConversionsByXML");

        addRequestParameter("url", url);
        addRequestParameter("convertAction", "convert");
        addRequestParameter("conversionId", "168");
        addRequestParameter("converted", "false");
        actionPerform();
        verifyForward("convert");
        verifyForwardPath("/do/testConversion");
        verifyNoActionErrors();
        ConversionForm cForm = (ConversionForm) request.getSession().getAttribute("ConversionForm");
        assertEquals(cForm.getUrl(), url);
        assertEquals(cForm.getConversionId(), "168");

    }
}
