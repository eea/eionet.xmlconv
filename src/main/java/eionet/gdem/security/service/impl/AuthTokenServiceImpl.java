package eionet.gdem.security.service.impl;

import eionet.gdem.security.TokenVerifier;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.security.service.AuthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AuthTokenServiceImpl implements AuthTokenService {

    @Autowired
    private TokenVerifier verifier;

    @Autowired
    @Qualifier("apiuserdetailsservice")
    private UserDetailsService userDetailsService;

    private UserDetails userDetails;

    @Override
    public String getParsedAuthenticationToken(String rawAuthenticationToken, String authenticationTokenSchema) throws JWTException {
        if (rawAuthenticationToken == null || !rawAuthenticationToken.startsWith(authenticationTokenSchema)) {
            throw new JWTException("Missing or invalid Authorization header.");
        }
        return removeAuthenticationSchemaFromHeader(rawAuthenticationToken, authenticationTokenSchema);
    }

    @Override
    public boolean check(String parsedAuthenticationToken) {
       return parsedAuthenticationToken != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    @Override
    public boolean verifyUser(String parsedAuthenticationToken) throws IOException {
        String username = verifier.verify(parsedAuthenticationToken);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        setUserDetails(userDetails);
        return userDetails.isEnabled() && userDetails.getUsername().equals(username);
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    private String removeAuthenticationSchemaFromHeader(String tokenHeader, String authenticationTokenSchema) throws JWTException {
        String stripedTokenHeader = tokenHeader.replace(authenticationTokenSchema, "");
        return stripedTokenHeader.trim();
    }
}
