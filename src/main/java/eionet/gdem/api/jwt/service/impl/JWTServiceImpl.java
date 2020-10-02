package eionet.gdem.api.jwt.service.impl;

import eionet.gdem.Properties;
import eionet.gdem.api.jwt.service.JWTService;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTServiceImpl implements JWTService {

    /**
     * JWT Audience.
     */
    private String JWT_AUDIENCE = Properties.getStringProperty(Properties.jwtAudienceProperty);

    /**
     * JWT issuer.
     */
    private String JWT_ISSUER = Properties.getStringProperty(Properties.jwtIssuerProperty);

    /**
     * JWT api key.
     */
    private String JWT_API_KEY= Properties.getStringProperty(Properties.jwtSecretKey);

    /** Dao for getting job data. */
    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();

    public JWTServiceImpl(){};

    /**
     * Creates a valid JWT token for vocabulary rdf upload via tha API
     *
     * @return the token
     */
    @Override
    public String generateJWTToken() throws JWTException {

        //The JWT will be signed with secret
        byte[] apiKeySecretBytes = this.getJwtApiKey().getBytes();

        Date now = new Date();

        Map<String, Object> claims= new HashMap<>();
        claims.put("iat", now.getTime());
        claims.put("iss", this.getJwtIssuer());
        claims.put("sub", this.getSubjectForJWTToken());
        claims.put("aud", this.getJwtAudience());

        //The JWT parameters are set
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, apiKeySecretBytes);

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    @Override
    public String getSubjectForJWTToken() throws JWTException {
        try{
            return xqJobDao.getAPIUsername();
        }
        catch (SQLException sqle){
            throw new JWTException("Error when retrieving JWT token subject from db");
        }

    }

    @Override
    public String getJwtAudience() {
        return JWT_AUDIENCE;
    }

    @Override
    public String getJwtIssuer() {
        return JWT_ISSUER;
    }

    @Override
    public String getJwtApiKey() {
        return JWT_API_KEY;
    }
}
