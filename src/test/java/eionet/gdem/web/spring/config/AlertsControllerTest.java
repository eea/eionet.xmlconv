package eionet.gdem.web.spring.config;

import eionet.gdem.services.MessageService;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.web.spring.admin.AlertsController;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class AlertsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;

    @Spy
    @InjectMocks
    private AlertsController alertsController;

    private MockHttpSession session;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        session = new MockHttpSession();
        session.setAttribute("user", "roug");
        when(messageService.getMessage(anyString())).thenReturn("test");
        mockMvc = MockMvcBuilders.standaloneSetup(alertsController).build();
    }

    @Test
    public void testViewAlertsSuccess() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/alerts")
                .session(session);
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(view().name("/old/admin/html/alerts"));
    }

    @Test
    public void testViewAlertsNotAuthorized() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/alerts");
        mockMvc.perform(builder)
                .andExpect(view().name("/error/error"));
    }
}





















