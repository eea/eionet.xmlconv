/*
 * Created on 23.04.2008
 */
package eionet.gdem.web.struts.qasandbox;

import eionet.gdem.test.ApplicationTestContext;
import javax.servlet.http.HttpSession;

import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS RunScriptActionTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class RunScriptActionTest extends MockStrutsTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setContextDirectory(TestUtils.getContextDirectory());
        setInitParameter("validating", "false");

        TestUtils.setUpProperties(this);
    }

    /**
     * test the QA sandbox permissions. Don't allow execute manually inserted scripts for non-authorized users
     */
    @Test
    public void testRunScriptNoPermissions() {

        setRequestPathInfo("/runScript");

        addRequestParameter("schemaUrl", "http://air-climate.eionet.europa.eu/schemas/dir199913ec/schema.xsd");
        addRequestParameter("sourceUrl", "http://cdr.eionet.europa.eu/fi/euvocsol/envsfurdw/questionnaire_voc_solvents.xml");
        addRequestParameter("scriptType", "xquery");
        addRequestParameter("scriptContent", "xquery version \"1.0\" \n\r string(4)");

        actionPerform();
        verifyForward("error");
        String[] errorMess = {"label.autorization.qasandbox.execute"};
        verifyActionErrors(errorMess);

    }

    /**
     * test the QA sandbox permissions. Don't allow execute manually inserted scripts for non-authorized users
     */
    @Test
    public void testRunScriptNoPermissions2() {

        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_USER);

        setRequestPathInfo("/runScript");

        addRequestParameter("schemaUrl", "http://air-climate.eionet.europa.eu/schemas/dir199913ec/schema.xsd");
        addRequestParameter("sourceUrl", "http://cdr.eionet.europa.eu/fi/euvocsol/envsfurdw/questionnaire_voc_solvents.xml");
        addRequestParameter("scriptType", "xquery");
        addRequestParameter("scriptContent", "xquery version \"1.0\" \n\r string(4)");

        actionPerform();
        verifyForward("error");
        String[] errorMess = {"label.autorization.qasandbox.execute"};
        verifyActionErrors(errorMess);

    }

    /**
     * test the QA sandbox permissions. Don't allow execute manually inserted scripts for non-authorized users
     */
    @Test
    public void testRunScriptSuccess() {

        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_ADMIN_USER);

        setRequestPathInfo("/runScript");

        addRequestParameter("schemaUrl", "http://air-climate.eionet.europa.eu/schemas/dir199913ec/schema.xsd");
        addRequestParameter("sourceUrl", "http://cdr.eionet.europa.eu/fi/euvocsol/envsfurdw/questionnaire_voc_solvents.xml");
        addRequestParameter("scriptType", "xquery 1.0");
        addRequestParameter("scriptContent", "xquery version \"1.0\";\n\r string(4)");

        actionPerform();
        verifyForward("success");

        verifyNoActionErrors();
        verifyNoActionMessages();
    }

}
