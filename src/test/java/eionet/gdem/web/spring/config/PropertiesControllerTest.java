package eionet.gdem.web.spring.config;

import eionet.gdem.services.MessageService;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.web.spring.admin.PropertiesController;
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
public class PropertiesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;

    private MockHttpSession session;

    @Spy
    @InjectMocks
    private PropertiesController propertiesController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        session = new MockHttpSession();
        session.setAttribute("user", "roug");
        when(messageService.getMessage(anyString())).thenReturn("test");
        mockMvc = MockMvcBuilders.standaloneSetup(propertiesController).build();
    }

    @Test
    public void testViewPropertiesSuccess() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/viewAndEditProperties")
                .session(session);
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(view().name("/old/admin/html/viewAndEditProperties"));
    }

    @Test
    public void testViewPropertiesNotAuthorized() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/viewAndEditProperties");
        mockMvc.perform(builder)
                .andExpect(view().name("/error/error"));
    }

}













