package eionet.gdem.api.jwt.web;

import eionet.gdem.api.jwt.service.JWTService;
import eionet.gdem.services.impl.AclOperationsServiceImpl;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class JWTApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JWTService jwtService;

    @Mock
    JWTApiController jwtApiController;

    MockHttpServletRequest request = new MockHttpServletRequest();

    @Mock
    AclOperationsServiceImpl aclOperationsService;
    Hashtable<String, Vector<String>> hashtableWithoutAdmins;
    Hashtable<String, Vector<String>> hashtableWithAdmins;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(jwtApiController.getJwtService()).thenReturn(jwtService);
        when(jwtApiController.getJwtService().generateJWTToken()).thenReturn("testToken");
        when(jwtApiController.generateJWTToken(request)).thenCallRealMethod();
        when(jwtApiController.getExecutionValueFromSSOPage()).thenCallRealMethod();
        when(jwtApiController.authenticateUser("wrongUsername", "WrongPassword")).thenCallRealMethod();
        when(jwtApiController.authenticateUser("userExistsUsername", "userExistsPassword")).thenReturn(true);
        when(jwtApiController.checkIfUserHasAdminRights("anthaant")).thenCallRealMethod();
        when(jwtApiController.checkIfUserHasAdminRights("userNotFound")).thenCallRealMethod();
        when(jwtApiController.checkIfUserHasAdminRights("heinlja")).thenCallRealMethod();
        when(jwtApiController.checkIfUserHasAdminRights("userExistsUsername")).thenReturn(true);
        when(jwtApiController.getSSO_LOGIN_PAGE_URI()).thenReturn("https://sso.eionet.europa.eu/login");
        when(jwtApiController.getAclOperationsService()).thenReturn(aclOperationsService);
        mockMvc = MockMvcBuilders.standaloneSetup(jwtApiController).build();
        hashtableWithoutAdmins = createGroupsAndUsersHashtableWithoutAdmins();
        hashtableWithAdmins = createGroupsAndUsersHashtableWithAdmins();
    }

    /* Test case: no paramerters in the header of the request */
    @Test
    public void testGenerateJWTTokenNoHeader() throws Exception {
        request.setMethod("POST");
        ResponseEntity<HashMap<String,String>> response = jwtApiController.generateJWTToken(request);
        Assert.assertThat(response, is(notNullValue()));
        Assert.assertThat(response.getStatusCode().toString(), is("401"));
        Assert.assertThat(response.getBody().toString(), is("{message=No Basic authentication received}"));
    }

    /* Test case: no basic authentication params in the header of the request */
    @Test
    public void testGenerateJWTTokenNoBasicAuthentication() throws Exception {
        String usernamePassword = "wrongUsername:WrongPassword";
        byte[] encoding = Base64.getEncoder().encode(usernamePassword.getBytes());
        request.setMethod("POST");
        request.addHeader("Authorization", "NotBasic " + new String(encoding));
        ResponseEntity<HashMap<String,String>> response = jwtApiController.generateJWTToken(request);
        Assert.assertThat(response, is(notNullValue()));
        Assert.assertThat(response.getStatusCode().toString(), is("401"));
        Assert.assertThat(response.getBody().toString(), is("{message=No Basic authentication received}"));
    }

    /* Test case: the given credentials do not belong to an eionet user */
    @Test
    public void testGenerateJWTTokenUserDoesNotExist() throws Exception {
        String usernamePassword = "wrongUsername:WrongPassword";
        byte[] encoding = Base64.getEncoder().encode(usernamePassword.getBytes());
        request.setMethod("POST");
        request.addHeader("Authorization", "Basic " + new String(encoding));

        ResponseEntity<HashMap<String,String>> response = jwtApiController.generateJWTToken(request);
        Assert.assertThat(response, is(notNullValue()));
        Assert.assertThat(response.getStatusCode().toString(), is("401"));
        Assert.assertThat(response.getBody().toString(), is("{message=User does not exist}"));
    }

    /* Test case: the user does not have admin rights */
    @Test
    public void testGenerateJWTTokenUserIsNotAdmin() throws Exception {
        String username = "userExistsUsername";
        String password = "userExistsPassword";
        String usernamePassword = username + ":" + password;
        byte[] encoding = Base64.getEncoder().encode(usernamePassword.getBytes());
        request.setRequestURI("/api/jwt/generateJWTToken");
        request.setMethod("POST");
        request.addHeader("Authorization", "Basic " + new String(encoding));
        when(jwtApiController.checkIfUserHasAdminRights("userExistsUsername")).thenReturn(false);

        ResponseEntity<HashMap<String,String>> response = jwtApiController.generateJWTToken(request);
        Assert.assertThat(response, is(notNullValue()));
        Assert.assertThat(response.getStatusCode().toString(), is("401"));
        Assert.assertThat(response.getBody().toString(), is("{message=User userExistsUsername does not have admin rights}"));
    }

    /* Test case: successful generation of token */
    @Test
    public void testGenerateJWTTokenSuccessful() throws Exception {
        String username = "userExistsUsername";
        String password = "userExistsPassword";

        String usernamePassword = username + ":" + password;
        byte[] encoding = Base64.getEncoder().encode(usernamePassword.getBytes());
        request.setRequestURI("/api/jwt/generateJWTToken");
        request.setMethod("POST");
        request.addHeader("Authorization", "Basic " + new String(encoding));

        ResponseEntity<HashMap<String,String>> response = jwtApiController.generateJWTToken(request);
        Assert.assertThat(response, is(notNullValue()));
        Assert.assertThat(response.getStatusCode().toString(), is("200"));
        Assert.assertThat(response.getBody().toString(), is("{token=testToken}"));
    }


    /* Test case: get execution value from SSO page successful*/
    @Test
    public void testGetExecutionValueFromSSOPageSuccessful() throws Exception {
        String execution = jwtApiController.getExecutionValueFromSSOPage();
        Assert.assertThat(execution, is(notNullValue()));
    }

    /* Test case: get execution value from wrong page*/
    @Test(expected = Exception.class)
    public void testGetExecutionValueFromSSOPageWrongURI() throws Exception {
        when(jwtApiController.getSSO_LOGIN_PAGE_URI()).thenReturn("https://www.google.com/");
        try
        {
            String execution = jwtApiController.getExecutionValueFromSSOPage();
        }
        catch(Exception e)
        {
            String expectedMessage = "The execution input type from the https://www.google.com/ page does not exist.";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Wrong URI - exception did not throw!");
    }

    /* Test case: authenticate user wrong credentials*/
    @Test
    public void testAuthenticateUserWrongCredentials() throws Exception {
        Boolean result = jwtApiController.authenticateUser("wrongUsername", "WrongPassword");
        Assert.assertThat(result, is(false));
    }

    /* Test case: authenticate user correct credentials*/
    @Test
    public void testAuthenticateUserCorrectCredentials() throws Exception {
        Boolean result = jwtApiController.authenticateUser("userExistsUsername", "userExistsPassword");
        Assert.assertThat(result, is(true));
    }

    /* Test case: get execution value from wrong page*/
    @Test(expected = Exception.class)
    public void testAuthenticateUserWrongURI() throws Exception {
        when(jwtApiController.getSSO_LOGIN_PAGE_URI()).thenReturn("https://www.google.com/");
        try
        {
            Boolean result = jwtApiController.authenticateUser("wrongUsername", "WrongPassword");
        }
        catch(Exception e)
        {
            String expectedMessage = "The execution input type from the https://www.google.com/ page does not exist.";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Wrong URI - exception did not throw!");
    }

    /* Test case: admin group doesn't exist */
    @Test(expected = Exception.class)
    public void testCheckIfUserHasAdminRightsAdminGroupDoesntExist() throws Exception {
        when(aclOperationsService.getRefreshedGroupsAndUsersHashTable(anyBoolean())).thenReturn(hashtableWithoutAdmins);
        try
        {
            Boolean result = jwtApiController.checkIfUserHasAdminRights("anthaant");
        }
        catch(Exception e)
        {
            String expectedMessage = "No gdem_admin role was found.";
            Assert.assertThat(e.getMessage(), is(expectedMessage));
            throw e;
        }
        fail("Admin group does not exist - exception did not throw!");
    }

    /* Test case: user was not found */
    @Test
    public void testCheckIfUserHasAdminRightsUserNotFound() throws Exception {
        when(aclOperationsService.getRefreshedGroupsAndUsersHashTable(anyBoolean())).thenReturn(hashtableWithAdmins);
        Boolean result = jwtApiController.checkIfUserHasAdminRights("userNotFound");
        Assert.assertThat(result, is(false));
    }

    /* Test case: user does not have admin rights */
    @Test
    public void testCheckIfUserHasAdminRightsUserIsNotAdmin() throws Exception {
        when(aclOperationsService.getRefreshedGroupsAndUsersHashTable(anyBoolean())).thenReturn(hashtableWithAdmins);
        Boolean result = jwtApiController.checkIfUserHasAdminRights("heinlja");
        Assert.assertThat(result, is(false));
    }

    /* Test case: user has admin rights */
    @Test
    public void testCheckIfUserHasAdminRightsUserIsAdmin() throws Exception {
        when(aclOperationsService.getRefreshedGroupsAndUsersHashTable(anyBoolean())).thenReturn(hashtableWithAdmins);
        Boolean result = jwtApiController.checkIfUserHasAdminRights("anthaant");
        Assert.assertThat(result, is(true));
    }

    private Hashtable<String, Vector<String>> createGroupsAndUsersHashtableWithAdmins(){
        Hashtable<String, Vector<String>> hashtable = new Hashtable<>();
        Vector<String> group = new Vector<>();
        group.add("test1");
        group.add("test2");
        group.add("test3");
        hashtable.put("group", group);

        Vector<String> admins = new Vector<>();
        admins.add("test1");
        admins.add("test4");
        admins.add("anthaant");
        admins.add("test5");
        hashtable.put("gdem_admin", admins);

        Vector<String> xmlgroup = new Vector<>();
        xmlgroup.add("test1");
        xmlgroup.add("test4");
        xmlgroup.add("test5");
        hashtable.put("xmlgroup", xmlgroup);

        return hashtable;
    }

    private Hashtable<String, Vector<String>> createGroupsAndUsersHashtableWithoutAdmins(){
        Hashtable<String, Vector<String>> hashtable = new Hashtable<>();
        Vector<String> group = new Vector<>();
        group.add("test1");
        group.add("test2");
        group.add("test3");
        hashtable.put("group", group);

        Vector<String> othergroup = new Vector<>();
        othergroup.add("test1");
        othergroup.add("test4");
        othergroup.add("anthaant");
        othergroup.add("test5");
        hashtable.put("othergroup", othergroup);

        Vector<String> xmlgroup = new Vector<>();
        xmlgroup.add("test1");
        xmlgroup.add("test4");
        xmlgroup.add("test5");
        hashtable.put("xmlgroup", xmlgroup);

        return hashtable;
    }
}
