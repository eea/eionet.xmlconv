package eionet.gdem.api.jwt.web;

import eionet.gdem.Properties;
import eionet.gdem.api.jwt.service.JWTService;
import eionet.gdem.api.jwt.service.impl.JWTServiceImpl;
import eionet.gdem.api.qa.web.QaController;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.services.AclOperationsService;
import org.apache.commons.lang.time.StopWatch;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@Validated
public class JWTApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QaController.class);

    private final JWTService jwtService = new JWTServiceImpl();

    @Autowired
    AclOperationsService aclOperationsService;
    private String SSO_LOGIN_PAGE_URI = Properties.SSO_LOGIN_URL;

    @RequestMapping(value = "/jwt/generateJwtToken/", method = RequestMethod.POST)
    public ResponseEntity<HashMap<String,String>> generateJWTToken(HttpServletRequest request) {
        StopWatch timer = new StopWatch();
        timer.start();
        try {
            LOGGER.info("Generating jwt token was requested");

            //authenticate username password and check permissions

            /* Retrieve credentials from Basic Authentication */
            String authentication = request.getHeader("Authorization");
            if (authentication == null || !authentication.startsWith("Basic ")) {
                String message = "No Basic authentication received";
                LOGGER.error(message);
                LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
                results.put("message", message);
                return new ResponseEntity<>(results, HttpStatus.UNAUTHORIZED);
            }

            String[] authenticationArray = authentication.split(" ");
            if (authenticationArray.length != 2) {
                String message = "Basic Authentication error";
                LOGGER.error(message);
                LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
                results.put("message", message);
                return new ResponseEntity<>(results, HttpStatus.UNAUTHORIZED);
            }
            String encodedUsernamePassword = authenticationArray[1].trim();
            byte[] decodedBytes = Base64.getDecoder().decode(encodedUsernamePassword);
            String decodedString = new String(decodedBytes);
            String[] decodedUsernamePassword = decodedString.split(":");
            if (decodedUsernamePassword.length != 2) {
                String message = "Credentials were provided incorrectly";
                LOGGER.error(message);
                LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
                results.put("message", message);
                return new ResponseEntity<>(results, HttpStatus.UNAUTHORIZED);
            }
            String username = decodedUsernamePassword[0];
            String password = decodedUsernamePassword[1];

            LOGGER.info(String.format("User %s has requested generation of a JWT token", username));

            if (authenticateUser(username, password) == false) {
                String message = "User does not exist";
                LOGGER.error(message);
                LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
                results.put("message", message);
                return new ResponseEntity<>(results, HttpStatus.UNAUTHORIZED);
            }

            if (!this.checkIfUserHasAdminRights(username)) {
                String message = "User " + username + " does not have admin rights";
                LOGGER.error(message);
                LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
                results.put("message", message);
                return new ResponseEntity<>(results, HttpStatus.UNAUTHORIZED);
            }

            LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
            String jwtToken = getJwtService().generateJWTToken();
            results.put("token",jwtToken);
            timer.stop();
            LOGGER.info(String.format("Generating jwt token for user %s was completed, total time of execution: %s", username, timer.toString()));
            return new ResponseEntity<>(results, HttpStatus.OK);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
            results.put("message", e.getMessage());
            return new ResponseEntity<>(results, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected Boolean authenticateUser(String username, String password) throws Exception {

        String executionParam = getExecutionValueFromSSOPage();

        HttpPost post = new HttpPost(this.getSSO_LOGIN_PAGE_URI());

        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("username", username));
        urlParameters.add(new BasicNameValuePair("password", password));
        urlParameters.add(new BasicNameValuePair("execution", executionParam));
        urlParameters.add(new BasicNameValuePair("_eventId", "submit"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            Integer statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == org.apache.http.HttpStatus.SC_OK){
                return true;
            }
            return false;
        }
        catch (Exception e){
            throw (e);
        }
    }

    protected Boolean checkIfUserHasAdminRights(String username) throws Exception {
        LOGGER.info(String.format("Checking if user %s has admin rights", username));
        Hashtable<String, Vector<String>> groupsAndUsersHash = getAclOperationsService().getRefreshedGroupsAndUsersHashTable(false);
        if(groupsAndUsersHash.get("gdem_admin") == null){
            throw new Exception("No gdem_admin role was found.");
        }
        Vector<String> adminUsers = groupsAndUsersHash.get("gdem_admin");
        if(adminUsers.contains(username)){
            return true;
        }
        return false;
    }

    protected AclOperationsService getAclOperationsService() {
        return aclOperationsService;
    }

    protected String getExecutionValueFromSSOPage() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String pageHtml = restTemplate.getForObject(this.getSSO_LOGIN_PAGE_URI(), String.class);
        Document doc = Jsoup.parse(pageHtml);
        Element execution = doc.select("input[name=execution]").first();
        if(execution == null){
            String errorMsg = String.format("The execution input type from the %s page does not exist.", this.getSSO_LOGIN_PAGE_URI());
            throw new Exception(errorMsg);
        }
        if(execution.val() == null || execution.val().length()==0){
            String errorMsg = String.format("The execution input type from the %s page has empty value.", this.getSSO_LOGIN_PAGE_URI());
            throw new Exception(errorMsg);
        }
        return execution.val();
    }

    protected String getSSO_LOGIN_PAGE_URI() {
        return SSO_LOGIN_PAGE_URI;
    }

    protected JWTService getJwtService() {
        return jwtService;
    }

}
