/*
 * Created on 21.04.2008
 */
package eionet.gdem.web.struts.schema;

import java.io.File;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.web.spring.schemas.IUPLSchemaDao;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockStrutsMultipartRequestSimulator;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS AddUplSchemaAction
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class AddUplSchemaActionTest extends MockStrutsTestCase {

    @Autowired
    private DataSource db;

    private IUPLSchemaDao uplSchemaDao;
    private String schemaUrl = "http://some.valid.url.eu/schema";
    private String description = "Updated description";

    //public AddUplSchemaActionTest(String testName) {
    //    super(testName);
   // }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        // set struts-confg file location
        setContextDirectory(TestUtils.getContextDirectory());
        // set tempdir property for executing multi-part requests. Struts tries to save the sent file temprarily
        context.setAttribute("javax.servlet.context.tempdir", new File(TestUtils.getStrutsTempDir(this)));
        setInitParameter("validating", "false");
        // setup database
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);
        uplSchemaDao = GDEMServices.getDaoService().getUPLSchemaDao();
        TestUtils.setUpProperties(this);
    }

    /**
     * Tests successful adding. Verifies the action message and forward
     *
     * @throws Exception
     *
     */
    @Test
    public void testSuccessfulForward() throws Exception {
        int countUplSchema = uplSchemaDao.getUplSchema().size();
        // overwrite the default StrutsRequestSimulator and mock multipartrequest object
        request = new MockStrutsMultipartRequestSimulator(config.getServletContext());
        setRequestPathInfo("/addUplSchema");

        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_ADMIN_USER);

        addRequestParameter("schemaUrl", schemaUrl);
        addRequestParameter("description", description);
        ((MockStrutsMultipartRequestSimulator) request).writeFile("schemaFile",
                getClass().getClassLoader().getResource(TestConstants.SEED_GENERALREPORT_SCHEMA).getFile(), "text/xml");

        actionPerform();
        verifyForward("success");
        verifyForwardPath("/do/uplSchemas");
        // verifyTilesForward("success", "/do/uplSchemas");
        verifyNoActionErrors();
        String[] actionMess = {"label.uplSchema.inserted"};
        verifyActionMessages(actionMess);

        int countUplSchema2 = uplSchemaDao.getUplSchema().size();
        assertEquals(countUplSchema + 1, countUplSchema2);

    }

    /**
     * Tests adding schema with user without appropriate permissions
     *
     * @throws Exception
     *
     */
    @Test
    public void testFailedNotPermissions() throws Exception {

        int countUplSchema = uplSchemaDao.getUplSchema().size();
        // overwrite the default StrutsRequestSimulator and mock multipartrequest object
        request = new MockStrutsMultipartRequestSimulator(config.getServletContext());
        setRequestPathInfo("/addUplSchema");

        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_USER);

        addRequestParameter("schemaUrl", schemaUrl);
        addRequestParameter("description", description);
        ((MockStrutsMultipartRequestSimulator) request).writeFile("schemaFile",
                getClass().getClassLoader().getResource(TestConstants.SEED_GENERALREPORT_SCHEMA).getFile(), "text/xml");

        actionPerform();
        verifyForward("success");
        verifyForwardPath("/do/uplSchemas");
        String[] errMess = {BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_INSERT};
        verifyActionErrors(errMess);

        // check if the row was added or not
        int countUplSchema2 = uplSchemaDao.getUplSchema().size();
        assertEquals(countUplSchema, countUplSchema2);
    }

    /**
     * test failed adding, the form should display error message: "schema file not found"
     */
    @Test
    public void testFailedFileNotFound() throws Exception {

        int countUplSchema = uplSchemaDao.getUplSchema().size();
        // HttpSession session = request.getSession();
        // session.setAttribute("user", TestConstants.TEST_ADMIN_USER);

        addRequestParameter("schemaUrl", "");
        addRequestParameter("description", description);

        setRequestPathInfo("/addUplSchema");
        actionPerform();
        verifyForward("fail");
        verifyForwardPath("/do/addUplSchemaForm");
        String[] errMess = {"label.uplSchema.validation"};
        verifyActionErrors(errMess);

        // check if the row was added or not
        int countUplSchema2 = uplSchemaDao.getUplSchema().size();
        assertEquals(countUplSchema, countUplSchema2);

    }
    @Test
    public void testFailedMalformedUrl() throws Exception {
        request = new MockStrutsMultipartRequestSimulator(config.getServletContext());
        setRequestPathInfo("/addUplSchema");

        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_ADMIN_USER);

        addRequestParameter("schemaUrl", "ht://malf.ormed");
        addRequestParameter("description", description);
        ((MockStrutsMultipartRequestSimulator) request).writeFile("schemaFile",
                getClass().getClassLoader().getResource(TestConstants.SEED_GENERALREPORT_SCHEMA).getFile(), "text/xml");

        actionPerform();
        verifyForward("fail");
    }
}
