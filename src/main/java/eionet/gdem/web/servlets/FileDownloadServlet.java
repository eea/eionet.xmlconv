package eionet.gdem.web.servlets;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.utils.SecurityUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 *
 */
@WebServlet(value = {"/queries/*", "/tmp/*", "/tmpfile/*"})
public class FileDownloadServlet extends FileServlet {
    @Override
    protected File getFile(HttpServletRequest request) throws IllegalArgumentException, SignOnException {
        String urlPath = null;
        String filePath = null;
        try {
            urlPath = URLDecoder.decode(StringUtils.substringAfter(request.getRequestURI(), request.getContextPath()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException();
        }

        String userName = getUser(request);
        if (userHasPerm(userName)) {
            filePath = getFilePath(request, urlPath);
        }
        return new File(filePath);
    }

    protected String getUser(HttpServletRequest request) throws SignOnException {
        String userName = (String) request.getSession().getAttribute("user");
        if (userName == null) {
            throw new SignOnException("Unauthorized. Must be logged in.");
        }
        return userName;
    }

    protected String getFilePath(HttpServletRequest request, String urlPath) {
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

}
