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

import eionet.gdem.XMLConvException;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.web.listeners.JobScheduler;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;


/**
 * Testing InterruptLongRunningJobsTask methods.
 *
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("eionet.gdem.qa.InterruptLongRunningJobsTask")
@PrepareForTest(JobScheduler.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*","javax.xml.parsers.*","org.xml.*","org.w3c.*","javax.management.*","org.apache.log4j.*"})
public class InterruptLongRunningJobsTaskTest {

    @Mock
    WorkqueueManager jobsManager;

    @Mock
    SchemaManager schemaManager;

    @Spy
    @InjectMocks
    InterruptLongRunningJobsTask intTask;

    private List<WorkqueueJob> jobs;
    private WorkqueueJob job;
    private String schemaUrl = "schemaUrl";
    private Scheduler mockScheduler;
    private JobExecutionContext jobExecutionContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jobs = new ArrayList<>();
        job = new WorkqueueJob();
        job.setDuration(Long.valueOf(100));
        job.setUrl("jobUrl");
        jobs.add(job);
        jobExecutionContext = Mockito.mock(JobExecutionContext.class);
    }

    @Test
    public void testExecute() throws Exception {
        when(jobsManager.getRunningJobs()).thenReturn(jobs);
        doReturn(schemaUrl).when(intTask.findSchemaFromXml(Mockito.anyString()));
        when(schemaManager.getSchemaMaxExecutionTime(Mockito.anyString())).thenReturn(Long.valueOf(50));
        PowerMockito.mockStatic(JobScheduler.class);
        mockScheduler = PowerMockito.mock(Scheduler.class);
//        PowerMockito.when(JobScheduler.class, "getQuartzScheduler").thenReturn(mockScheduler);
        PowerMockito.when(JobScheduler.getQuartzScheduler()).thenReturn(mockScheduler);
        PowerMockito.when(mockScheduler.checkExists(Mockito.any(JobKey.class))).thenReturn(true);
        PowerMockito.doNothing().when(mockScheduler.interrupt(Mockito.any(JobKey.class)));
        intTask.execute(jobExecutionContext);
        Mockito.verify(intTask).execute(jobExecutionContext);
    }

}





















