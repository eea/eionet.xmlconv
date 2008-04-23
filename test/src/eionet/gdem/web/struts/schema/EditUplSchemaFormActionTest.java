/*
 * Created on 21.04.2008
 */
package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpSession;

import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import servletunit.struts.MockStrutsTestCase;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * EditUplSchemaFormActionTest
 */

public class EditUplSchemaFormActionTest  extends MockStrutsTestCase {

    public EditUplSchemaFormActionTest(String testName) {
        super(testName);
    }

    public void setUp() throws Exception {
        super.setUp();
        setConfigFile(TestUtils.getStrutsConfigLocation());
        setInitParameter("validating","false");
        
        //setup database
        DbHelper.setUpDatabase(this, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);

    }

    public void testSuccessfulForward() {
        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_ADMIN_USER);
        
        addRequestParameter("schemaId","8");
        setRequestPathInfo("/editUplSchemaForm");
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/editUplSchema.jsp");
        verifyNoActionErrors();
    }

    public void testFailedForward() {
        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_ADMIN_USER);
        
        addRequestParameter("schemaId","0");
        setRequestPathInfo("/editUplSchemaForm");
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/editUplSchema.jsp");
        String[] errMess = {BusinessConstants.EXCEPTION_GENERAL};
        verifyActionErrors(errMess);
    }
}

