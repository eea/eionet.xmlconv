package eionet.gdem.api.qa.web;

import eionet.gdem.security.errors.JWTException;
import eionet.gdem.security.service.AuthTokenService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class FileDownloadControllerTest {

    @Mock
    AuthTokenService authTokenService;

    @Mock
    Path file;

    @Spy
    @InjectMocks
    FileDownloadController fileDownloadController;

    private static final String ZIP_FILEPATH = "/tmp/fileName.zip";
    private static final String HTML_FILEPATH = "/tmp/fileName.html";

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(authTokenService.getParsedAuthenticationTokenFromSchema(Mockito.anyString(), Mockito.anyString())).thenReturn("Bearer jwtToken");
        when(authTokenService.verifyUser(Mockito.anyString())).thenReturn("admin");
        doReturn(true).when(fileDownloadController).checkIfFileExists(Mockito.any(Path.class));
        doNothing().when(fileDownloadController).copyFIle(Mockito.any(HttpServletResponse.class), Mockito.any(Path.class));
        doReturn(file).when(fileDownloadController).getPath(Mockito.anyString());
    }

    @Test
    public void testGetZipFileSuccess() throws Exception {
        doReturn(ZIP_FILEPATH).when(fileDownloadController).getFilePath(Mockito.anyString(), Mockito.anyString());
        fileDownloadController.getZipFile("fileName.zip", "Bearer jwtToken", new MockHttpServletResponse());
        verify(fileDownloadController, times(1)).copyFIle(Mockito.any(HttpServletResponse.class),Mockito.any(Path.class));
    }

    @Test(expected = JWTException.class)
    public void testGetZipFileJwtException() throws JWTException, IOException {
        doReturn(ZIP_FILEPATH).when(fileDownloadController).getFilePath(Mockito.anyString(), Mockito.anyString());
        when(authTokenService.getParsedAuthenticationTokenFromSchema(Mockito.anyString(), Mockito.anyString())).thenThrow(JWTException.class);
        fileDownloadController.getZipFile("fileName.zip", "jwtToken", new MockHttpServletResponse());
        verify(fileDownloadController, times(0)).copyFIle(Mockito.any(HttpServletResponse.class),Mockito.any(Path.class));
    }

    @Test
    public void testGetZipFileNotFound() throws IOException, JWTException {
        doReturn(ZIP_FILEPATH).when(fileDownloadController).getFilePath(Mockito.anyString(), Mockito.anyString());
        doReturn(false).when(fileDownloadController).checkIfFileExists(Mockito.any(Path.class));
        fileDownloadController.getZipFile("fileName.zip", "Bearer jwtToken", new MockHttpServletResponse());
        verify(fileDownloadController, times(0)).copyFIle(Mockito.any(HttpServletResponse.class),Mockito.any(Path.class));
    }

    @Test
    public void testGetHtmlFileSuccess() throws Exception {
        doReturn(HTML_FILEPATH).when(fileDownloadController).getFilePath(Mockito.anyString(), Mockito.anyString());
        fileDownloadController.getHtmlFile("fileName.html", "Bearer jwtToken", new MockHttpServletResponse());
        verify(fileDownloadController, times(1)).copyFIle(Mockito.any(HttpServletResponse.class),Mockito.any(Path.class));
    }

    @Test(expected = JWTException.class)
    public void testGetHtmlFileJwtException() throws JWTException, IOException {
        doReturn(HTML_FILEPATH).when(fileDownloadController).getFilePath(Mockito.anyString(), Mockito.anyString());
        when(authTokenService.getParsedAuthenticationTokenFromSchema(Mockito.anyString(), Mockito.anyString())).thenThrow(JWTException.class);
        fileDownloadController.getHtmlFile("fileName.html", "jwtToken", new MockHttpServletResponse());
        verify(fileDownloadController, times(0)).copyFIle(Mockito.any(HttpServletResponse.class),Mockito.any(Path.class));
    }

    @Test
    public void testGetHtmlFileNotFound() throws IOException, JWTException {
        doReturn(HTML_FILEPATH).when(fileDownloadController).getFilePath(Mockito.anyString(), Mockito.anyString());
        doReturn(false).when(fileDownloadController).checkIfFileExists(Mockito.any(Path.class));
        fileDownloadController.getHtmlFile("fileName.html", "Bearer jwtToken", new MockHttpServletResponse());
        verify(fileDownloadController, times(0)).copyFIle(Mockito.any(HttpServletResponse.class),Mockito.any(Path.class));
    }

}























