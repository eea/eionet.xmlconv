/*
 * Created on 21.04.2008
 */
package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.dcm.BusinessConstants;
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
 * @author Enriko Käsper, TietoEnator Estonia AS EditUplSchemaFormActionTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class SchemaElemFormActionTest extends MockStrutsTestCase {
    @Autowired
    private DataSource db;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setContextDirectory(TestUtils.getContextDirectory());
        setInitParameter("validating", "false");

        // setup database
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);

    }
    @Test
    public void testSuccessfulForward() {
        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_ADMIN_USER);

        addRequestParameter("schemaId", "2");
        setRequestPathInfo("/schemaElemForm");
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/schema.jsp");
        verifyNoActionErrors();
    }
    @Test
    public void testFailedForward() {
        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_ADMIN_USER);

        addRequestParameter("schemaId", "0");
        setRequestPathInfo("/schemaElemForm");
        actionPerform();
        verifyForward("fail");
        verifyInputTilesForward("/schema.jsp");
        String[] errMess = {BusinessConstants.EXCEPTION_SCHEMA_NOT_EXIST};
        verifyActionErrors(errMess);
    }
}
