/*
 * Created on 23.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.List;

import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.Schema;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * SearchCRConversionActionTest
 */

public class SearchCRConversionActionTest   extends MockStrutsTestCase {

    public SearchCRConversionActionTest(String testName) {
        super(testName);
    }

    public void setUp() throws Exception {
        super.setUp();
        setConfigFile(TestUtils.getStrutsConfigLocation());
        setInitParameter("validating","false");
        
        //setup database
        DbHelper.setUpDatabase(this, TestConstants.SEED_DATASET_CONVERSIONS_XML);

    }

    /**
     * test if the form is successfully formwarding and stores the schemas list in session
     */
    public void testSuccessfulForward() {
        
    	String schemaUrl = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";

    	setRequestPathInfo("/searchCR");
    	
        addRequestParameter("schemaUrl",schemaUrl);
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/crConversion.jsp");
        verifyNoActionErrors();

        ConversionForm cForm = (ConversionForm) request.getSession().getAttribute("ConversionForm");
        assertEquals(cForm.getSchemaUrl(),schemaUrl);

        //XMLCONV should find some stylesheets for specified XML
        Schema schema = cForm.getSchema();
        assertTrue(schema.getStylesheets().size()>0);
    }
    /**
     * test if the form is successfully formwarding and stores the schemas list in session
     */
    public void testFailedForward() {
        
    	String schemaUrl = "No such schema";

    	setRequestPathInfo("/searchCR");
    	
        addRequestParameter("schemaUrl",schemaUrl);
        actionPerform();
        verifyForward("error");
        verifyForwardPath("/do/crConversionForm");
        String[] errMess ={BusinessConstants.EXCEPTION_GENERAL};
        verifyActionErrors(errMess);

    }

}

