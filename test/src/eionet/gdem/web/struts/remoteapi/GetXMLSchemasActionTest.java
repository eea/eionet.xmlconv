/*
 * Created on 18.03.2008
 */
package eionet.gdem.web.struts.remoteapi;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.dcm.results.GetXMLSchemasResult;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockServletOutputStream;
import eionet.gdem.test.mocks.MockServletResponse;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * GetXMLSchemasActionTest
 */

public class GetXMLSchemasActionTest  extends DBTestCase{

	
	/**
	 * Provide a connection to the database.
	 */
	public GetXMLSchemasActionTest(String name)	{
		super( name );
	}
	/**
	 * Set up test case properties
	 */
    protected void setUp()throws Exception{
		DbHelper.setUpConnectionProperties();
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
	 * Tests action execution
	 */
	public void testExecute() throws Exception {
		
		//call the request
		MockServletResponse response = TestUtils.executeAction(new GetXMLSchemasAction(), new HashMap());
		
		assertEquals(TestConstants.XML_CONTENTYPE_RESULT,response.getContentType());
		assertEquals(200,response.getStatus());
		
		//System.out.println(response.getOutputStream().toString());

		//check if the result is well-formed XML
		IXmlCtx x = new XmlContext();
		x.setWellFormednessChecking();
		x.checkFromInputStream(new ByteArrayInputStream(((MockServletOutputStream)response.getOutputStream()).toByteArray()));
		
		//count the schemas found from returned XML
		IXQuery xQuery=x.getQueryManager();
		List schemas = xQuery.getElementValues(GetXMLSchemasResult.SCHEMA_TAG);
		assertTrue(schemas.size()>36);
	}

}
