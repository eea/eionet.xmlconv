/*
 * Created on 29.10.2008
 */
package eionet.gdem.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.web.spring.workqueue.IXQJobDao;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS QueryServiceTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QueryServiceTest {

    @Autowired
    private DataSource db;

    @Autowired
    private IQueryDao queryDao;
     
    @Autowired
    private IXQJobDao xqJobDao;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QA_XML);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    @Test
    public void testListQAScriptsDeactivated () throws Exception {
        QueryService qm = new QueryService();
        queryDao.deactivateQuery("26");
        Vector listQaResult = qm.listQAScripts("60");
        assertTrue(listQaResult.size()==0);
        
        queryDao.activateQuery("26");
        listQaResult = qm.listQAScripts("60");
        assertTrue(listQaResult.size()==1);
    }
    
    @Test
    public void testListQueriesDeactivated () throws Exception {
        QueryService qm = new QueryService();
        queryDao.deactivateQuery("26");
        List<Hashtable> listQaResult = qm.listQueries("http://dd.eionet.europa.eu/namespace.jsp?ns_id=200 http://dd.eionet.europa.eu/GetSchema?id=TBL1919");
        assertTrue(listQaResult.size()==0);
        
        queryDao.activateQuery("26");
        listQaResult = qm.listQueries("http://dd.eionet.europa.eu/namespace.jsp?ns_id=200 http://dd.eionet.europa.eu/GetSchema?id=TBL1919");
        assertTrue(listQaResult.size()==1);
    }



}
