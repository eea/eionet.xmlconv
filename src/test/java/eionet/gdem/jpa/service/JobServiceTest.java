package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.web.spring.schemas.ISchemaDao;
import eionet.gdem.web.spring.schemas.SchemaMySqlDao;
import eionet.gdem.web.spring.workqueue.JobMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private QueryHistoryService queryHistoryService;

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
    public void testFindById() throws DatabaseException {
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
    public void testSave() throws DatabaseException {
        when(jobRepository.save(any(JobEntry.class))).thenReturn(jobEntry);
        jobServiceImpl.saveOrUpdate(jobEntry);
        verify(jobServiceImpl).saveOrUpdate(jobEntry);
    }

    @Test
    public void testGetRetryCounter() throws DatabaseException {
        when(jobRepository.getRetryCounter(1)).thenReturn(2);
        Integer result = jobServiceImpl.getRetryCounter(1);
        assertThat(result, is(2));
    }

    @Test
    public void testGetNumberOfTotalJobs() {
        when(jobRepository.count()).thenReturn(3L);
        assertThat(jobServiceImpl.getNumberOfTotalJobs(), is(3));
    }

    @Test
    public void testGetNumberOfTotalJobsForZero() {
        when(jobRepository.count()).thenReturn(0L);
        assertThat(jobServiceImpl.getNumberOfTotalJobs(), is(0));
    }

    @Test
    public void testGetJobsMetadata() {
        Timestamp timestamp = new Timestamp(new Date().getTime());

        jobEntry.setTimestamp(timestamp);
        jobEntry.setSrcFile("test");
        jobEntry.setFile("test");
        jobEntry.setnStatus(2);
        jobEntry.setQueryId(3);
        jobEntry.setInstance("test");
        jobEntry.setJobType("test");
        jobEntry.setResultFile("test");
        jobEntry.setUrl("test");

        List<JobMetadata> jobMetadataListResult = jobServiceImpl.getJobsMetadata(jobEntries);
        assertThat(jobMetadataListResult.size(), is(1));
        assertThat(jobMetadataListResult.get(0).getJobId(), is("1"));
        assertThat(jobMetadataListResult.get(0).getJobExecutorName(), is("demoExecutor"));
        assertThat(jobMetadataListResult.get(0).getTimestamp(), is(jobEntry.getTimestamp().toString()));
        assertThat(jobMetadataListResult.get(0).getFileName(), is("test"));
        assertThat(jobMetadataListResult.get(0).getScript_file().get(0), is("test"));
        assertThat(jobMetadataListResult.get(0).getStatus(), is(2));
        assertThat(jobMetadataListResult.get(0).getStatusName(), is("PROCESSING"));
        assertThat(jobMetadataListResult.get(0).getScriptId(), is("3"));
        assertThat(jobMetadataListResult.get(0).getInstance(), is("test"));
        assertThat(jobMetadataListResult.get(0).getJobType(), is("test"));
        assertThat(jobMetadataListResult.get(0).getResult_file(), is(nullValue()));
        assertThat(jobMetadataListResult.get(0).getUrl(), is("test"));
        assertThat(jobMetadataListResult.get(0).getUrl_name(), is("test"));
    }

    @Test
    public void testGetDuplicateIdentifierForSchemaValidation() throws SQLException, IOException {
        String documentUrl = "testUrl";
        String scriptId = "-1";
        assertThat(jobServiceImpl.getDuplicateIdentifier(documentUrl, scriptId), is(nullValue()));
        when(jobServiceImpl.getHashFromCdrBdrForFile(documentUrl)).thenReturn("testHash");
        assertThat(jobServiceImpl.getDuplicateIdentifier(documentUrl, scriptId), is("testUrl_testHash_-1"));
    }

    @Test
    public void testGetDuplicateIdentifierForScriptExecutionWithoutHistory() throws SQLException, IOException {
        String documentUrl = "testUrl";
        String scriptId = "15";
        when(jobServiceImpl.getHashFromCdrBdrForFile(documentUrl)).thenReturn("testHash");
        assertThat(jobServiceImpl.getDuplicateIdentifier(documentUrl, scriptId), is("testUrl_testHash_15"));
    }

    @Test
    public void testGetDuplicateIdentifierForScriptExecutionWithHistory() throws SQLException, IOException {
        String documentUrl = "testUrl";
        String scriptId = "15";
        QueryHistoryEntry queryHistoryEntry = new QueryHistoryEntry();
        Date date = new Date();
        queryHistoryEntry.setDateModified(date);
        when(jobServiceImpl.getHashFromCdrBdrForFile(documentUrl)).thenReturn("testHash");
        when(queryHistoryService.findLastEntryByQueryId(Integer.valueOf(scriptId))).thenReturn(queryHistoryEntry);
        assertThat(jobServiceImpl.getDuplicateIdentifier(documentUrl, scriptId), is("testUrl_testHash_15_"+ date.toString()));
    }

    @Test
    public void testGetDuplicateIdentifierForScriptExecutionOfEnvelopeWithoutHistory() {
        String documentUrl = "testUrl/xml";
        String scriptId = "15";
        assertThat(jobServiceImpl.getDuplicateIdentifier(documentUrl, scriptId), is("testUrl/xml_15"));
    }

    @Test
    public void testGetDuplicateIdentifierForScriptExecutionOfEnvelopeWithHistory() {
        String documentUrl = "testUrl/xml";
        String scriptId = "15";
        QueryHistoryEntry queryHistoryEntry = new QueryHistoryEntry();
        Date date = new Date();
        queryHistoryEntry.setDateModified(date);
        when(queryHistoryService.findLastEntryByQueryId(Integer.valueOf(scriptId))).thenReturn(queryHistoryEntry);
        assertThat(jobServiceImpl.getDuplicateIdentifier(documentUrl, scriptId), is("testUrl/xml_15_"+ date.toString()));
    }
}













