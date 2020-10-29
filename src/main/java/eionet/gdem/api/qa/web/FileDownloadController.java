package eionet.gdem.api.qa.web;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.security.service.AuthTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

@RestController
public class FileDownloadController {

    AuthTokenService authTokenService;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadController.class);

    @Autowired
    public FileDownloadController(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @RequestMapping(value = "/downloadFile/{fileName}", method = RequestMethod.GET)
    public String getFile(@PathVariable String fileName, @RequestHeader("authorization") String authorization) throws JWTException, IOException {
        String filePath = null;
        String urlPath = "/" + Constants.TMP_FOLDER + fileName + Constants.HTML_FILE;

        String rawAuthenticationToken = authorization;
        String parsedAuthenticationToken = authTokenService.getParsedAuthenticationTokenFromSchema(rawAuthenticationToken, Properties.jwtHeaderSchema);
        if (authTokenService.verifyUser(parsedAuthenticationToken)) {
            filePath = getFilePath(urlPath, fileName);
        }

        File file = new File(filePath);
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes, "UTF-8");
    }

    protected String getFilePath(String urlPath, String fileName) {
        String filePath;
        filePath = Properties.appRootFolder + urlPath;
        if (fileName == null || fileName.isEmpty() || "/".equals(fileName)) {
            throw new IllegalArgumentException();
        }
        return filePath;
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<String> handleServletRequestBindingException(Exception exception) {
        LOGGER.info(exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing authorization token.");
    }

    @ExceptionHandler(JWTException.class)
    public ResponseEntity<String> handleJWTException(Exception exception) {
        LOGGER.info("Error during token verification.",exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(Exception exception) {
        LOGGER.info("Got an IllegalArgumentException from user code; interpreting it as 400 Bad Request.", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(Exception exception) {
        LOGGER.info("Error during file reading", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during file reading");
    }
}

























