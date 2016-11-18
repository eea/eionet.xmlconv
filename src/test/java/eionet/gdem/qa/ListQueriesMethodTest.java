package eionet.gdem.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.Vector;

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
 * This unittest tests the QA Service listQueries and listQAScripts method
 *
 * @author Enriko Käsper, TietoEnator Estonia AS ListConversionsMethodTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ListQueriesMethodTest {

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
     * Tests that the result of listConversions method contains the right data as defined in seed xml file.
     */
    @Test
    public void testListConversionsXSDResult() throws Exception {

        ListQueriesMethod qm = new ListQueriesMethod();

        Vector v = qm.listQueries("http://cdrtest.eionet.eu.int/xmlexports/dir9243eec/schema.xsd");
        assertTrue(v.size() == 1);
        Hashtable ht = (Hashtable) v.get(0);

        assertEquals(ht.get(QaScriptView.TYPE), "xsd");
        assertEquals(ht.get(QaScriptView.CONTENT_TYPE_ID), "HTML");
        assertEquals(ht.get(QaScriptView.CONTENT_TYPE_OUT), ListQueriesMethod.DEFAULT_QA_CONTENT_TYPE);
        assertEquals(ht.get(QaScriptView.UPPER_LIMIT), String.valueOf(ListQueriesMethod.VALIDATION_UPPER_LIMIT));
    }

    @Test
    public void testListConversionsXQueryResult() throws Exception {

        ListQueriesMethod qas = new ListQueriesMethod();
        // get all queries (xqueries, xml schemas, xslts)
        Vector v = qas.listQueries("http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd");
        assertTrue(v.size() == 2);

        Hashtable ht = (Hashtable) v.get(0);

        assertEquals(ht.get(QaScriptView.TYPE), Constants.QA_TYPE_XQUERY);
        assertEquals(ht.get(QaScriptView.CONTENT_TYPE_ID), "HTML");
        assertEquals(ht.get(QaScriptView.CONTENT_TYPE_OUT), ListQueriesMethod.DEFAULT_QA_CONTENT_TYPE);
        assertEquals(ht.get(QaScriptView.UPPER_LIMIT), "20");
    }

    @Test
    public void testListConversionsAllQueries() throws Exception {

        ListQueriesMethod qas = new ListQueriesMethod();

        // get all queries (xqueries, xml schemas, xslts)
        Vector v = qas.listQueries(null);
        assertTrue(v.size() > 10);

    }

}
