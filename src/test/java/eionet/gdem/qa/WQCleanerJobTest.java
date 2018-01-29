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
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */

package eionet.gdem.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

/**
 * Testing WQCleanerJob methods.
 *
 * @author Enriko Käsper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WQCleanerJobTest{

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

    @Test
    public void testCanDelete() throws Exception {
        Properties.wqJobMaxAge = 24;// 24 hours

        WorkqueueJob job = new WorkqueueJob();
        job.setStatus(Constants.XQ_PROCESSING);
        Calendar jobDate = Calendar.getInstance();
        jobDate.add(Calendar.DAY_OF_MONTH, -1);
        job.setJobTimestamp(jobDate.getTime());
        assertFalse(WQCleanerJob.canDeleteJob(job));

        jobDate.add(Calendar.DAY_OF_MONTH, -2);
        job.setJobTimestamp(jobDate.getTime());
        assertFalse(WQCleanerJob.canDeleteJob(job));

        job.setStatus(Constants.XQ_RECEIVED);
        assertFalse(WQCleanerJob.canDeleteJob(job));

        job.setStatus(Constants.XQ_PROCESSING);
        assertFalse(WQCleanerJob.canDeleteJob(job));

        job.setStatus(Constants.XQ_READY);
        assertTrue(WQCleanerJob.canDeleteJob(job));

        job.setStatus(Constants.XQ_FATAL_ERR);
        assertTrue(WQCleanerJob.canDeleteJob(job));

        job.setStatus(Constants.XQ_LIGHT_ERR);
        assertTrue(WQCleanerJob.canDeleteJob(job));
    }

    @Test
    public void testCleanWorkqueueJobs() throws Exception {
        WorkqueueManager manager = new WorkqueueManager();
        List<WorkqueueJob> jobs = manager.getFinishedJobs();
        assertTrue(jobs.size() > 0);

        WQCleanerJob cleaner = new WQCleanerJob();
        cleaner.execute(null);
        jobs = manager.getFinishedJobs();
        assertEquals(jobs.size(), 0);
    }
}
