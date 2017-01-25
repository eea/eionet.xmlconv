package eionet.gdem.security;

import com.google.gson.Gson;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        PrintWriter out = httpServletResponse.getWriter();
        Gson gson = new Gson();
        LinkedHashMap<String, String> results = new LinkedHashMap<String, String>();
        results.put("httpStatusCode", HttpStatus.UNAUTHORIZED.toString());
        results.put("errorMessage", "Access Denied from Entry Point Unauthorized Handler");
        out.write(gson.toJson(results));
        
    }
}
