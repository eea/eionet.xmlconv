package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Spy
    @InjectMocks
    private JobServiceImpl jobServiceImpl;

    private InternalSchedulingStatus internalStatus;
    private List<JobEntry> jobEntries;
    private JobEntry jobEntry;
    private Timestamp timestamp;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        jobEntry = new JobEntry().setId(1).setJobExecutorName("demoExecutor");
        jobEntries = new ArrayList<>();
        jobEntries.add(jobEntry);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse("23/12/2021");
        long time = date.getTime();
        timestamp = new Timestamp(time);
        internalStatus = new InternalSchedulingStatus(3);
    }

    @Test
    public void testChangeNStatus() throws DatabaseException {
        doNothing().when(jobRepository).updateJobNStatus(anyInt(), anyString(), any(Timestamp.class), anyInt());
        jobServiceImpl.changeNStatus(1, 0);
        verify(jobServiceImpl).changeNStatus(1, 0);
    }

    @Test
    public void testChangeNStatusAndInternalStatus() throws DatabaseException {
        doNothing().when(jobRepository).updateJobNStatusAndInternalStatus(anyInt(), anyInt(), anyString(), any(Timestamp.class), anyInt());
        jobServiceImpl.changeNStatusAndInternalStatus(1, 0, 3);
        verify(jobServiceImpl).changeNStatusAndInternalStatus(1, 0, 3);
    }

    @Test
    public void testUpdateJob() throws DatabaseException {
        doNothing().when(jobRepository).updateJob(anyInt(), any(InternalSchedulingStatus.class), anyString(), any(Timestamp.class), anyBoolean(), anyLong(), anyInt());
        jobServiceImpl.updateJob(0, internalStatus, "demoExecutor", timestamp, jobEntry);
        verify(jobServiceImpl).updateJob(0, internalStatus, "demoExecutor", timestamp, jobEntry);
    }

    @Test
    public void testFindById() {
        when(jobRepository.findById(anyInt())).thenReturn(jobEntry);
        JobEntry result = jobServiceImpl.findById(1);
        assertThat(result.getJobExecutorName(), is("demoExecutor"));
    }

    @Test
    public void testFindByIntSchedulingStatus() {
        when(jobRepository.findByIntSchedulingStatus(any(InternalSchedulingStatus.class))).thenReturn(jobEntries);
        List<JobEntry> result = jobServiceImpl.findByIntSchedulingStatus(internalStatus);
        assertThat(result.get(0).getId(), is(1));
    }

    @Test
    public void testFindByIntSchedulingStatusAndIsHeavy() {
        when(jobRepository.findByIntSchedulingStatusAndIsHeavy(any(InternalSchedulingStatus.class), anyBoolean())).thenReturn(jobEntries);
        List<JobEntry> result = jobServiceImpl.findByIntSchedulingStatusAndIsHeavy(internalStatus, false);
        assertThat(result.get(0).getId(), is(1));
    }

    @Test
    public void testFindProcessingJobs() {
        when(jobRepository.findProcessingJobs()).thenReturn(jobEntries);
        List<JobEntry> result = jobServiceImpl.findProcessingJobs();
        assertThat(result.get(0).getId(), is(1));
    }

    @Test
    public void testUpdateWorkerRetries() {
        doNothing().when(jobRepository).updateWorkerRetries(anyInt(), any(Timestamp.class), anyInt());
        jobServiceImpl.updateWorkerRetries(2, timestamp, 1);
        verify(jobServiceImpl).updateWorkerRetries(2, timestamp, 1);
    }

    @Test
    public void testUpdateHeavyRetriesOnFailure() {
        doNothing().when(jobRepository).updateHeavyRetriesOnFailure(anyInt(), any(Timestamp.class), anyInt());
        jobServiceImpl.updateHeavyRetriesOnFailure(2, timestamp, 1);
        verify(jobServiceImpl).updateHeavyRetriesOnFailure(2, timestamp, 1);
    }

    @Test
    public void testSave() {
        when(jobRepository.save(any(JobEntry.class))).thenReturn(jobEntry);
        jobServiceImpl.save(jobEntry);
        verify(jobServiceImpl).save(jobEntry);
    }

    @Test
    public void testGetRetryCounter() {
        when(jobRepository.getRetryCounter(1)).thenReturn(2);
        Integer result = jobServiceImpl.getRetryCounter(1);
        assertThat(result, is(2));
    }
}













