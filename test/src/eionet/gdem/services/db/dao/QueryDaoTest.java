/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Content Registry 2.0.
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 * 
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.services.db.dao;

import java.util.HashMap;
import java.util.List;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, Tieto Estonia
 * QueryDaoTest
 */

public class QueryDaoTest extends DBTestCase{

	private  IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();

	/**
	 * Provide a connection to the database.
	 */
	public QueryDaoTest(String name)	{
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
	 * The method adds QA script into DB, then it edits the properties and finally deletes the added query.
	 * After each operation it checks the properties values.
	 * 
	 * @throws Exception
	 */
	public void testQueryMethods() throws Exception{
		
		String queryFileName = "script.xquery";
		String description = "test QA script";
		String schemaID = "83";
		String shortName = "New QA script";
		String content_type ="HTML";
		String script_type ="xquery";
		
		//get all uploaded queries
		List queries = queryDao.listQueries(null);
		//count queries
		int countQueries = queries.size();
		
		//add query int db
		String queryId = queryDao.addQuery(schemaID, shortName, queryFileName, description, content_type, script_type);
		
		//count queries
		List queries2 = queryDao.listQueries(null);
		int countQueries2 = queries2.size();
		
		//check if the number of queries is increased
		assertEquals(countQueries+1,countQueries2);
		
		//get the query Object BY query ID
		HashMap query = queryDao.getQueryInfo(queryId);
		
		assertEquals((String)query.get("schema_id"),schemaID);
		assertEquals((String)query.get("query"),queryFileName);
		assertEquals((String)query.get("description"),description);
		assertEquals((String)query.get("schema_id"),schemaID);
		assertEquals((String)query.get("short_name"),shortName);
		assertEquals((String)query.get("content_type"),content_type);
		assertEquals((String)query.get("script_type"),script_type);
		
		//check boolean methods
		assertTrue(queryDao.checkQueryFile(queryFileName));
		assertTrue(queryDao.checkQueryFile(queryId, queryFileName));

		//upadate query fileds
		queryDao.updateQuery(queryId, schemaID, shortName + "UPD", description + "UPD",queryFileName, content_type, script_type);
		
		//Get query by ID and test if all upadted fields are in DB
		query = queryDao.getQueryInfo(queryId);
		assertEquals((String)query.get("description"),description + "UPD");
		assertEquals((String)query.get("short_name"),shortName + "UPD");
				
		//delete inserted query
		queryDao.removeQuery(queryId);
		
		//count queries
		List queries3 = queryDao.listQueries(null);
		int countQueries3 = queries3.size();
		
		//check if the nuber of schemas is the same as in the beginning
		assertEquals(countQueries,countQueries3);
	}}