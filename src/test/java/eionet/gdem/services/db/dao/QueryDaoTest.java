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

import eionet.gdem.qa.IQueryDao;
import eionet.gdem.qa.QaScriptView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

/**
 * @author Enriko Käsper, Tieto Estonia QueryDaoTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryDaoTest {

    @Autowired
    private DataSource db;

    @Autowired
    private IQueryDao queryDao;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QA_XML);
    }

    /**
     * The method adds QA script into DB, then it edits the properties and finally deletes the added query. After each operation it
     * checks the properties values.
     *
     * @throws Exception
     */
    @Test
    public void testQueryMethods() throws Exception {

        String queryFileName = "script.xquery";
        String description = "test QA script";
        String schemaID = "83";
        String shortName = "New QA script";
        String content_type = "HTML";
        String script_type = "xquery";
        String upperLimit = "100";
        String url = "http://url.com";

        // get all uploaded queries
        List queries = queryDao.listQueries(null);
        // count queries
        int countQueries = queries.size();

        // add query int db
        String queryId =
                queryDao.addQuery(schemaID, shortName, queryFileName, description, content_type, script_type, upperLimit, url);

        // count queries
        List queries2 = queryDao.listQueries(null);
        int countQueries2 = queries2.size();

        // check if the number of queries is increased
        assertEquals(countQueries + 1, countQueries2);

        // get the query Object BY query ID
        HashMap query = queryDao.getQueryInfo(queryId);

        assertEquals(query.get(QaScriptView.SCHEMA_ID), schemaID);
        assertEquals(query.get(QaScriptView.QUERY), queryFileName);
        assertEquals(query.get(QaScriptView.DESCRIPTION), description);
        assertEquals(query.get(QaScriptView.SCHEMA_ID), schemaID);
        assertEquals(query.get(QaScriptView.SHORT_NAME), shortName);
        assertEquals(query.get(QaScriptView.CONTENT_TYPE), content_type);
        assertEquals(query.get(QaScriptView.SCRIPT_TYPE), script_type);
        assertEquals(query.get(QaScriptView.UPPER_LIMIT), upperLimit);
        assertEquals(query.get(QaScriptView.URL), url);
        assertEquals(query.get(QaScriptView.IS_ACTIVE), "1");
        // check boolean methods
        assertTrue(queryDao.checkQueryFile(queryFileName));
        assertTrue(queryDao.checkQueryFile(queryId, queryFileName));

        // upadate query fileds
        queryDao.updateQuery(queryId, schemaID, shortName + "UPD", description + "UPD", queryFileName, content_type, script_type, upperLimit, url);

        // Get query by ID and test if all upadted fields are in DB
        query = queryDao.getQueryInfo(queryId);
        assertEquals(query.get(QaScriptView.DESCRIPTION), description + "UPD");
        assertEquals(query.get(QaScriptView.SHORT_NAME), shortName + "UPD");

        //deactivate QA Script in order to 
        queryDao.deactivateQuery(queryId);
        query = queryDao.getQueryInfo(queryId);
        assertEquals(query.get(QaScriptView.IS_ACTIVE), "0");
        
        //reactivate QA Script in order to 
        queryDao.activateQuery(queryId);
        query = queryDao.getQueryInfo(queryId);
        assertEquals(query.get(QaScriptView.IS_ACTIVE), "1");
        
        // delete inserted query
        queryDao.removeQuery(queryId);

        // count queries
        List queries3 = queryDao.listQueries(null);
        int countQueries3 = queries3.size();

        // check if the nuber of schemas is the same as in the beginning
        assertEquals(countQueries, countQueries3);
    }
}
