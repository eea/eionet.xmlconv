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
 * The Original Code is "XMLCONV - Converters and QA Service"
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s): Enriko Käsper
 */
package eionet.gdem.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.Constants;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

/**
 * This unittest tests the QA Service listQAScripts method.
 *
 * @author Enriko Käsper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ListQAScriptsMethodTest {

    @Autowired
    private DataSource db;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QA_XML);
    }

    /**
     * Tests that the result of listQAScripts method contains the right data as defined in seed xml file.
     */
    @Test
    public void testListConversionsXSDResult() throws Exception {

        XQueryService qm = new XQueryService();

        Vector listQaResult = qm.listQAScripts("http://cdrtest.eionet.eu.int/xmlexports/dir9243eec/schema.xsd");
        assertTrue(listQaResult.size() == 1);
        Vector resultQuery = (Vector) listQaResult.get(0);

        assertEquals(resultQuery.get(0), String.valueOf(Constants.JOB_VALIDATION));
        assertEquals(resultQuery.get(2), "");
        assertEquals(resultQuery.get(3), String.valueOf(ListQueriesMethod.VALIDATION_UPPER_LIMIT));
    }

    @Test
    public void testListConversionsXQueryResult() throws Exception {

        ListQueriesMethod qas = new ListQueriesMethod();
        // get all queries (xqueries, xml schemas, xslts)
        Vector listQaResult = qas.listQAScripts("http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd");
        assertTrue(listQaResult.size() == 2);

        Vector ht = (Vector) listQaResult.get(0);

        assertEquals(ht.get(0), "48");
        System.out.println(ht.get(1) +"inside test method ");
        assertEquals(ht.get(1), "Checks species names and suggest fuzzy matching alternatves if found from the list of European species");
        assertEquals(ht.get(3), "20");
    }
}
