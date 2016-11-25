package eionet.gdem.security.errors;

import com.google.gson.Gson;
import java.util.LinkedHashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@ControllerAdvice
public class JWTExceptionHandler extends ResponseEntityExceptionHandler  {
    
    
    @ExceptionHandler(value = { JWTException.class })
    protected ResponseEntity<Object> handleJWTException(Exception ex, WebRequest request) {
        
        Gson gson = new Gson();
        LinkedHashMap<String, String> results = new LinkedHashMap<String, String>();
        results.put("httpStatusCode", HttpStatus.BAD_REQUEST.toString());
        results.put("errorMessage", "Authentication Failed");
        String bodyOfResponse = gson.toJson(results);
        return handleExceptionInternal(ex, bodyOfResponse, 
          new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
