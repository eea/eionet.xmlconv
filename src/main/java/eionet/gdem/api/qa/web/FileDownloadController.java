package eionet.gdem.api.qa.web;

import eionet.gdem.Properties;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.security.service.AuthTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/download")
public class FileDownloadController {

    AuthTokenService authTokenService;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadController.class);

    @Autowired
    public FileDownloadController(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }


    @RequestMapping(value = "/zip/{fileName}", method = RequestMethod.GET)
    public void getFile(@PathVariable String fileName, @RequestHeader(value = "authorization") String authorization, HttpServletResponse response) throws JWTException, IOException {
        String filePath = null;
        String urlPath = new StringBuilder("/tmp/").append(fileName).append(".zip").toString();

        String rawAuthenticationToken = authorization;
        String parsedAuthenticationToken = authTokenService.getParsedAuthenticationTokenFromSchema(rawAuthenticationToken, Properties.jwtHeaderSchemaProperty);
        if (authTokenService.verifyUser(parsedAuthenticationToken)) {
           filePath = getFilePath(urlPath, fileName);
        }

        Path file = getPath(filePath);
        if (checkIfFileExists(file)) {
            response.setContentType("application/zip");
            response.addHeader("Content-Disposition", "attachment; filename=" + file.getFileName());
            copyFIle(response, file);
            response.getOutputStream().flush();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected String getFilePath(String urlPath, String fileName) {
        String filePath;
        filePath = Properties.appRootFolder + urlPath;
        if (fileName == null || fileName.isEmpty() || "/".equals(fileName)) {
            throw new IllegalArgumentException();
        }
        return filePath;
    }

    protected Path getPath(String filePath) {
        return Paths.get(filePath);
    }

    protected boolean checkIfFileExists(Path file) {
        return Files.exists(file);
    }

    protected void copyFIle(HttpServletResponse response, Path file) throws IOException {
        Files.copy(file, response.getOutputStream());
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public void handleServletRequestBindingException(Exception exception, HttpServletResponse response) throws IOException {
        LOGGER.info(exception.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing authorization token");
    }

    @ExceptionHandler(JWTException.class)
    public void handleJWTException(Exception exception, HttpServletResponse response) throws IOException {
        LOGGER.info("Error during token verification.",exception);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(Exception exception, HttpServletResponse response) throws IOException {
        LOGGER.info("Got an IllegalArgumentException from user code; interpreting it as 400 Bad Request.", exception);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public void handleIOException(Exception exception, HttpServletResponse response) throws IOException {
        LOGGER.info("Got an IOException.",exception);
        response.setContentType("");
        response.addHeader("Content-Disposition", "");
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.toString());
    }
}

























