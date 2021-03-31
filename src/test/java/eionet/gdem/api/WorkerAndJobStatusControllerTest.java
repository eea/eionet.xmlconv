package eionet.gdem.api;

import eionet.gdem.SchedulingConstants;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.JobExecutor;
import eionet.gdem.jpa.Entities.JobExecutorHistory;
import eionet.gdem.jpa.service.JobExecutorHistoryService;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.JobHistoryService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class WorkerAndJobStatusControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private JobExecutorService jobExecutorService;

    @Mock
    private JobHistoryService jobHistoryService;

    @Mock
    private JobExecutorHistoryService jobExecutorHistoryService;

    @Spy
    @InjectMocks
    private WorkerAndJobStatusController workerAndJobStatusController;

    private MockHttpSession session;
    private MockMvc mockMvc;

    @Before
    public void setUp()  {
        MockitoAnnotations.initMocks(this);
        setSession();
        mockMvc = MockMvcBuilders.standaloneSetup(workerAndJobStatusController).build();
    }

    void setSession() {
        session = new MockHttpSession();
        session.setAttribute("jobId", 335);
    }

    @Test
    public void changeWorkerStatusToFailed() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/worker/fail")
                .session(session);
        JobEntry jobEntry = new JobEntry().setId(335).setUrl("url").setSrcFile("srcFile").setFile("file").setResultFile("resultFile").setJobType("ON DEMAND").setJobExecutorName("demoExecutor");
        when(jobService.findById(anyInt())).thenReturn(jobEntry);
        JobExecutor jobExecutor = new JobExecutor("demoExecutor", SchedulingConstants.WORKER_FAILED, 335, "containerId", "demoExecutor-queue");
        when(jobExecutorService.findByName(anyString())).thenReturn(jobExecutor);
        doNothing().when(jobExecutorService).saveOrUpdateJobExecutor(any(JobExecutor.class));
        doNothing().when(jobExecutorHistoryService).saveJobExecutorHistoryEntry(any(JobExecutorHistory.class));
        doNothing().when(jobService).changeNStatus(anyInt(), anyInt());
        doNothing().when(jobService).changeIntStatusAndJobExecutorName(any(InternalSchedulingStatus.class), anyString(), any(Timestamp.class), anyInt());
        doNothing().when(jobHistoryService).updateStatusesAndJobExecutorName(any(XQScript.class), anyInt(), anyInt(), anyString(), anyString());
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }
}



































