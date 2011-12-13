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

import java.util.Vector;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.Constants;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * This unittest tests the QA Service listQAScripts method.
 * 
 * @author Enriko Käsper
 */

public class ListQAScriptsMethodTest extends DBTestCase {

    /**
     * Provide a connection to the database.
     */
    public ListQAScriptsMethodTest(String name) {
        super(name);
        DbHelper.setUpConnectionProperties();
    }

    /**
     * Set up test case properties
     */
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.setUpProperties(this);
    }

    /**
     * Load the data which will be inserted for the test
     */
    protected IDataSet getDataSet() throws Exception {
        IDataSet loadedDataSet =
                new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_QA_XML));
        return loadedDataSet;
    }

    /**
     * Tests that the result of listQAScripts method contains the right data as defined in seed xml file.
     */
    public void testListConversionsXSDResult() throws Exception {

        XQueryService qm = new XQueryService();

        Vector listQaResult = qm.listQAScripts("http://cdrtest.eionet.eu.int/xmlexports/dir9243eec/schema.xsd");
        assertTrue(listQaResult.size() == 1);
        Vector resultQuery = (Vector) listQaResult.get(0);

        assertEquals((String) resultQuery.get(0), String.valueOf(Constants.JOB_VALIDATION));
        assertEquals((String) resultQuery.get(2), "");
        assertEquals((String) resultQuery.get(3), String.valueOf(ListQueriesMethod.VALIDATION_UPPER_LIMIT));
    }

    public void testListConversionsXQueryResult() throws Exception {

        ListQueriesMethod qas = new ListQueriesMethod();
        // get all queries (xqueries, xml schemas, xslts)
        Vector listQaResult = qas.listQAScripts("http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd");
        assertTrue(listQaResult.size() == 2);

        Vector ht = (Vector) listQaResult.get(0);

        assertEquals((String) ht.get(0), "48");
        assertEquals((String) ht.get(1), "Article 17 - General report species check");
        assertEquals((String) ht.get(3), "20");
    }
}
