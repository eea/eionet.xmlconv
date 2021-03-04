package eionet.gdem.api;

import eionet.gdem.jpa.Entities.JobEntry;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class WorkerStatusControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private JobExecutorService jobExecutorService;

    @Mock
    private JobHistoryService jobHistoryService;

    @Spy
    @InjectMocks
    private WorkerStatusController workerStatusController;

    private MockHttpSession session;
    private MockMvc mockMvc;

    @Before
    public void setUp()  {
        MockitoAnnotations.initMocks(this);
        setSession();
        mockMvc = MockMvcBuilders.standaloneSetup(workerStatusController).build();
    }

    void setSession() {
        session = new MockHttpSession();
        session.setAttribute("jobId", 335);
    }

    @Test
    public void changeWorkerStatusToFailed() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/worker/fail")
                .session(session);
        JobEntry jobEntry = new JobEntry().setSrcFile("srcFile").setFile("file").setResultFile("resultFile").setJobType("ON DEMAND");
        when(jobService.findById(anyInt())).thenReturn(jobEntry);
        doNothing().when(jobExecutorService).updateJobExecutor(anyInt(), anyInt(), anyString());
        doNothing().when(jobService).changeNStatus(anyInt(), anyInt());
        doNothing().when(jobHistoryService).updateStatusesAndJobExecutorName(any(XQScript.class), anyInt(), anyInt(), anyString(), anyString());
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }
}


































