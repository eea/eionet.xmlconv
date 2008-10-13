package eionet.gdem.qa;

import java.util.Hashtable;
import java.util.Vector;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.Constants;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * This unittest tests the QA  Service listQueries and listQAScripts method
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ListConversionsMethodTest
 */

public class ListQueriesMethodTest  extends DBTestCase{


	/**
	 * Provide a connection to the database.
	 */
	public ListQueriesMethodTest(String name)
	{
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
						TestConstants.SEED_DATASET_QA_XML));
		return loadedDataSet;
	}
	/**
	 * Tests that the result of listConversions method contains the right data as defined in seed xml file.
	 */
	public void testListConversionsXSDResult() throws Exception {

		ListQueriesMethod qm = new ListQueriesMethod();

		Vector v = qm.listQueries("http://cdrtest.eionet.eu.int/xmlexports/dir9243eec/schema.xsd");
		assertTrue(v.size()==1);
		Hashtable ht = (Hashtable)v.get(0);
		
		assertEquals((String)ht.get(ListQueriesMethod.KEY_TYPE),"xsd");
		assertEquals((String)ht.get(ListQueriesMethod.KEY_CONTENT_TYPE_ID),"HTML");
		assertEquals((String)ht.get(ListQueriesMethod.KEY_CONTENT_TYPE_OUT),ListQueriesMethod.DEFAULT_QA_CONTENT_TYPE);
	}    	
	public void testListConversionsXQueryResult() throws Exception {

		ListQueriesMethod qas = new ListQueriesMethod();
		//get all queries (xqueries, xml schemas, xslts)
		Vector v = qas.listQueries("http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd");
		assertTrue(v.size()==2);

		Hashtable ht = (Hashtable)v.get(0);

		assertEquals((String)ht.get(ListQueriesMethod.KEY_TYPE),Constants.QA_TYPE_XQUERY);
		assertEquals((String)ht.get(ListQueriesMethod.KEY_CONTENT_TYPE_ID),"HTML");
		assertEquals((String)ht.get(ListQueriesMethod.KEY_CONTENT_TYPE_OUT),ListQueriesMethod.DEFAULT_QA_CONTENT_TYPE);
	}    	
	public void testListConversionsAllQueries() throws Exception {

		ListQueriesMethod qas = new ListQueriesMethod();

		//get all queries (xqueries, xml schemas, xslts)
		Vector v = qas.listQueries(null);
		assertTrue(v.size()==10);

	}    	

}
