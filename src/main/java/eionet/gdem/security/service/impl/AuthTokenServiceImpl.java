package eionet.gdem.security.service.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import eionet.gdem.security.TokenVerifier;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.security.service.AuthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
public class AuthTokenServiceImpl implements AuthTokenService {

    private TokenVerifier verifier;

    private UserDetailsService userDetailsService;

    private UserDetails userDetails;

    @Autowired
    public AuthTokenServiceImpl(TokenVerifier verifier, @Qualifier("apiuserdetailsservice") UserDetailsService userDetailsService) {
        this.verifier = verifier;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String getParsedAuthenticationTokenFromSchema(String rawAuthenticationToken, String authenticationTokenSchema) throws JWTException {
        if (rawAuthenticationToken == null || !rawAuthenticationToken.startsWith(authenticationTokenSchema)) {
            throw new JWTException("Missing or invalid Authorization header.");
        }
        String parsedAuthenticationToken = rawAuthenticationToken.replace(authenticationTokenSchema, "").trim();

        if (parsedAuthenticationToken == null) {
            throw new JWTException("Error during parsing authentication token");
        }
        return parsedAuthenticationToken;
    }

    @Override
    public String verifyUser(String parsedAuthenticationToken) throws JWTException {
        String username = null;
        try {
            username = verifier.verify(parsedAuthenticationToken);
        } catch (JWTVerificationException e) {
            throw new JWTException("Error during token verification");
        }
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if( userDetails.isEnabled() && userDetails.getUsername().equals(username)){
            return username;
        }else{
            return null;
        }
    }

    public UserDetails getUserDetails(String username) {
        return this.userDetailsService.loadUserByUsername(username);
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

}
