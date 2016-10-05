package eionet.gdem.dcm.business;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Enriko on 8.11.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class SourceFileMangerTest {

    @Test
    @Ignore
    //XXX: This should fail, unless there is a reason to have UTF-8 plain text files.
    public void downloadFileWithoutAuth() throws IOException, URISyntaxException {

        HttpFileManager httpFileManager = new HttpFileManager();
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        httpFileManager.getHttpResponse(httpResponse, null, TestConstants.NETWORK_FILE_TO_TEST);
        assertEquals("text/plain; charset=UTF-8", httpResponse.getContentType());
        assertTrue(httpResponse.getContentLength() > 0);
        assertTrue(httpResponse.getContentAsString().length() > 0);
    }

    @Test
    @Ignore
    //XXX: This should fail, unless there is a reason to have UTF-8 plain text files.
    public void downloadFileWithAuth() throws IOException, URISyntaxException {
        HttpFileManager httpFileManager = new HttpFileManager();
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        //when();
        httpFileManager.getHttpResponse(httpResponse, null, TestConstants.NETWORK_FILE_TO_TEST);
        assertEquals("text/plain; charset=UTF-8", httpResponse.getContentType());
        assertTrue(httpResponse.getContentLength() > 0);
        assertTrue(httpResponse.getContentAsString().length() > 0);
    }

    @Test
    public void buildSourceFileUrlWithTicket() throws IOException, URISyntaxException {
        String url = "http://trustedurl.com";
        String ticket = "ticketValue";

        assertEquals(Properties.gdemURL + Constants.GETSOURCE_URL + "?ticket=" + ticket + "&source_url=" + url,
                HttpFileManager.getSourceUrlWithTicket(ticket, url, true));
        assertEquals(url, HttpFileManager.getSourceUrlWithTicket(null, url, false));
    }


}
