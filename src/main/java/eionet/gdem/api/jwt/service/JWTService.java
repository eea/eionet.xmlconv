package eionet.gdem.api.jwt.service;

import eionet.gdem.security.errors.JWTException;

public interface JWTService {
    /**
     * Creates a valid JWT token for rest api methods
     *
     * @return the token
     */
    String generateJWTToken() throws JWTException;

    String getSubjectForJWTToken() throws JWTException;

    String getJwtAudience();

    String getJwtIssuer();

    String getJwtApiKey();
}
