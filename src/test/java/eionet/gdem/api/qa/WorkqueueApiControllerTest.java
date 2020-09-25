package eionet.gdem.api.qa;

import eionet.gdem.api.WorkqueueApiController;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

public class WorkqueueApiControllerTest {

    @Mock
    WorkqueueApiController workqueueApiController;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    private String[] jobIds;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        MockitoAnnotations.initMocks(this);
        Mockito.doNothing().when(workqueueApiController).callWQManagerDeleteMethod(jobIds);
        doCallRealMethod().when(workqueueApiController).delete(request, response);
    }

    /* Test case: get method instead of post */
    @Test
    public void testDeleteNotPost() throws Exception {
        request.setRequestURI("/restapi/workqueue");
        request.setMethod("GET");
        workqueueApiController.delete(request, response);
        Assert.assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
        Assert.assertThat(response.getContentAsString(), is("\"{\"message\":\"Method was not POST\"}\""));
    }

    /* Test case: no job id was provided */
    @Test
    public void testDeleteNoJobId() throws Exception {
        request.setRequestURI("/restapi/workqueue");
        request.setMethod("POST");
        workqueueApiController.delete(request, response);
        Assert.assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
        Assert.assertThat(response.getContentAsString(), is("\"{\"message\":\"Missing job id from request\"}\""));
    }

    /* Test case: successful */
    @Test
    public void testDeleteSuccessful() throws Exception {
        request.setRequestURI("/restapi/workqueue");
        request.setMethod("POST");
        request.setParameter("job_id", "12345");
        workqueueApiController.delete(request, response);
        Assert.assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
        Assert.assertThat(response.getContentAsString(), is("\"{\"message\":\"Job deleted successfully\"}\""));
    }

}

