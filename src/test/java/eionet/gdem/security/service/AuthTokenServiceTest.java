package eionet.gdem.security.service;

import eionet.gdem.security.TokenVerifier;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.security.service.impl.AuthTokenServiceImpl;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class AuthTokenServiceTest {

    @Mock
    TokenVerifier verifier;

    @Mock
    @Qualifier("apiuserdetailsservice")
    UserDetailsService userDetailsService;

    @Spy
    @InjectMocks
    AuthTokenServiceImpl authTokenService;

    private static final String TOKEN = "erewfsdfs";
    private static final String TOKEN_SCHEMA = "BEARER";
    private static final String USERNAME = "userName";
    private UserDetails userDetails;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userDetails = mock(UserDetails.class);
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.getUsername()).thenReturn(USERNAME);
    }

    @Test
    public void testGetParsedAuthenticationToken() throws JWTException {
        String result = authTokenService.getParsedAuthenticationTokenFromSchema(TOKEN_SCHEMA + " " + TOKEN, TOKEN_SCHEMA);
        assertEquals(TOKEN, result);
    }

    @Test
    public void testVerifyUser() throws JWTException, UnsupportedEncodingException {
        when(verifier.verify(Mockito.anyString())).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(userDetails);
        doNothing().when(authTokenService).setUserDetails(Mockito.any(UserDetails.class));
        boolean result = authTokenService.verifyUser(TOKEN);
        assertTrue(result);
    }
}











