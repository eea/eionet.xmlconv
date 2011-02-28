/*
 * Created on 23.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import servletunit.struts.MockStrutsTestCase;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.CRServiceClient;
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
     * test if the form is successfully forwarding and stores the schemas list in session
     */
    public void testSuccessfulForward() {
        
    	String schemaUrl = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";
    	CRServiceClient.setMockXmlFilesBySchema(getXmlFilesBySchema(schemaUrl));
    	
    	setRequestPathInfo("/searchCR");
    	
        addRequestParameter("schemaUrl",schemaUrl);
        actionPerform();
        verifyForward("success");
        verifyInputTilesForward("/crConversion.jsp");
        verifyNoActionErrors();

        ConversionForm cForm = (ConversionForm) request.getSession().getAttribute("ConversionForm");
        assertEquals(cForm.getSchemaUrl(),schemaUrl);

        //XMLCONV should find some stylesheets for specified XML
       //Schema schema = cForm.getSchema();
        //assertTrue(schema.getStylesheets().size()>0);
    }
    /**
     * test if the form is successfully forwarding and stores the schemas list in session
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

    private List<Hashtable<String,String>> getXmlFilesBySchema(String schema){
	    
    	Hashtable<String,String> hash1 = new Hashtable<String,String>();
    	hash1.put("uri", "http://test.com/file1.xml");
    	hash1.put("lastModified", "2006-07-03T13:19:33");
    	
    	Hashtable<String,String> hash2 = new Hashtable<String,String>();
    	hash2.put("uri", "http://test.com/file2.xml");
    	hash2.put("lastModified", "2007-07-03T13:19:33");

    	Hashtable<String,String> hash3 = new Hashtable<String,String>();
    	hash3.put("uri", "http://test.com/file3.xml");
    	hash3.put("lastModified", "2008-07-03T13:19:33");
    	
    	List<Hashtable<String,String>> list= new ArrayList<Hashtable<String,String>>();
    	list.add(hash1);
    	list.add(hash2);
    	list.add(hash3);
    	return list;
    }
}

