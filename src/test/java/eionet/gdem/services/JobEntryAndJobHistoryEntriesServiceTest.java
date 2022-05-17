package eionet.gdem.services;

import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.services.impl.JobEntryAndJobHistoryEntriesServiceImpl;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.web.spring.workqueue.JobEntryAndJobHistoryEntriesObject;
import eionet.gdem.web.spring.workqueue.JobHistoryMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class JobEntryAndJobHistoryEntriesServiceTest {

    @Mock
    private JobService jobService;

    @Mock
    private JobHistoryService jobHistoryService;

    @InjectMocks
    private JobEntryAndJobHistoryEntriesServiceImpl jobEntryAndJobHistoryEntriesServiceImpl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetJobEntryAndJobHistoryEntriesOfJob() throws DatabaseException {
        JobEntry jobEntry = new JobEntry().setId(4).setTimestamp(new Timestamp(new Date().getTime()));
        Timestamp timestamp = new Timestamp(new Date().getTime());
        JobHistoryEntry jobHistoryEntry = new JobHistoryEntry().setFullStatusName("READY").setDateAdded(timestamp).setJobExecutorName("testExecutor");
        List<JobHistoryEntry> jobHistoryEntries = new ArrayList<>();
        jobHistoryEntries.add(jobHistoryEntry);
        when(jobService.findById(anyInt())).thenReturn(jobEntry);
        when(jobHistoryService.getJobHistoryEntriesOfJob(anyString())).thenReturn(jobHistoryEntries);
        List<JobHistoryMetadata> jobHistoryMetadataList = jobEntryAndJobHistoryEntriesServiceImpl.getJobHistoryMetadata("4");
        assertThat(jobHistoryMetadataList.size(), is(1));
        assertThat(jobHistoryMetadataList.get(0).getStatus_name(), is("READY"));
        assertThat(jobHistoryMetadataList.get(0).getDate_added(), is(timestamp.toString()));
        assertThat(jobHistoryMetadataList.get(0).getJob_executor_name(), is("testExecutor"));
    }
}

















