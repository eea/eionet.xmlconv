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

public class WorkqueueApiControllerTestIT {

    WorkqueueApiController workqueueApiController;

    @Mock
    WorkqueueManager workqueueManager;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    final static String expectedToken = "testToken";

    private String[] jobIds;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        Mockito.doNothing().when(workqueueManager).deleteJobs(jobIds);
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

}

