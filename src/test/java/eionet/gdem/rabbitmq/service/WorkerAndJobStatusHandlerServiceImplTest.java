package eionet.gdem.rabbitmq.service;

import eionet.gdem.Constants;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.rabbitMQ.model.WorkerJobRabbitMQRequestMessage;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageSender;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerServiceImpl;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkerAndJobStatusHandlerServiceImplTest {

    @Mock
    JobService jobService;
    @Mock
    JobHistoryService jobHistoryService;
    @Mock
    JobExecutorService jobExecutorService;
    @Mock
    JobExecutorHistoryService jobExecutorHistoryService;
    @Mock
    RabbitMQMessageSender rabbitMQMessageSender;
    @InjectMocks
    WorkerAndJobStatusHandlerServiceImpl workerAndJobStatusHandlerServiceImpl;
    InternalSchedulingStatus internalStatus;
    JobEntry jobEntry;
    JobExecutor jobExecutor;
    JobExecutorHistory jobExecutorHistory;

    @Before
    public void setUp() throws DatabaseException {
        MockitoAnnotations.initMocks(this);
        internalStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_QUEUED);
        jobEntry = new JobEntry().setId(100).setJobExecutorName("demoJobExecutor");;
        jobExecutor = new JobExecutor().setContainerId("123456").setHeartBeatQueue("demoJobExecutor-queue");;
        jobExecutorHistory = new JobExecutorHistory();
        doNothing().when(jobService).updateJob(anyInt(), any(InternalSchedulingStatus.class), anyString(), any(Timestamp.class), any(JobEntry.class));
        doNothing().when(jobHistoryService).updateJobHistory(anyInt(), anyInt(), any(JobEntry.class));
        doNothing().when(jobExecutorService).saveOrUpdateJobExecutor(anyBoolean(), any(JobExecutor.class));
        doNothing().when(jobExecutorHistoryService).saveJobExecutorHistoryEntry(any(JobExecutorHistory.class));
    }

    @Test
    public void testUpdateJobAndJobHistoryEntries() throws DatabaseException {
        workerAndJobStatusHandlerServiceImpl.updateJobAndJobHistoryEntries(Constants.XQ_PROCESSING, internalStatus, jobEntry);
        verify(jobHistoryService).updateJobHistory(anyInt(), anyInt(), any(JobEntry.class));
    }

    @Test
    public void testSaveOrUpdateJobExecutor() throws DatabaseException {
        workerAndJobStatusHandlerServiceImpl.saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);
        verify(jobExecutorService).saveOrUpdateJobExecutor(anyBoolean(), any(JobExecutor.class));
    }

    @Test
    public void testUpdateJobAndJobExecTables() throws DatabaseException {
        workerAndJobStatusHandlerServiceImpl.updateJobAndJobExecTables(Constants.XQ_PROCESSING, internalStatus, jobEntry, jobExecutor, jobExecutorHistory);
        verify(jobExecutorService).saveOrUpdateJobExecutor(anyBoolean(), any(JobExecutor.class));
    }

    @Test
    public void testHandleCancelledJob() throws DatabaseException {
        InternalSchedulingStatus intStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
        when(jobExecutorService.findByName(anyString())).thenReturn(jobExecutor);
        workerAndJobStatusHandlerServiceImpl.handleCancelledJob(jobEntry, SchedulingConstants.WORKER_READY, Constants.XQ_FATAL_ERR, intStatus);
        verify(jobExecutorService).findByName(anyString());
    }

    @Test
    public void testResendMessageToWorker() throws DatabaseException {
        InternalSchedulingStatus intStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
        WorkerJobRabbitMQRequestMessage workerJobRabbitMQRequestMessage = new WorkerJobRabbitMQRequestMessage();
        doNothing().when(jobService).updateWorkerRetries(anyInt(), any(Timestamp.class), anyInt());
        doNothing().when(rabbitMQMessageSender).sendMessageToRabbitMQ(any(WorkerJobRabbitMQRequestMessage.class));
        workerAndJobStatusHandlerServiceImpl.resendMessageToWorker(Constants.MAX_SCRIPT_EXECUTION_RETRIES, Constants.XQ_FATAL_ERR, intStatus, jobEntry, workerJobRabbitMQRequestMessage, jobExecutor, jobExecutorHistory, false);
    }
}





























