/*
 * Created on 22.04.2008
 */
package eionet.gdem.web.struts.schema;

import java.io.File;
import java.util.Hashtable;

import javax.servlet.http.HttpSession;

import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IUPLSchemaDao;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockStrutsMultipartRequestSimulator;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * EditUplSchemaActionTest
 */

public class EditUplSchemaActionTest  extends MockStrutsTestCase {

	private  IUPLSchemaDao uplSchemaDao = GDEMServices.getDaoService().getUPLSchemaDao();
	private String uplSchemaId="8";
	private String schemaId="2";
	private String schemaFileName="xliff.dtd";

	public EditUplSchemaActionTest(String testName) {
		super(testName);
	}

	public void setUp() throws Exception {
		super.setUp();
		//set struts-confg file location
		setConfigFile(TestUtils.getStrutsConfigLocation());
		// set tempdir property for executing multi-part requests. Struts tries to save the sent file temprarily
		context.setAttribute("javax.servlet.context.tempdir", new File(TestUtils.getStrutsTempDir(this)));
		setInitParameter("validating","false");
		//setup database
		DbHelper.setUpDatabase(this, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);
		TestUtils.setUpProperties(this);
	}
	/**
	 * Tests successful editing. Verifies the action message and forward
	 * @throws Exception 
	 * 
	 */
	public void testSuccessfulForward() throws Exception {

		//overwrite the default StrutsRequestSimulator and mock multipartrequest object
		request = new MockStrutsMultipartRequestSimulator(config.getServletContext());
		setRequestPathInfo("/editUplSchema");

		HttpSession session = request.getSession();
		session.setAttribute("user", TestConstants.TEST_ADMIN_USER);

		addRequestParameter("uplSchemaId",uplSchemaId);
		addRequestParameter("schemaId",schemaId);
		addRequestParameter("uplSchemaFileName",schemaFileName);
		((MockStrutsMultipartRequestSimulator)request).writeFile("schemaFile",getClass().getClassLoader().getResource(TestConstants.SEED_XLIFF_DTD).getFile(),"text/xml");       

		actionPerform();
		verifyForward("success");
		verifyForwardPath("/do/schemaElemForm");
		//verifyTilesForward("success", "/do/uplSchemas");
		verifyNoActionErrors();
		String[] actionMess = {"label.uplSchema.updated"};
		verifyActionMessages(actionMess);

		//Get schema by ID and test if all inserted fields are in DB
		Hashtable schema = uplSchemaDao.getUplSchemaById(uplSchemaId);
		assertEquals((String)schema.get("upl_schema_file"),schemaFileName);
	}
	/**
	 * Editing failed, because of lack of permissins
	 * @throws Exception 
	 * 
	 */
	public void testFailedNotPermissions() throws Exception {

		//overwrite the default StrutsRequestSimulator and mock multipartrequest object
		request = new MockStrutsMultipartRequestSimulator(config.getServletContext());
		setRequestPathInfo("/editUplSchema");

		HttpSession session = request.getSession();
		session.setAttribute("user", TestConstants.TEST_USER);

		addRequestParameter("uplSchemaId",uplSchemaId);
		addRequestParameter("schemaId",schemaId);
		addRequestParameter("uplSchemaFileName",schemaFileName);
		((MockStrutsMultipartRequestSimulator)request).writeFile("schemaFile",getClass().getClassLoader().getResource(TestConstants.SEED_XLIFF_XML).getFile(),"text/xml");       

		actionPerform();
		verifyForward("success");
		verifyForwardPath("/do/schemaElemForm");
		String[] errMess = {BusinessConstants.EXCEPTION_AUTORIZATION_SCHEMA_UPDATE};
		verifyActionErrors(errMess);

		//Get schema by ID and test if all inserted fields are NOT in DB
		Hashtable schema = uplSchemaDao.getUplSchemaById(uplSchemaId);
		assertTrue(schemaFileName.equals((String)schema.get("upl_schema_file")));
		assertTrue(uplSchemaId.equals((String)schema.get("upl_schema_id")));

	}
    /**
     * test failed adding the new file, the form should display error message: "schema file not found"
     */
    public void testFailedFileNotFound()throws Exception {

    	int countUplSchema = uplSchemaDao.getUplSchema().size();
        //HttpSession session = request.getSession();
        //session.setAttribute("user", TestConstants.TEST_ADMIN_USER);

        addRequestParameter("uplSchemaId",uplSchemaId);
        
        setRequestPathInfo("/editUplSchema");
        actionPerform();
		verifyForwardPath("/do/schemaElemForm");
        String[] errMess = {"label.uplSchema.upload.validation"};
        verifyActionErrors(errMess);

        //check if the row was added or not
        int countUplSchema2 = uplSchemaDao.getUplSchema().size();
        assertEquals(countUplSchema,countUplSchema2);
        
    }
}

