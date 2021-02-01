package eionet.gdem.qa;

import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;


/**
 * Testing InterruptLongRunningJobsTask methods.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class InterruptLongRunningJobsTaskTest {

    @Mock
    WorkqueueManager jobsManager;

    @Mock
    SchemaManager schemaManager;

    @Mock
    JobExecutionContext jobExecutionContext;

    @Spy
    @InjectMocks
    InterruptLongRunningJobsTask intTask;

    private List<WorkqueueJob> jobs;
    private WorkqueueJob job;
    private String schemaUrl = "schemaUrl";
    private Scheduler mockScheduler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jobs = new ArrayList<>();
        job = new WorkqueueJob();
        job.setJobId("646541");
        job.setDuration(Long.valueOf(100));
        job.setUrl("jobUrl");
        jobs.add(job);
        mockScheduler = Mockito.mock(Scheduler.class);
    }

    @Test
    public void testExecute() throws Exception {
        when(jobsManager.getRunningJobs()).thenReturn(jobs);
        doReturn(schemaUrl).when(intTask).findSchemaFromXml(Mockito.anyString());
        when(schemaManager.getSchemaMaxExecutionTime(Mockito.anyString())).thenReturn(Long.valueOf(50));
        doReturn(mockScheduler).when(intTask).getScheduler();
        when(mockScheduler.checkExists(Mockito.any(JobKey.class))).thenReturn(true);
        doReturn(true).when(mockScheduler).interrupt(Mockito.any(JobKey.class));
        intTask.execute(jobExecutionContext);
        Mockito.verify(intTask).execute(jobExecutionContext);
    }

}





















