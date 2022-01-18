package eionet.gdem.api;

import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.jpa.service.QueryHistoryService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class ScriptsHistoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QueryHistoryService queryHistoryService;

    @InjectMocks
    private ScriptsHistoryController scriptsHistoryController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(scriptsHistoryController).build();
    }

    @Test
    public void testGetAllScriptsHistory() throws Exception {
        List<QueryHistoryEntry> entries = new ArrayList<>();
        when(queryHistoryService.findEntriesByQueryId(anyInt())).thenReturn(entries);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/scriptData/history/1");
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }
}
















