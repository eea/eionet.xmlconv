package eionet.gdem.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import eionet.gdem.XMLConvException;
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

        List<Hashtable> list = qm.listQueries("http://cdrtest.eionet.eu.int/xmlexports/dir9243eec/schema.xsd");
        assertTrue(list.size() == 1);
        Hashtable ht = list.get(0);

        assertEquals(ht.get(ListQueriesMethod.KEY_TYPE), "xsd");
        assertEquals(ht.get(ListQueriesMethod.KEY_CONTENT_TYPE_ID), "HTML");
        assertEquals(ht.get(ListQueriesMethod.KEY_CONTENT_TYPE_OUT), ListQueriesMethod.DEFAULT_QA_CONTENT_TYPE);
        assertEquals(ht.get(ListQueriesMethod.KEY_UPPER_LIMIT), String.valueOf(ListQueriesMethod.VALIDATION_UPPER_LIMIT));
    }

    @Test
    public void testListConversionsXQueryResult() throws Exception {

        ListQueriesMethod qas = new ListQueriesMethod();
        // get all queries (xqueries, xml schemas, xslts)
        List<Hashtable> list = qas.listQueries("http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd");
        assertTrue(list.size() == 2);

        Hashtable ht = (Hashtable) list.get(0);

        assertEquals(ht.get(ListQueriesMethod.KEY_TYPE), Constants.QA_TYPE_XQUERY);
        assertEquals(ht.get(ListQueriesMethod.KEY_CONTENT_TYPE_ID), "HTML");
        assertEquals(ht.get(ListQueriesMethod.KEY_CONTENT_TYPE_OUT), ListQueriesMethod.DEFAULT_QA_CONTENT_TYPE);
        assertEquals(ht.get(ListQueriesMethod.KEY_UPPER_LIMIT), "20");
    }

    @Test
    public void testListConversionsAllQueries() throws Exception {

        ListQueriesMethod qas = new ListQueriesMethod();

        // get all queries (xqueries, xml schemas, xslts)
        List<Hashtable> list = qas.listQueries(null);
        assertTrue(list.size() > 10);

    }


    @Test
    public void testListQAScriptsKeys() throws XMLConvException {
        ListQueriesMethod qas = new ListQueriesMethod();
        List<Hashtable> listQaResult = qas.listQueries("http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd");
        Hashtable ht = (Hashtable) listQaResult.get(0);
        assertTrue(ht.containsKey(ListQueriesMethod.KEY_QUERY_ID));
        assertTrue(ht.containsKey(ListQueriesMethod.KEY_SHORT_NAME));
        assertTrue(ht.containsKey(ListQueriesMethod.KEY_QUERY));
        assertTrue(ht.containsKey(ListQueriesMethod.KEY_DESCRIPTION));
        assertTrue(ht.containsKey(ListQueriesMethod.KEY_SCHEMA_ID));
        assertTrue(ht.containsKey(ListQueriesMethod.KEY_XML_SCHEMA));
        assertTrue(ht.containsKey(ListQueriesMethod.KEY_CONTENT_TYPE_ID));
        assertTrue(ht.containsKey(ListQueriesMethod.KEY_CONTENT_TYPE_OUT));
        assertTrue(ht.containsKey(ListQueriesMethod.KEY_TYPE));
        assertTrue(ht.containsKey(ListQueriesMethod.KEY_UPPER_LIMIT));
    }

}
