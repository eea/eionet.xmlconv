package eionet.gdem.dcm.business;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Enriko on 8.11.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class SourceFileMangerTest {

    @Test
    public void downloadFileWithoutAuth() throws IOException {

        SourceFileManager sourceFileManger = new SourceFileManager();
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        sourceFileManger.getFileNoAuthentication(httpResponse, "http://converters.eionet.europa.eu/dropdownmenus.txt");
        assertEquals("text/plain; charset=UTF-8", httpResponse.getContentType());
        assertTrue(httpResponse.getContentLength() > 0);
        assertTrue(httpResponse.getContentAsString().length() > 0);
    }

    @Test
    public void downloadFileWithAuth() throws IOException {

        SourceFileManager sourceFileManger = new SourceFileManager();
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        //when();
        sourceFileManger.getFileBasicAuthentication(httpResponse, null, "http://converters.eionet.europa.eu/dropdownmenus.txt");
        assertEquals("text/plain; charset=UTF-8", httpResponse.getContentType());
        assertTrue(httpResponse.getContentLength() > 0);
        assertTrue(httpResponse.getContentAsString().length() > 0);
    }

    @Test
    public void buildSourceFileUrlWithTicket() throws IOException {
        String url = "http://trustedurl.com";
        String ticket = "ticketValue";

        assertEquals(Properties.gdemURL + Constants.GETSOURCE_URL + "?ticket=" + ticket + "&source_url=" + url,
                SourceFileManager.getSourceFileAdapterURL(ticket, url, true));
        assertEquals(url, SourceFileManager.getSourceFileAdapterURL(null, url, false));
    }


}
