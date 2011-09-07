/*
 * Created on 21.04.2008
 */
package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpSession;

import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS UplSchemaFormActionTest
 */

public class UplSchemaFormActionTest extends MockStrutsTestCase {

    public UplSchemaFormActionTest(String testName) {
        super(testName);
    }

    public void setUp() throws Exception {
        super.setUp();
        setConfigFile(TestUtils.getStrutsConfigLocation());
        setInitParameter("validating", "false");

        // setup database
        DbHelper.setUpDatabase(this, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);
    }

    public void testSuccessfulForward() {

        setRequestPathInfo("/uplSchemas");
        actionPerform();
        verifyForward("success");
        verifyTilesForward("success", "/uplSchema.jsp");
        verifyNoActionErrors();

        // test if the list of schemas is stored in session attribute
        HttpSession session = request.getSession();
        UplSchemaHolder holder = (UplSchemaHolder) session.getAttribute("schemas.uploaded");
        assertTrue(holder.getSchemas().size() > 0);
    }
}
