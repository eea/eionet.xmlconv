package eionet.gdem.api.qa.web;

import eionet.gdem.security.service.AuthTokenService;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class FileDownloadControllerTest {

    private MockMvc mockMvc;

    @Mock
    AuthTokenService authTokenService;

    @Mock
    Path file;

    @Spy
    @InjectMocks
    FileDownloadController fileDownloadController;

    private static final String FILEPATH = "/tmp/file.zip";

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileDownloadController).build();
        when(authTokenService.getParsedAuthenticationTokenFromSchema(Mockito.anyString(), Mockito.anyString())).thenReturn("testToken");
        when(authTokenService.verifyUser(Mockito.anyString())).thenReturn(true);
        doReturn(FILEPATH).when(fileDownloadController).getFilePath(Mockito.anyString(), Mockito.anyString());
        doReturn(true).when(fileDownloadController).checkIfFileExists(Mockito.any(Path.class));
        doNothing().when(fileDownloadController).copyFIle(Mockito.any(HttpServletResponse.class), Mockito.any(Path.class));
        doReturn(file).when(fileDownloadController).getPath(Mockito.anyString());
 //       when(file.getFileName()).thenReturn(file);
    }

    @Test
    public void testGetFile() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("authorization", "jwtToken");
        mockMvc.perform(get("/download/zip/{fileName}", "fileName.zip").headers(httpHeaders))
                .andExpect(status().isOk());
    }



}























