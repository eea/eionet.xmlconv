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

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.Constants;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * Testing XQJobDao
 *
 * @author Enriko Käsper, Tieto Estonia QueryDaoTest
 */

public class XQJobDaoTest extends DBTestCase {

    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();

    /**
     * Provide a connection to the database.
     */
    public XQJobDaoTest(String name) {
        super(name);
        DbHelper.setUpConnectionProperties();
    }

    /**
     * Set up test case properties
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.setUpProperties(this);
    }

    /**
     * Load the data which will be inserted for the test
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet loadedDataSet =
            new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_QA_XML));
        return loadedDataSet;
    }

    /**
     * @throws Exception
     */
    public void testQueryMethods() throws Exception {
        assertEquals(1, xqJobDao.getJobs(Constants.XQ_PROCESSING).length);
        assertEquals(1, xqJobDao.getJobs(Constants.XQ_READY).length);
        assertEquals(1, xqJobDao.getJobs(Constants.XQ_FATAL_ERR).length);
        assertEquals(2, xqJobDao.getXQFinishedJobs().length);
        assertEquals(1, xqJobDao.countActiveJobs());
        String[] data = xqJobDao.getXQJobData("1290");
        assertEquals(data[0], "http://cdrtest.eionet.europa.eu/ee/eu/art17/envrzqn7q/gr-general-report.xml");
        assertEquals(1, xqJobDao.getJobsLimit(Constants.XQ_PROCESSING, 1).length);
        xqJobDao.changeJobStatus("1290", Constants.XQ_READY);
        assertEquals(0, xqJobDao.countActiveJobs());
        assertEquals(3, xqJobDao.getXQFinishedJobs().length);
        xqJobDao.endXQJob("1290");
        assertNull(xqJobDao.getJobs(Constants.XQ_PROCESSING));
        assertEquals(2, xqJobDao.getXQFinishedJobs().length);
    }
}
