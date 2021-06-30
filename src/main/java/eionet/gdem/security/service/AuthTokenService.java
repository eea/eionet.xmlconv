package eionet.gdem.security.service;

import eionet.gdem.security.errors.JWTException;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthTokenService {

    String getParsedAuthenticationTokenFromSchema(String rawAuthenticationToken, String authenticationTokenSchema) throws JWTException;

    String verifyUser(String parsedAuthenticationToken) throws JWTException;

    UserDetails getUserDetails(String username);
}
