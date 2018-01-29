/*
 * Created on 22.04.2008
 */
package eionet.gdem.web.struts.schema;

import java.io.File;
import java.util.Hashtable;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.web.spring.schemas.IUPLSchemaDao;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS DeleteUplSchemaActiontest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class DeleteUplSchemaActiontest extends MockStrutsTestCase {

    @Autowired
    private DataSource db;

    private IUPLSchemaDao uplSchemaDao;
    private String schemaId = "3";
    private String uplSchemaId = "10";

    @Before
    public void setUp() throws Exception {
        super.setUp();
        // set struts-confg file location
        uplSchemaDao = GDEMServices.getDaoService().getUPLSchemaDao();
        setContextDirectory(TestUtils.getContextDirectory());
        // set tempdir property for executing multi-part requests. Struts tries to save the sent file temprarily
        context.setAttribute("javax.servlet.context.tempdir", new File(TestUtils.getStrutsTempDir(this)));
        setInitParameter("validating", "false");
        // setup database
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);
        TestUtils.setUpProperties(this);
    }

    /**
     * Tests successful deleting. Verifies the action message and forward and DB
     *
     * @throws Exception
     *
     */
    @Test
    public void testSuccessfulForward() throws Exception {

        int countUplSchema = uplSchemaDao.getUplSchema().size();

        setRequestPathInfo("/deleteUplSchema");

        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_ADMIN_USER);

        addRequestParameter("schemaId", schemaId);
        addRequestParameter("schema", "schema");
        // addRequestParameter("schemaFile","schema.xsd");
        addRequestParameter("deleteSchema", "true");

        actionPerform();
        verifyForward("success");
        verifyForwardPath("/do/uplSchemas");
        // verifyTilesForward("success", "/do/uplSchemas");
        verifyNoActionErrors();
        String[] actionMess = {"label.schema.deleted"};
        verifyActionMessages(actionMess);

        // check if the row was deleted or not
        int countUplSchema2 = uplSchemaDao.getUplSchema().size();
        assertEquals(countUplSchema - 1, countUplSchema2);
    }

    /**
     * Editing failed, because of lack of permissins
     *
     * @throws Exception
     *
     */
    @Test
    public void testFailedNotPermissions() throws Exception {

        int countUplSchema = uplSchemaDao.getUplSchema().size();

        setRequestPathInfo("/deleteUplSchema");

        HttpSession session = request.getSession();
        session.setAttribute("user", TestConstants.TEST_USER);

        addRequestParameter("schemaId", schemaId);
        addRequestParameter("schema", "schema");

        actionPerform();
        verifyForward("fail");
        verifyForwardPath("/do/schemaElemForm");
        String[] errMess = {BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_DELETE};
        verifyActionErrors(errMess);

        // Get schema by ID and test if it still exists in DB
        Hashtable schema = uplSchemaDao.getUplSchemaById(uplSchemaId);
        assertEquals((String) schema.get("upl_schema_id"), uplSchemaId);

        // check if the row was deleted or not
        int countUplSchema2 = uplSchemaDao.getUplSchema().size();
        assertEquals(countUplSchema, countUplSchema2);

    }
}
