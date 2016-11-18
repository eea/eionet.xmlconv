package eionet.gdem.api.qa.web;

import com.google.gson.Gson;
import eionet.gdem.test.ApplicationTestContext;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class QaControllerTestIT {

    private MockMvc mockMvc;

    @Autowired
    QaController qaController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(qaController).build();
    }

    @Test
    public void testFailToperformInstantQARequestOnFileBecauseOfEmptySourceUrl() throws Exception {
        MockHttpServletRequestBuilder request = post("/qajobs");
        request.contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void testFailToperformInstantQARequestOnFileBecauseOfEmptyScriptId() throws Exception {
        MockHttpServletRequestBuilder request = post("/qajobs");
        request.contentType(MediaType.APPLICATION_JSON);
        HashMap<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("sourceUrl", "http://example.library");
        request.content(new Gson().toJson(requestBody));
        mockMvc.perform(request).andDo(print());
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void testSuccessFullInstantQARequest() throws Exception {
        MockHttpServletRequestBuilder request = post("/qajobs");
        request.contentType(MediaType.APPLICATION_JSON);
        HashMap<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("sourceUrl", "http://converterstest.eionet.europa.eu/xmlfile/aqd-labels.xml");
        requestBody.put("scriptId", "-1");
        request.content(new Gson().toJson(requestBody));
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void testFailToScheduleQaRequestOnEnvelopeBecauseOfEmptyEnvelopeUrl() throws Exception {
        MockHttpServletRequestBuilder request = post("/asynctasks/qajobs/batch");
        request.contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request).andDo(print());
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void testSuccessToScheduleQaRequestOnEnvelope() throws Exception {
        MockHttpServletRequestBuilder request = post("/asynctasks/qajobs/batch");
        request.contentType(MediaType.APPLICATION_JSON);
        HashMap<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("envelopeUrl", "http://cdrtest.eionet.europa.eu/gr/colvjazdw/envvkyrww/AutomaticQA_70556");
        request.content(new Gson().toJson(requestBody));
        mockMvc.perform(request).andDo(print());
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void testSuccessGetQaResultsForJob() throws Exception {
        MockHttpServletRequestBuilder request = get("/asynctasks/qajobs/{jobId}", 42);
        mockMvc.perform(request).andExpect(status().isOk());
        mockMvc.perform(request).andDo(print());
    }

    @Test
    public void testFailureListQaScriptsBecauseOfWrongActiveStatus() throws Exception {
        MockHttpServletRequestBuilder request = get("/qascripts");
        request.param("active", "untrue");
        mockMvc.perform(request).andExpect(status().isBadRequest());
        mockMvc.perform(request).andDo(print());
    }

    @Test
    public void testSuccessfullListOfQaScriptsForTrueActiveStatus() throws Exception {
        MockHttpServletRequestBuilder request = get("/qascripts");
        request.param("active", "true");
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void testSuccessfullListOfQaScriptsForFalseActiveStatus() throws Exception {
        MockHttpServletRequestBuilder request = get("/qascripts");
        request.param("active", "false");
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void testSuccessfullListOfQaScriptsForAllActiveStatus() throws Exception {
        MockHttpServletRequestBuilder request = get("/qascripts");
        request.param("active", "all");
        mockMvc.perform(request).andExpect(status().isOk());
    }
}
