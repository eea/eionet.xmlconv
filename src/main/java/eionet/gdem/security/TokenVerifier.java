package eionet.gdem.security;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import eionet.gdem.security.errors.JWTException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Component
public class TokenVerifier {

      @Value("${jwt.secret}")
      private String secret;
      
      @Value("${jwt.audience}")
      private String audience;
      
      @Value("${jwt.issuer}")
      private String issuer;
      
      private static final String JWT_SUBJECT_CLAIM="sub";

    public String verify(String authToken) throws IOException , JWTException {

        final JWTVerifier verifier = new JWTVerifier(secret, audience, issuer);
        String username = null;
        try {
            final Map<String, Object> claims = verifier.verify(authToken);
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                String key = entry.getKey();
                if (key == JWT_SUBJECT_CLAIM) {
                    username = entry.getValue().toString();
                }
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException | SignatureException |  JWTVerifyException  ex) {
            Logger.getLogger(AuthenticationTokenFilter.class.getName()).log(Level.SEVERE, null, ex);
            throw new JWTException(ex);
        } 

        return username;
    }

}
