package eionet.gdem.web.struts.stylesheet;

import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockStrutsMultipartRequestSimulator;
import servletunit.struts.MockStrutsTestCase;

import javax.servlet.http.HttpSession;
import java.io.File;

/**
 * @author George Sofianos
 */
public class AddStylesheetActionTest extends MockStrutsTestCase {

  public AddStylesheetActionTest(String testName) {
    super(testName);
  }

  public void setUp() throws Exception {
    super.setUp();
    // set struts-confg file location
    setContextDirectory(TestUtils.getContextDirectory());
    // set tempdir property for executing multi-part requests. Struts tries to save the sent file temprarily
    context.setAttribute("javax.servlet.context.tempdir", new File(TestUtils.getStrutsTempDir(this)));
    setInitParameter("validating", "false");
    TestUtils.setUpProperties(this);
  }

  public void testAddStylesheetWithoutDescription() throws Exception {
    request = new MockStrutsMultipartRequestSimulator(config.getServletContext());
    setRequestPathInfo("/stylesheetAdd");

    HttpSession session = request.getSession();
    session.setAttribute("user", TestConstants.TEST_ADMIN_USER);

    addRequestParameter("newSchemas", "http://thisisatesturl.com/testschema.xsd");
    addRequestParameter("outputtype", "XML");
    addRequestParameter("description", "");
    ((MockStrutsMultipartRequestSimulator) request).writeFile("xslfile",
            getClass().getClassLoader().getResource(TestConstants.SEED_XSLSCRIPT_TEST).getFile(), "text/xml");
    actionPerform();
    verifyForward("fail");
    verifyForwardPath("/do/addStylesheetForm");
    String[] errors = {"label.stylesheet.error.descriptionMissing"};
    verifyActionErrors(errors);
  }
}