/*
 * Created on 21.01.2009
 */
package eionet.gdem.utils;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.web.spring.login.LoginController;
import junit.framework.TestCase;
import edu.yale.its.tp.cas.client.filter.CASFilter;
import eionet.gdem.test.mocks.MockHttpSession;
import eionet.gdem.test.mocks.MockServletContext;
import eionet.gdem.test.mocks.MockServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * This class tests teh different methods in SecurityUtil class. Ttested methods are: getLoginUrl(), getLogoutUrl,
 * getUrlWithContextPath()
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS SecurityUtilTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class SecurityUtilTest {

    /**
     * If port=80 and scheme=http, then the returned URL should not contain port number
     * 
     * @throws Exception
     */
    @Test
    public void testGetUrlWithContextPath1() throws Exception {
        MockServletRequest req = new MockServletRequest();
        req.setServerName("testserver");
        req.setServerPort(80);
        req.setScheme("http");
        req.setContextPath("/context");

        String url = SecurityUtil.getUrlWithContextPath(req);
        assertEquals("http://testserver/context", url);
    }

    /**
     * If port!=80 and scheme=http, then the returned URL should contain port number
     * 
     * @throws Exception
     */
    @Test
    public void testGetUrlWithContextPath2() throws Exception {
        MockServletRequest req = new MockServletRequest();
        req.setServerName("testserver");
        req.setServerPort(8080);
        req.setScheme("http");
        req.setContextPath("/context");

        String url = SecurityUtil.getUrlWithContextPath(req);
        assertEquals("http://testserver:8080/context", url);

    }

    /**
     * If port=443 and scheme=https, then the returned URL should not contain the port number
     * 
     * @throws Exception
     */
    @Test
    public void testGetUrlWithContextPath3() throws Exception {
        MockServletRequest req = new MockServletRequest();
        req.setServerName("testserver");
        req.setServerPort(443);
        req.setScheme("https");
        req.setContextPath("/context");

        String url = SecurityUtil.getUrlWithContextPath(req);
        assertEquals("https://testserver/context", url);

    }

    /**
     * If port!=443 and scheme=https, then the returned URL should contain port number
     * 
     * @throws Exception
     */
    @Test
    public void testGetUrlWithContextPath4() throws Exception {
        MockServletRequest req = new MockServletRequest();
        req.setServerName("testserver");
        req.setServerPort(8080);
        req.setScheme("https");
        req.setContextPath("/context");

        String url = SecurityUtil.getUrlWithContextPath(req);
        assertEquals("https://testserver:8080/context", url);
    }

    /**
     * test the local login URL and afterLogin session attribute
     * 
     * @throws Exception
     */
    @Test
    public void testGetLoginUrlLocal() throws Exception {
        MockServletContext context = new MockServletContext();
        MockHttpSession session = new MockHttpSession();
        MockServletRequest req = new MockServletRequest();
        session.setServletContext(context);
        req.setSession(session);
        req.setServerName("testserver");
        req.setServerPort(80);
        req.setScheme("http");
        req.setContextPath("/context");
        req.setRequestURL("http://testserver/context/servlet");
        req.setQueryString("id=222");

        String url = SecurityUtil.getLoginURL(req);
        assertEquals("http://testserver/context/do/login", url);
        assertEquals("http://testserver/context/servlet?id=222",
                (String) session.getAttribute(LoginController.AFTER_LOGIN_ATTR_NAME));

    }

    /**
     * test the remote login URL and afterLogin session attribute
     * 
     * @throws Exception
     */
    @Test
    public void testGetLoginUrlSSO() throws Exception {
        MockServletContext context = new MockServletContext();
        MockHttpSession session = new MockHttpSession();
        MockServletRequest req = new MockServletRequest();
        context.setInitParameter(CASFilter.LOGIN_INIT_PARAM, "http://ssoserver/login");
        session.setServletContext(context);
        req.setSession(session);
        req.setServerName("testserver");
        req.setServerPort(80);
        req.setScheme("http");
        req.setContextPath("/context");
        req.setRequestURL("http://testserver/context/servlet");
        req.setQueryString("id=222");

        String url = SecurityUtil.getLoginURL(req);
        assertEquals("http://ssoserver/login?service=http%3A%2F%2Ftestserver%2Fcontext%2Fdo%2FafterLogin", url);
        assertEquals("http://testserver/context/servlet?id=222",
                (String) session.getAttribute(LoginController.AFTER_LOGIN_ATTR_NAME));

    }

    /**
     * test the local logout URL
     * 
     * @throws Exception
     */
    @Test
    public void testGetLogoutUrlLocal() throws Exception {
        MockServletContext context = new MockServletContext();
        MockHttpSession session = new MockHttpSession();
        MockServletRequest req = new MockServletRequest();
        session.setServletContext(context);
        req.setSession(session);
        req.setServerName("testserver");
        req.setServerPort(80);
        req.setScheme("http");
        req.setContextPath("/context");
        req.setRequestURL("http://testserver/context/servlet");
        req.setQueryString("id=222");

        String url = SecurityUtil.getLogoutURL(req);
        assertEquals("start", url);

    }

    /**
     * test the remote logout URL
     * 
     * @throws Exception
     */
    @Test
    public void testGetLogoutUrlSSO() throws Exception {
        MockServletContext context = new MockServletContext();
        MockHttpSession session = new MockHttpSession();
        MockServletRequest req = new MockServletRequest();
        context.setInitParameter(CASFilter.LOGIN_INIT_PARAM, "http://ssoserver/login");
        session.setServletContext(context);
        req.setSession(session);
        req.setServerName("testserver");
        req.setServerPort(80);
        req.setScheme("http");
        req.setContextPath("/context");
        req.setRequestURL("http://testserver/context/servlet");
        req.setQueryString("id=222");

        String url = SecurityUtil.getLogoutURL(req);
        assertEquals("http://ssoserver/logout?url=http%3A%2F%2Ftestserver%2Fcontext", url);

    }
}
