package eionet.gdem.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Token verifier class.
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Component
public class TokenVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenVerifier.class);

    @Value("${jwt.secret}")
    private String secret;
      
    @Value("${jwt.audience}")
    private String audience;
      
    @Value("${jwt.issuer}")
    private String issuer;

    /**
     *
     * @param authToken - Authentication token
     * @return
     * @throws UnsupportedEncodingException - Unsupported Encoding
     */
    public String verify(String authToken) throws UnsupportedEncodingException {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .acceptLeeway(1)
                .build();

        String username = null;

        try {
            DecodedJWT jwt = verifier.verify(authToken);
            username = jwt.getSubject();
        } catch (JWTVerificationException ex) {
            LOGGER.error("Authentication error: ", ex);
        }

        return username;
    }

}
