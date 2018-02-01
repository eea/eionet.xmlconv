package eionet.gdem.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import eionet.acl.AccessControlListIF;
import eionet.acl.AccessController;
import eionet.acl.AppUser;
import edu.yale.its.tp.cas.client.filter.CASFilter;
import eionet.acl.SignOnException;
import eionet.gdem.XMLConvException;
import org.springframework.web.util.UrlPathHelper;

/**
 * This is a class containing some utility methods for keeping security.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */
public final class SecurityUtil {

    /**
     * Private constructor
     */
    private SecurityUtil() {
        // do nothing
    }
    /**
     * Returns current user, or 'null', if the current session does not have user attached to it.
     *
     * @param request Request
     * @param attrName Attribute name
     */
    public static AppUser getUser(HttpServletRequest request, String attrName) {

        HttpSession session = request.getSession();
        AppUser user = session == null ? null : (AppUser) session.getAttribute(attrName);

        if (user == null) {
            String casUserName = (String) session.getAttribute(CASFilter.CAS_FILTER_USER);
            if (casUserName != null) {
                user = new CASUser(casUserName);
                session.setAttribute(attrName, user);
                session.setAttribute("user", user.getUserName());
            }
        } else if (user instanceof CASUser) {
            String casUserName = (String) session.getAttribute(CASFilter.CAS_FILTER_USER);
            if (casUserName == null) {
                user = null;
                session.removeAttribute(attrName);
                session.removeAttribute("user");
            } else if (!casUserName.equals(user.getUserName())) {
                user = new CASUser(casUserName);
                session.setAttribute(attrName, user);
                session.setAttribute("user", user.getUserName());
            }
        }

        if (user != null)
            return user;
        else
            return null;
    }

    /**
     * Checks if user has permissions
     * @param usr User
     * @param aclPath ACL Path
     * @param prm Permissions
     * @return True if user has permissions.
     * @throws SignOnException If an error occurs.
     */
    public static boolean hasPerm(String usr, String aclPath, String prm) throws SignOnException {
        AccessControlListIF acl;

        if (!aclPath.startsWith("/"))
            return false;

        boolean has = false;
        int i = aclPath.indexOf("/", 1);
        while (i != -1 && !has) {
            String subPath = aclPath.substring(0, i);
            try {
                acl = AccessController.getAcl(subPath);
            } catch (Exception e) {
                acl = null;
            }

            if (acl != null)
                has = acl.checkPermission(usr, prm);

            i = aclPath.indexOf("/", i + 1);
        }

        if (!has) {
            try {
                acl = AccessController.getAcl(aclPath);
            } catch (Exception e) {
                acl = null;
            }

            if (acl != null)
                has = acl.checkPermission(usr, prm);
        }

        return has;
    }

    /**
     * Returns login URL
     * @param request Request
     * @return login URL
     * @throws XMLConvException If an error occurs.
     */
    public static String getLoginURL(HttpServletRequest request) throws XMLConvException {

        String urlWithContextPath = getUrlWithContextPath(request);
        String result = "login";

        /*String afterLoginUrl = getRealRequestURL(request);*/
        String afterLoginUrl = new UrlPathHelper().getPathWithinApplication(request);
        // store the current page in the session to be able to come back after login
        if (afterLoginUrl != null && !afterLoginUrl.contains("login"))
            request.getSession().setAttribute("afterLogin", afterLoginUrl);

        String casLoginUrl = request.getSession().getServletContext().getInitParameter(CASFilter.LOGIN_INIT_PARAM);
        if (casLoginUrl != null) {

            StringBuffer loginUrl = new StringBuffer(casLoginUrl);
            loginUrl.append("?service=");
            try {
                // + request.getScheme() + "://" + SERVER_NAME + request.getContextPath() + "/login";
                loginUrl.append(URLEncoder.encode(urlWithContextPath + "/login/afterLogin", "UTF-8"));
                result = loginUrl.toString();
            } catch (UnsupportedEncodingException e) {
                throw new XMLConvException(e.toString(), e);
            }
        } else {
            // got to local login page
            result = urlWithContextPath + "/login/local";
        }

        return result;
    }

    /**
     * Returns logout URL
     * @param request Request
     * @return Logoug URL
     * @throws XMLConvException If an error occurs.
     */
    public static String getLogoutURL(HttpServletRequest request) throws XMLConvException {

        String result = "start";

        String casLoginUrl = request.getSession().getServletContext().getInitParameter(CASFilter.LOGIN_INIT_PARAM);
        if (casLoginUrl != null) {

            StringBuffer buf = new StringBuffer(casLoginUrl.replaceFirst("/login", "/logout"));
            try {
                buf.append("?url=").append(URLEncoder.encode(getUrlWithContextPath(request), "UTF-8"));
                result = buf.toString();
            } catch (UnsupportedEncodingException e) {
                throw new XMLConvException(e.toString(), e);
            }
        }
        // goto start page
        return result;
    }

    /**
     * Returns URL with context path
     * @param request Request
     * @return URL with context path
     */
    public static String getUrlWithContextPath(HttpServletRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Cannot take null parameters.");
        }

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        StringBuffer url = new StringBuffer(scheme);
        url.append("://").append(serverName);

        // if not http:80 or https:443, then add the port number
        if (!(scheme.equalsIgnoreCase("http") && serverPort == 80) && !(scheme.equalsIgnoreCase("https") && serverPort == 443)
                && serverPort > 0) {

            url.append(":");
            url.append(String.valueOf(serverPort));
        }

        url.append(request.getContextPath());
        return url.toString();
    }

    /**
     * Returns Real request URL
     * @param request Request
     * @return Real request URL
     */
    public static String getRealRequestURL(HttpServletRequest request) {

        HttpServletRequest tmpRequest = request;
        while (tmpRequest instanceof HttpServletRequestWrapper) {
            tmpRequest = (HttpServletRequest) ((HttpServletRequestWrapper) tmpRequest).getRequest();
        }
        StringBuffer url = tmpRequest.getRequestURL();

        if (tmpRequest.getQueryString() != null)
            url.append("?").append(tmpRequest.getQueryString());

        return url.toString();
    }
}

/**
 * CAS User class.
 * @author Unknown
 */
class CASUser extends AppUser {

    /**
     * Constructor
     * @param userName Username
     */
    public CASUser(String userName) {
        this.authenticatedUserName = userName;
    }

    private String authenticatedUserName;

    public String getUserName() {
        return authenticatedUserName;
    }
}
