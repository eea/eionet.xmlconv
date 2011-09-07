/*
 * Created on 23.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.List;

import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ListConvFormActionTest
 */

public class ListConvFormActionTest extends MockStrutsTestCase {

    public ListConvFormActionTest(String testName) {
        super(testName);
    }

    public void setUp() throws Exception {
        super.setUp();
        setConfigFile(TestUtils.getStrutsConfigLocation());
        setInitParameter("validating", "false");

        // setup database
        DbHelper.setUpDatabase(this, TestConstants.SEED_DATASET_CONVERSIONS_XML);

    }

    /**
     * test if the form is successfully formwarding and stores the schemas list in session
     */
    public void testSuccessfulForward() {

        setRequestPathInfo("/listConvForm");
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/listConv.jsp");
        verifyNoActionErrors();

        List schemasInSession = (List) request.getSession().getAttribute("conversion.schemas");
        assertTrue(schemasInSession.size() > 0);

    }

}
