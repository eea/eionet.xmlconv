package eionet.gdem.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.jpa.Entities.PropertiesEntry;
import eionet.gdem.jpa.service.PropertiesService;
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
public class PropertiesApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PropertiesService propertiesService;

    @Spy
    @InjectMocks
    private PropertiesApiController propertiesApiController;

    private PropertiesEntry propertiesEntry;
    private List<PropertiesEntry> propertiesEntryList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        propertiesEntry = new PropertiesEntry();
        propertiesEntryList = new ArrayList<>();
        mockMvc = MockMvcBuilders.standaloneSetup(propertiesApiController).build();
    }

    @Test
    public void testGetAllProperties() throws Exception {
        when(propertiesService.findAll()).thenReturn(propertiesEntryList);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/properties/get/all");
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }

    @Test
    public void testSave() throws Exception {
        doNothing().when(propertiesService).save(any(PropertiesEntry.class));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(propertiesEntry);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/properties/add")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(propertiesService).delete(anyInt());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/properties/delete/1");
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }
}















