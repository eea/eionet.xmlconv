package eionet.gdem.api.qa.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.errors.EmptyParameterException;
import eionet.gdem.api.qa.model.EnvelopeWrapper;
import eionet.gdem.api.qa.service.QaService;
import static eionet.gdem.qa.ListQueriesMethod.DEFAULT_CONTENT_TYPE_ID;
import static eionet.gdem.qa.ListQueriesMethod.VALIDATION_UPPER_LIMIT;
import eionet.gdem.qa.QaScriptView;
import eionet.gdem.test.ApplicationTestContext;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.commons.lang.builder.EqualsBuilder;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class QaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QaService qaServiceMock;

    QaController qaController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.qaController = new QaController(qaServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(qaController).build();
    }

    @Test(expected = EmptyParameterException.class)
    public void testFailToperformInstantQARequestOnFileBecauseOfEmptySourceUrl() throws EmptyParameterException, XMLConvException, UnsupportedEncodingException {
        EnvelopeWrapper envelopeWrapper = new EnvelopeWrapper();
        qaController.performInstantQARequestOnFile(envelopeWrapper);
    }

    @Test(expected = EmptyParameterException.class)
    public void testFailToperformInstantQARequestOnFileBecauseOfEmptyScriptId() throws EmptyParameterException, XMLConvException, UnsupportedEncodingException {
        EnvelopeWrapper envelopeWrapper = new EnvelopeWrapper();
        envelopeWrapper.setSourceUrl("\"http://converterstest.eionet.europa.eu/xmlfile/aqd-labels.xml");
        qaController.performInstantQARequestOnFile(envelopeWrapper);
    }

    @Test(expected = XMLConvException.class)
    public void testFailToSendQaRequestBecauseOfXMLConvException() throws Exception {
        String sourceUrl = "http://converterstest.eionet.europa.eu/xmlfile/aqd-labels.xml";
        String scriptId = "-1";
        EnvelopeWrapper envelopeWrapper = new EnvelopeWrapper();
        envelopeWrapper.setSourceUrl(sourceUrl);
        envelopeWrapper.setScriptId(scriptId);
        when(qaServiceMock.runQaScript(anyString(), anyString())).thenThrow(new XMLConvException("xmlconv exception"));
        qaController.performInstantQARequestOnFile(envelopeWrapper);
        ArgumentCaptor<String> sourceUrlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> scriptIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(qaServiceMock, times(1)).runQaScript(sourceUrlCaptor.capture(), scriptIdCaptor.capture());
        assertTrue(EqualsBuilder.reflectionEquals(sourceUrlCaptor.getValue(), sourceUrl));
        assertTrue(EqualsBuilder.reflectionEquals(scriptIdCaptor.getValue(), scriptId));
    }

    @Test(expected = EmptyParameterException.class)
    public void testFailToscheduleQaRequestOnEnvelopeEmptyParameterException() throws EmptyParameterException, XMLConvException, JsonProcessingException {
        EnvelopeWrapper envelopeWrapper = new EnvelopeWrapper();
        qaController.scheduleQaRequestOnEnvelope(envelopeWrapper);
    }

    @Test
    public void SuccessScheduleQaRequestOnEnvelope() throws XMLConvException, EmptyParameterException, JsonProcessingException {
        String envelopeUrl = "http://cdrtest.eionet.europa.eu/gr/colvjazdw/envvkyrww/AutomaticQA_70556";
        EnvelopeWrapper envelopeWrapper = new EnvelopeWrapper();
        envelopeWrapper.setEnvelopeUrl(envelopeUrl);
        qaController.scheduleQaRequestOnEnvelope(envelopeWrapper);
        ArgumentCaptor<String> envelopeUrlCaptor = ArgumentCaptor.forClass(String.class);
        verify(qaServiceMock, times(1)).scheduleJobs(envelopeUrlCaptor.capture());
        assertTrue(EqualsBuilder.reflectionEquals(envelopeUrlCaptor.getValue(), envelopeUrl));
    }

    @Test
    public void FailureToGetQaResultsForJobBecauseOfXMLConvException() throws XMLConvException, Exception {
        when(qaServiceMock.getJobResults(any(String.class))).thenThrow(XMLConvException.class);
        mockMvc.perform(get("/asynctasks/qajobs/{jobId}", 77))
                .andExpect(status().isInternalServerError());
        verify(qaServiceMock, times(1)).getJobResults("77");
    }

    @Test
    public void SuccessToGetQaResultsForJob() throws XMLConvException, Exception {

        String jobid = "42";
        Hashtable<String, String> results = new Hashtable<String, String>();
        results.put(Constants.RESULT_VALUE_PRM, "200");
        results.put(Constants.RESULT_FEEDBACKSTATUS_PRM, "BLOCKER");
        results.put(Constants.RESULT_FEEDBACKMESSAGE_PRM, "Feedback Message");
        results.put(Constants.RESULT_METATYPE_PRM, "text/html");
        results.put(Constants.RESULT_SCRIPTTITLE_PRM, "script title");
        when(qaServiceMock.getJobResults(any(String.class))).thenReturn(results);

        mockMvc.perform(get("/asynctasks/qajobs/{jobId}", jobid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionStatus", is("200")))
                .andExpect(jsonPath("$.feedbackStatus", is("BLOCKER")))
                .andExpect(jsonPath("$.feedbackMessage", is("Feedback Message")))
                .andExpect(jsonPath("$.feedbackContentType", is("text/html")))
                .andExpect(jsonPath("$.feedbackContent", is("script title")));

        ArgumentCaptor<String> jobIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(qaServiceMock, times(1)).getJobResults(jobIdCaptor.capture());
        assertTrue(EqualsBuilder.reflectionEquals(jobIdCaptor.getValue(), jobid));
    }

    @Test
    public void FailureToListQaScriptsBecauseOfXMLConvException() throws XMLConvException, Exception {
        when(qaServiceMock.listQAScripts(any(), anyString())).thenThrow(XMLConvException.class);
        mockMvc.perform(get("/qascripts"))
                .andExpect(status().isInternalServerError());
        verify(qaServiceMock, times(1)).listQAScripts(any(), anyString());
    }

    @Test
    public void SuccessListQaScriptsWithNoParams() throws XMLConvException, Exception {
        mockMvc.perform(get("/qascripts"))
                .andExpect(status().isOk());
        // When no active status is present, default value(true) must be used automatically
        verify(qaServiceMock, times(1)).listQAScripts(null, "true");

    }

    @Test
    public void SuccessListQaScriptsWithSchemaAndActiveStatus() throws XMLConvException, Exception {

        mockMvc.perform(get("/qascripts?schema={schema}&active={active}", "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd", "true"))
                .andExpect(status().isOk());

        verify(qaServiceMock, times(1)).listQAScripts("http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd", "true");
    }
}
