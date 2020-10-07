package eionet.gdem.api.jwt.web;

import eionet.gdem.api.jwt.service.JWTService;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.services.impl.AclOperationsServiceImpl;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Hashtable;
import java.util.Vector;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class JWTApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JWTService jwtService;

    JWTApiController jwtApiController;

    @Mock
    AclOperationsServiceImpl aclOperationsService;
    Hashtable<String, Vector<String>> hashtableWithoutAdmins;
    Hashtable<String, Vector<String>> hashtableWithAdmins;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.jwtApiController = new JWTApiController();
        mockMvc = MockMvcBuilders.standaloneSetup(jwtApiController).build();
        when(jwtApiController.getAclOperationsService()).thenReturn(aclOperationsService);
        hashtableWithoutAdmins = createGroupsAndUsersHashtableWithoutAdmins();
        hashtableWithAdmins = createGroupsAndUsersHashtableWithAdmins();
    }

    /* Test case: authenticate user wrong credentials*/
    @Test
    public void testAuthenticateUserWrongCredentials() throws Exception {
        Boolean result = jwtApiController.authenticateUser("wrongUsername", "WrongPassword");
        Assert.assertThat(result, is(false));
    }

    /* Test case: authenticate user correct credentials
     * The following test has been commented out since a valid user's credentials should not be existing in the code.
     * However, it has been tested and it works
     * */
    @Test
    public void testAuthenticateUserCorrectCredentials() throws Exception {
        Boolean result = jwtApiController.authenticateUser("", "");
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
            String expectedMessage = "No dd_admin role was found.";
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
        Vector<String> authors = new Vector<>();
        authors.add("test1");
        authors.add("test2");
        authors.add("test3");
        hashtable.put("dd_author", authors);

        Vector<String> admins = new Vector<>();
        admins.add("test1");
        admins.add("test4");
        admins.add("anthaant");
        admins.add("test5");
        hashtable.put("dd_admin", admins);

        Vector<String> xmlgroup = new Vector<>();
        xmlgroup.add("test1");
        xmlgroup.add("test4");
        xmlgroup.add("test5");
        hashtable.put("xmlgroup", xmlgroup);

        return hashtable;
    }

    private Hashtable<String, Vector<String>> createGroupsAndUsersHashtableWithoutAdmins(){
        Hashtable<String, Vector<String>> hashtable = new Hashtable<>();
        Vector<String> authors = new Vector<>();
        authors.add("test1");
        authors.add("test2");
        authors.add("test3");
        hashtable.put("dd_author", authors);

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
