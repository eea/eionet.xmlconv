package eionet.gdem.web.servlets;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.security.service.AuthTokenService;
import eionet.gdem.utils.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

/**
 *
 */
@WebServlet(value = {"/xmlfile/*", "/queries/*", "/schema/*", "/tmp/*", "/tmpfile/*", "/xsl/*"})
public class FileDownloadServlet extends FileServlet {
    @Override
    protected File getFile(HttpServletRequest request) throws IllegalArgumentException, IOException, SignOnException, JWTException {
        String urlPath = null;
        String filePath = null;
        try {
            urlPath = URLDecoder.decode(StringUtils.substringAfter(request.getRequestURI(), request.getContextPath()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException();
        }
        String referer = request.getHeader("referer");
        if (referer!=null) {
            URI uri = null;
            try {
                uri = new URI(referer);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException();
            }
            String domain = uri.getHost();
            if (Properties.appHost.contains(domain)) {
                String userName = (String) request.getSession().getAttribute("user");
                if (userName!=null && userHasPerm(userName)) {
                    filePath = downloadFile(request, urlPath);
                }
            } else {
                throw new SignOnException("incorrect domain " + domain);
            }
        } else {
            String rawAuthenticationToken = request.getHeader(Properties.jwtHeader);
            AuthTokenService authTokenService = getAuthTokenService();
            String parsedAuthenticationToken = authTokenService.getParsedAuthenticationToken(rawAuthenticationToken, Properties.jwtHeaderSchema);
            if (authTokenService.check(parsedAuthenticationToken)) {
                if (authTokenService.verifyUser(parsedAuthenticationToken)) {
                    filePath = downloadFile(request, urlPath);
                }
            }
        }
        return new File(filePath);
    }

    protected String downloadFile(HttpServletRequest request, String urlPath) {
        String filePath;
        filePath = Properties.appRootFolder + urlPath;
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            throw new IllegalArgumentException();
        }
        return filePath;
    }

    protected boolean userHasPerm(String userName) throws SignOnException {
       return SecurityUtil.hasPerm(userName, "/" + Constants.ACL_WQ_PATH, "v");
    }

    protected AuthTokenService getAuthTokenService(){
        return (AuthTokenService) SpringApplicationContext.getBean(AuthTokenService.class);
    }
}
