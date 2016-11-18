package eionet.gdem.security;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Component
public class TokenVerifier {

    final String secret = "top-secret";
    final String audience = "eea";
    final String issuer = "eea";

    public String verify(String authToken) throws IOException {

        final JWTVerifier verifier = new JWTVerifier(secret, audience, issuer);
        String username = null;
        try {
            final Map<String, Object> claims = verifier.verify(authToken);
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                String key = entry.getKey();
                if (key == "sub") {
                    username = entry.getValue().toString();
                }
            }

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AuthenticationTokenFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AuthenticationTokenFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(AuthenticationTokenFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(AuthenticationTokenFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JWTVerifyException ex) {
            Logger.getLogger(AuthenticationTokenFilter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return username;
    }

}
