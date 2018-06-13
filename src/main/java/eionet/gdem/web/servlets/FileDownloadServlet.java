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
@WebServlet(value = {"/xmlfile/*", "/queries/*", "/schema/*", "/tmp/*", "/log/*", "/tmpfile/*", "/xsl/*"})
public class FileDownloadServlet extends FileServlet {
    @Override
    protected File getFile(HttpServletRequest request) throws IllegalArgumentException {

        String urlPath = null;
        try {
            urlPath = URLDecoder.decode(StringUtils.substringAfter(request.getRequestURI(), request.getContextPath()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException();
        }
        String filePath = Properties.appRootFolder + urlPath;

        /*String securityMessage = checkPermissions(request, urlPath);
        if (securityMessage != null) {
            handleNotAuthorised(securityMessage, request, response);
            return;
        }

        // Get the file object from the file store
        File file = new File(filePath);

        // If file was not found, send 404.
        if (file == null || !file.exists() || !file.isFile()) {
            handleFileNotFound("Could not find file by the following URI: " + request.getRequestURI(), request, response);
            return;
        }*/
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            throw new IllegalArgumentException();
        }

        return new File(filePath);
    }
}
