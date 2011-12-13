/*
 * Created on 23.04.2008
 */
package eionet.gdem.web.struts.conversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.Schema;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ListConversionsActionTest
 */

public class ListConversionsActionTest extends MockStrutsTestCase {

    private static final Log LOGGER = LogFactory.getLog(ListConversionsActionTest.class);

    public ListConversionsActionTest(String testName) {
        super(testName);
    }

    public void setUp() throws Exception {
        super.setUp();
        setContextDirectory(TestUtils.getContextDirectory());
        setInitParameter("validating", "false");

        // setup database
        DbHelper.setUpDatabase(this, TestConstants.SEED_DATASET_CONVERSIONS_XML);

    }

    /**
     * test if the form is successfully formwarding and stores ConversionForm in session The form should find available conversions
     * for specified URL
     */
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
    public void testFailedForward() {

        String url = "It is not an URL";

        setRequestPathInfo("/listConversionsByXML");

        addRequestParameter("url", url);
        addRequestParameter("searchAction", "search");
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/listConv.jsp");
        String[] errMess = {BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED};
        verifyActionErrors(errMess);

        ConversionForm cForm = (ConversionForm) request.getSession().getAttribute("ConversionForm");
        assertEquals(cForm.getUrl(), url);
        assertTrue(cForm.getSchemas().size() == 0);

    }

    /**
     * test if the form is successfully formwarding and stores ConversionForm in session The form should find available conversions
     * for specified URL
     */
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
