package eionet.gdem.security;

import com.google.gson.Gson;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.security.service.AuthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

    @Value("${jwt.header}")
    private String tokenHeader;
    @Value("${jwt.header.schema}")
    private String authenticationTokenSchema;

    private static final List<String> INTERCEPTED_URLS = Collections.unmodifiableList(Arrays.asList("/asynctasks/", "/qajobs", "dataflows"));

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private TokenVerifier verifier;

    @Autowired
    @Qualifier("apiuserdetailsservice")
    private UserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        try {

            String requestURL = httpRequest.getRequestURL().toString();
            if (!requiresAuthentication(requestURL)) {
                chain.doFilter(request, response);
                return;
            }
            String rawAuthenticationToken = httpRequest.getHeader(this.tokenHeader);
            String parsedAuthenticationToken = authTokenService.getParsedAuthenticationTokenFromSchema(rawAuthenticationToken, this.authenticationTokenSchema);

            if (parsedAuthenticationToken != null) {
                String username = this.authTokenService.verifyUser(parsedAuthenticationToken);
                if(username!=null){
                    UserDetails userDetails = this.authTokenService.getUserDetails(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }else{
                    throw new JWTException("username not found in Database");
                }
            }
            chain.doFilter(request, response);
        } catch (JWTException ex) {
            Logger.getLogger(AuthenticationTokenFilter.class.getName()).log(Level.SEVERE, null, ex);
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            PrintWriter out = httpServletResponse.getWriter();
            Gson gson = new Gson();
            LinkedHashMap<String, String> results = new LinkedHashMap<String, String>();
            results.put("httpStatusCode", HttpStatus.UNAUTHORIZED.toString());
            results.put("errorMessage", ex.getMessage());
            out.write(gson.toJson(results));
        }
    }

    private boolean requiresAuthentication(String url) {
        for (String interceptedUrl : INTERCEPTED_URLS) {
            if (url.contains(interceptedUrl)) {
                return true;
            }
        }
        return false;
    }
}
