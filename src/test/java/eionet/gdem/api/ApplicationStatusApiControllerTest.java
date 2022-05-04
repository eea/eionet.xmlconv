package eionet.gdem.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.SchedulingConstants;
import eionet.gdem.api.model.ApplicationStatus;
import eionet.gdem.api.model.JobExecutorReportStatus;
import eionet.gdem.jpa.repositories.JobExecutorRepository;
import eionet.gdem.rancher.model.ContainerData;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestrator;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestrator;
import eionet.gdem.test.ApplicationTestContext;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class ApplicationStatusApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JobExecutorRepository jobExecutorRepository;
    @Mock
    private ServicesRancherApiOrchestrator servicesRancherApiOrchestrator;
    @Mock
    private ContainersRancherApiOrchestrator containersRancherApiOrchestrator;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Spy
    @InjectMocks
    private ApplicationStatusApiController applicationStatusApiController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(applicationStatusApiController).build();
    }

    @Test
    public void testGetStatus() throws Exception {
        doNothing().when(jobExecutorRepository).checkConnection();
        doNothing().when(rabbitTemplate).convertAndSend(anyString(),anyString(),anyString());
        when(rabbitTemplate.receiveAndConvert(anyString())).thenReturn("healthCheck");
        List<String> instances = new ArrayList<>();
        instances.add("li154784");
        when(servicesRancherApiOrchestrator.getContainerInstances(anyString())).thenReturn(instances);
        ContainerData containerData = new ContainerData();
        containerData.setState(SchedulingConstants.CONTAINER_STATE_ENUM.RUNNING.getValue());
        when(containersRancherApiOrchestrator.getContainerInfoById(anyString())).thenReturn(containerData);
        JobExecutorReportStatus jobExecutorReportStatus = new JobExecutorReportStatus();
        jobExecutorReportStatus.setLightJobExecutorInstancesRunning(1).setHeavyJobExecutorInstancesRunning(1).setFmeSyncJobExecutorInstancesRunning(1).setFmeAsyncJobExecutorInstancesRunning(1);
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/application/status");
        MvcResult result = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.databaseConnection", is("up")))
                .andExpect(jsonPath("$.rabbitmqConnection", is("up")))
                .andExpect(jsonPath("$.rancherConnection", is("up")))
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        ApplicationStatus content = mapper.readValue(contentAsString, ApplicationStatus.class);
        MatcherAssert.assertThat(content.getJobExecutorReportStatus().getLightJobExecutorInstancesRunning(), is(1));
        MatcherAssert.assertThat(content.getJobExecutorReportStatus().getHeavyJobExecutorInstancesRunning(), is(1));
        MatcherAssert.assertThat(content.getJobExecutorReportStatus().getFmeAsyncJobExecutorInstancesRunning(), is(1));
        MatcherAssert.assertThat(content.getJobExecutorReportStatus().getFmeSyncJobExecutorInstancesRunning(), is(1));
    }
}




























