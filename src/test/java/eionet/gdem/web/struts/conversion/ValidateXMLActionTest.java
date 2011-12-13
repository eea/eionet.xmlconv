/*
 * Created on 23.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.List;

import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ValidateXMLActionTest
 */

public class ValidateXMLActionTest extends MockStrutsTestCase {

    public ValidateXMLActionTest(String testName) {
        super(testName);
    }

    public void setUp() throws Exception {
        super.setUp();
        setContextDirectory(TestUtils.getContextDirectory());
        setInitParameter("validating", "false");

        // setup database
        DbHelper.setUpDatabase(this, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);
        TestUtils.setUpProperties(this);

    }

    /**
     * test if the form is successfully forwarded and retreives no validation errors
     */
    public void testSuccessfulForwardValidXML() {

        String url = TestUtils.getSeedURL(TestConstants.SEED_GW_VALID_XML, this);

        setRequestPathInfo("/validateXML");

        addRequestParameter("url", url);
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/validateXML.jsp");
        verifyNoActionErrors();

        assertEquals(request.getAttribute("conversion.validatedSchema"),
                Properties.gdemURL.concat("/").concat(TestConstants.SEED_GW_SCHEMA));
        assertTrue(request.getAttribute("conversion.valid") == null
                || ((List) request.getAttribute("conversion.valid")).size() == 0); // no errors in the list

    }

    /**
     * test if the form is successfully forwarded and retreives validation errors
     */
    public void testSuccessfulForwardInvalidXML() {

        String url = TestUtils.getSeedURL(TestConstants.SEED_GW_INVALID_XML, this);

        setRequestPathInfo("/validateXML");

        addRequestParameter("url", url);
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/validateXML.jsp");
        verifyNoActionErrors();

        assertEquals(request.getAttribute("conversion.validatedSchema"),
                Properties.gdemURL.concat("/").concat(TestConstants.SEED_GW_SCHEMA));
        assertTrue(((List) request.getAttribute("conversion.valid")).size() > 0); // errors in the list

    }

    /**
     * test if the form is successfully formwarding The form should get an error message
     */
    public void testFailedForward() {

        String url = "It is not an URL";

        setRequestPathInfo("/validateXML");

        addRequestParameter("url", url);
        actionPerform();
        verifyForward("error");
        verifyForwardPath("/do/validateXMLForm");
        String[] errMess = {BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED};
        verifyActionErrors(errMess);

    }

    /**
     * test if the form is successfully formwarding The form should get an error message
     */
    public void testFailedForwardWithoutURL() {

        setRequestPathInfo("/validateXML");

        actionPerform();
        verifyForward("error");
        verifyForwardPath("/do/validateXMLForm");
        String[] errMess = {"label.conversion.selectSource"};
        verifyActionErrors(errMess);

    }
}
