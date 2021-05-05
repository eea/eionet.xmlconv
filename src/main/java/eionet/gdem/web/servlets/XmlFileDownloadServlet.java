package eionet.gdem.web.servlets;

import eionet.gdem.Properties;
import org.apache.commons.lang.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 *
 */
@WebServlet(value = {"/xmlfile/*"})
public class XmlFileDownloadServlet extends FileServlet {
    @Override
    protected File getFile(HttpServletRequest request) throws IllegalArgumentException {
        String urlPath = null;
        String filePath = null;
        try {
            urlPath = URLDecoder.decode(StringUtils.substringAfter(request.getRequestURI(), request.getContextPath()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException();
        }

        filePath = getFilePath(request, urlPath);
        return new File(filePath);
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

}
