package eionet.gdem.http;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 *
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class HttpFileManagerTest {

    private HttpFileManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new HttpFileManager();
    }

    @Test
    public void testFileInputStream() throws IOException, URISyntaxException {
        InputStream in = manager.getFileInputStream(Properties.gdemURL.concat("/dropdownmenus.txt"), null, false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(in, out);
        assertTrue("Empty file:", out.size() > 0);
    }

    @Test
    public void testHttpResponse() throws IOException, URISyntaxException {
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        manager.getHttpResponse(httpResponse, null, TestConstants.NETWORK_FILE_TO_TEST);
        assertEquals("text/plain", httpResponse.getContentType());
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

    @After
    public void tearDown() throws Exception {
        manager.closeQuietly();
    }
}