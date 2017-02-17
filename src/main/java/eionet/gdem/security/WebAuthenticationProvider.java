package eionet.gdem.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 *
 *
 */
@Component
public class WebAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        authentication.getName();
        authentication.getCredentials().toString();
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
