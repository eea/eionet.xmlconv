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

    private static final String FILEPATH = "/tmp/fileName.zip";

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(authTokenService.getParsedAuthenticationTokenFromSchema(Mockito.anyString(), Mockito.anyString())).thenReturn("Bearer jwtToken");
        when(authTokenService.verifyUser(Mockito.anyString())).thenReturn(true);
        doReturn(FILEPATH).when(fileDownloadController).getFilePath(Mockito.anyString(), Mockito.anyString());
        doReturn(true).when(fileDownloadController).checkIfFileExists(Mockito.any(Path.class));
        doNothing().when(fileDownloadController).copyFIle(Mockito.any(HttpServletResponse.class), Mockito.any(Path.class));
        doReturn(file).when(fileDownloadController).getPath(Mockito.anyString());
    }

    @Test
    public void testGetFileSuccess() throws Exception {
        fileDownloadController.getFile("fileName.zip", "Bearer jwtToken", new MockHttpServletResponse());
        verify(fileDownloadController, times(1)).copyFIle(Mockito.any(HttpServletResponse.class),Mockito.any(Path.class));
    }

    @Test(expected = JWTException.class)
    public void testGetFileJwtException() throws JWTException, IOException {
        when(authTokenService.getParsedAuthenticationTokenFromSchema(Mockito.anyString(), Mockito.anyString())).thenThrow(JWTException.class);
        fileDownloadController.getFile("fileName.zip", "jwtToken", new MockHttpServletResponse());
        verify(fileDownloadController, times(0)).copyFIle(Mockito.any(HttpServletResponse.class),Mockito.any(Path.class));
    }

    @Test
    public void testGetFileNotFound() throws IOException, JWTException {
        doReturn(false).when(fileDownloadController).checkIfFileExists(Mockito.any(Path.class));
        fileDownloadController.getFile("fileName.zip", "Bearer jwtToken", new MockHttpServletResponse());
        verify(fileDownloadController, times(0)).copyFIle(Mockito.any(HttpServletResponse.class),Mockito.any(Path.class));
    }

}























