package eionet.gdem.security;

import com.google.gson.Gson;
import eionet.gdem.security.errors.JWTException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

    @Value("${jwt.header}")
    private String tokenHeader;
    @Value("${jwt.header.schema}")
    private String authenticationTokenSchema;

    private static final List<String> INTERCEPTED_URLS = Collections.unmodifiableList(Arrays.asList("/asynctasks/", "/qajobs"));

    @Autowired
    private TokenVerifier verifier;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        try {

            String requestURL = httpRequest.getRequestURL().toString();
            if (!requiresAuthentication(requestURL)) {
                chain.doFilter(request, response);
            }
            String rawAuthenticationToken = httpRequest.getHeader(this.tokenHeader);
            if (rawAuthenticationToken == null || !rawAuthenticationToken.startsWith(authenticationTokenSchema)) {
                throw new JWTException("Missing or invalid Authorization header.");
            }
            String parsedAuthenticationToken = removeAuthenticationSchemaFromHeader(rawAuthenticationToken);

            if (parsedAuthenticationToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = verifier.verify(parsedAuthenticationToken);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (userDetails.isEnabled() && userDetails.getUsername().equals(username)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
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
            results.put("errorMessage", "Access Denied");
            out.write(gson.toJson(results));
        }
    }

    private String removeAuthenticationSchemaFromHeader(String tokenHeader) throws JWTException {

        String stripedTokenHeader;
        stripedTokenHeader = tokenHeader.replace(authenticationTokenSchema, "");
        return stripedTokenHeader.trim();
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
