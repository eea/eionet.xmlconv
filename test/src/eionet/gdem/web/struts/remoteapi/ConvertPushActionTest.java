/*
 * Created on 19.03.2008
 */
package eionet.gdem.web.struts.remoteapi;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.dcm.remote.XMLErrorResult;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockServletOutputStream;
import eionet.gdem.test.mocks.MockServletResponse;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ConvertPushActionTest
 */

public class ConvertPushActionTest  extends DBTestCase{

	/**
	 * Provide a connection to the database.
	 */
	public ConvertPushActionTest(String name)	{
		super( name );
    	DbHelper.setUpConnectionProperties();
	}
	/**
	 * Set up test case properties
	 */
    protected void setUp()throws Exception{
    	super.setUp();
		TestUtils.setUpProperties(this);
    }
	/**
	 * Load the data which will be inserted for the test
	 */
	protected IDataSet getDataSet() throws Exception {
		IDataSet loadedDataSet = new FlatXmlDataSet(
				getClass().getClassLoader().getResourceAsStream(
						TestConstants.SEED_DATASET_CONVERSIONS_XML));
		return loadedDataSet;
	}
	/**
	 * Tests execute method - validate the result file and metadata( content type and file name) 
	 */
	public void testExecute() throws Exception {
        
        String seedFile  = getClass().getClassLoader().getResource(TestConstants.SEED_GENERAL_REPORT_XML).getFile();
        String fileContentType =TestConstants.XML_CONTENTYPE_RESULT;
        String fileParamName=ConvertPushAction.CONVERT_FILE_PARAM_NAME;
        
        String param1[] = {"168"};
        Map map = new HashMap();
		map.put(ConvertPushAction.CONVERT_ID_PARAM_NAME,param1);

		//call the request
		MockServletResponse response = TestUtils.executeActionMultipart(new ConvertPushAction(), map, fileParamName, seedFile, fileContentType);
		
		assertEquals(TestConstants.HTML_CONTENTYPE_RESULT,response.getContentType());
		assertEquals(new StringBuilder("inline;filename=\"").append(TestConstants.GR_HTML_FILENAME_RESULT).append("\"").toString()
				,response.getHeader("Content-Disposition"));
		//test if the converion result contains some text from seed..xml file
		assertTrue(response.getOutputStream().toString().indexOf(TestConstants.STRCONTENT_RESULT)>0);
	}

	/**
	 * Tests convertPush method with zipped XML file.
	 * Validate the result file and metadata( content type and file name) 
	 */
	public void testExecuteZip() throws Exception {

        String seedFile  = getClass().getClassLoader().getResource(TestConstants.SEED_GENERAL_REPORT_ZIP).getFile();
        String fileContentType =TestConstants.ZIP_CONTENTYPE_RESULT;
        String fileParamName=ConvertPushAction.CONVERT_FILE_PARAM_NAME;
        
        String param1[] = {"168"};
        Map map = new HashMap();
		map.put(ConvertPushAction.CONVERT_ID_PARAM_NAME,param1);

		//call the request
		MockServletResponse response = TestUtils.executeActionMultipart(new ConvertPushAction(), map, fileParamName, seedFile, fileContentType);
		
		assertEquals(TestConstants.HTML_CONTENTYPE_RESULT,response.getContentType());
		assertEquals(new StringBuilder("inline;filename=\"").append(TestConstants.GR_HTML_FILENAME_RESULT).append("\"").toString()
				,response.getHeader("Content-Disposition"));
		//test if the converion result contains some text from seed..xml file
		assertTrue(response.getOutputStream().toString().indexOf(TestConstants.STRCONTENT_RESULT)>0);
	}
	/**
	 * Tests convert method - the conversion should fail and result should be error XML  
	 */
	public void testExecuteError() throws Exception {
		
        
        String seedFile  = getClass().getClassLoader().getResource(TestConstants.SEED_GENERAL_REPORT_XML).getFile();
        String fileContentType =TestConstants.XML_CONTENTYPE_RESULT;
        String fileParamName=ConvertPushAction.CONVERT_FILE_PARAM_NAME;
        
        String param1[] = {"-999"};
        Map map = new HashMap();
		map.put(ConvertPushAction.CONVERT_ID_PARAM_NAME,param1);

		//call the request
		MockServletResponse response = TestUtils.executeActionMultipart(new ConvertPushAction(), map, fileParamName, seedFile, fileContentType);
			
		assertEquals(TestConstants.XML_CONTENTYPE_RESULT,response.getContentType());
		assertEquals(400,response.getStatus());
		
		//System.out.println(response.getOutputStream().toString());
		//test if the converion result contains some text from seed..xml file
		assertTrue(response.getOutputStream().toString().indexOf(XMLErrorResult.ERROR_TAG)>0);

		//check if the result is well-formed XML
		IXmlCtx x = new XmlContext();
		x.setWellFormednessChecking();
		x.checkFromInputStream(new ByteArrayInputStream(((MockServletOutputStream)response.getOutputStream()).toByteArray()));
	}
}
