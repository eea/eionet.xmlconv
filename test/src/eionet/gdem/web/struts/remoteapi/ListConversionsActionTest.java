/*
 * Created on 18.03.2008
 */
package eionet.gdem.web.struts.remoteapi;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.dcm.results.GetXMLSchemasResult;
import eionet.gdem.dcm.results.ListConversionsResult;
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
 * testListConversionsTest
 */

public class ListConversionsActionTest  extends DBTestCase{

	
	/**
	 * Provide a connection to the database.
	 */
	public ListConversionsActionTest(String name)	{
		super( name );
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
		MockServletResponse response = TestUtils.executeAction(new ListConversionsAction(), new HashMap());
		
		assertEquals(TestConstants.XML_CONTENTYPE_RESULT,response.getContentType());
		assertEquals(200,response.getStatus());
		
		//System.out.println(response.getOutputStream().toString());

		//check if the result is well-formed XML
		IXmlCtx x = new XmlContext();
		x.setWellFormednessChecking();
		x.checkFromInputStream(new ByteArrayInputStream(((MockServletOutputStream)response.getOutputStream()).toByteArray()));
		
		//count the conversions found from returned XML
		IXQuery xQuery=x.getQueryManager();
		List convertIds = xQuery.getElementValues(ListConversionsResult.CONVERT_ID_TAG);
		assertTrue(convertIds.size()>83);
	}

	/**
	 * Tests action execution with parameters
	 */
	public void testExecuteWithParams() throws Exception {

		String schema = "http://waste.eionet.europa.eu/schemas/waste/schema.xsd";
        String param1[] = {schema};
        Map map = new HashMap();
		map.put(ListConversionsAction.SCHEMA_PARAM_NAME,param1);
		
		//call the request
		MockServletResponse response = TestUtils.executeAction(new ListConversionsAction(),map);
		
		assertEquals(TestConstants.XML_CONTENTYPE_RESULT,response.getContentType());
		assertEquals(200,response.getStatus());
		
		//System.out.println(response.getOutputStream().toString());

		//check if the result is well-formed XML
		IXmlCtx x = new XmlContext();
		x.setWellFormednessChecking();
		x.checkFromInputStream(new ByteArrayInputStream(((MockServletOutputStream)response.getOutputStream()).toByteArray()));
		
		//There should be 3 conversions
		IXQuery xQuery=x.getQueryManager();
		List conversions = xQuery.getElements(ListConversionsResult.CONVERSION_TAG);
		assertEquals(3,conversions.size());
		
		//validate the converison Ids
		List converIds = xQuery.getElementValues(ListConversionsResult.CONVERT_ID_TAG);
		assertEquals("169",(String)converIds.get(0));
		assertEquals("171",(String)converIds.get(1));
		
		//validate the xsl file names
		List xsl = xQuery.getElementValues(ListConversionsResult.XSL_TAG);
		assertEquals("dir75442_excel.xsl",(String)xsl.get(0));
		assertEquals("dir75442_html.xsl",(String)xsl.get(1));
		
		//validate content types
		List contentTypes = xQuery.getElementValues(ListConversionsResult.CONTENT_TYPE_TAG);
		assertEquals(TestConstants.EXCEl_CONTENTYPE_RESULT,(String)contentTypes.get(0));
		assertEquals(TestConstants.HTML_CONTENTYPE_RESULT,(String)contentTypes.get(1));

		//validate result types
		List resultTypes = xQuery.getElementValues(ListConversionsResult.RESULT_TYPE_TAG);
		assertEquals("EXCEL",(String)resultTypes.get(0));
		assertEquals("HTML",(String)resultTypes.get(1));

		//validate schemas
		List schemas = xQuery.getElementValues(ListConversionsResult.XML_SCHEMA_TAG);
		assertEquals(schema,(String)schemas.get(0));
		assertEquals(schema,(String)schemas.get(1));
	}
}
