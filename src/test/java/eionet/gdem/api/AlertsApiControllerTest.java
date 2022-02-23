package eionet.gdem.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.jpa.Entities.AlertEntry;
import eionet.gdem.jpa.enums.AlertSeverity;
import eionet.gdem.jpa.service.AlertService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class AlertsApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlertService alertService;

    @Spy
    @InjectMocks
    private AlertsApiController alertsApiController;

    private AlertEntry alertEntry;
    private List<AlertEntry> alertEntryList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        alertEntry = new AlertEntry().setId(1).setSeverity(AlertSeverity.LOW).setDescription("alert").setNotificationSentToUns(false);
        alertEntry.setOccurrenceDateMod("2022-02-21 13:56:00");
        alertEntryList = new ArrayList<>();
        alertEntryList.add(alertEntry);
        mockMvc = MockMvcBuilders.standaloneSetup(alertsApiController).build();
    }

    @Test
    public void testGetAllProperties() throws Exception {
        when(alertService.findAll()).thenReturn(alertEntryList);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/alerts/get/all");
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }

    @Test
    public void testSave() throws Exception {
        doNothing().when(alertService).save(any(AlertEntry.class));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(alertEntry);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/alerts/add")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(alertService).delete(anyInt());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/alerts/delete/1");
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }
}















