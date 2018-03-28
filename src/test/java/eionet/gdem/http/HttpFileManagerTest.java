package eionet.gdem.http;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
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

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private HttpFileManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new HttpFileManager();
    }

    @Test
    public void testFileInputStream() throws IOException, URISyntaxException, XMLConvException {
        InputStream in = manager.getFileInputStream(TestUtils.getLocalURL("xmlfile/sample-dev.xml"), null, false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(in, out);
        assertTrue("Empty file:", out.size() > 0);
    }

    @Test
    public void testHttpResponse() throws IOException, URISyntaxException, XMLConvException {
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        manager.getHttpResponse(httpResponse, null, TestUtils.getLocalURL("xmlfile/sample-dev.xml"));
        assertEquals(MediaType.APPLICATION_XML_VALUE, httpResponse.getContentType());
        assertTrue(httpResponse.getContentLength() > 0);
        assertTrue(httpResponse.getContentAsString().length() > 0);
    }

    @Test
    public void buildSourceFileUrlWithTicket() throws IOException, URISyntaxException, XMLConvException {
        String url = "http://trustedurl.com";
        String ticket = "ticketValue";

        assertEquals("http://localhost:8080" + Constants.GETSOURCE_URL + "?ticket=" + ticket + "&source_url=" + url,
                HttpFileManager.getSourceUrlWithTicket(ticket, url, true));
        assertEquals(url, HttpFileManager.getSourceUrlWithTicket(null, url, false));
    }

    @Test
    public void testFileProxyUrl() throws IOException, URISyntaxException, XMLConvException {
        exception.expect(URISyntaxException.class);
        String fileUrl = "http://trustedurl.com";
        String url = Properties.gdemURL + Constants.GETSOURCE_URL + "&source_url=" + fileUrl;
        manager.getFileInputStream(url, null, true);
    }

    @After
    public void tearDown() throws Exception {
        manager.closeQuietly();
    }
}