package eionet.gdem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 */
public class WebPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Autowired
    @Qualifier("webuserdetailsservice")
    private UserDetailsService userDetailsService;

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        String user = (String) httpServletRequest.getSession().getAttribute("user");
        if (user == null) {
            return null;
        }
        return userDetailsService.loadUserByUsername(user);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return null;
    }
}
