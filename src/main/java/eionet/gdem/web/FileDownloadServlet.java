/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 3
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Juhan Voolaid
 */

package eionet.gdem.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.stream.FileImageInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Properties;

/**
 * Servlet for downloading files that users have uploaded. Files will be available for downloading from ${app.home} subfolders:
 * queries, schema, tmp, xmlfile and xsl.
 *
 * @author Juhan Voolaid
 */
public class FileDownloadServlet extends HttpServlet {

    private static final Log LOGGER = LogFactory.getLog(FileDownloadServlet.class);

    private static final String CONTENT_TYPE_XML = "text/xml";
    private static final String CONTENT_TYPE_HTML = "text/html";
    private static final String CONTENT_TYPE_TEXT = "text/plain";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,
            FileNotFoundException {

        String urlPath = StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());
        String filePath = Properties.appRootFolder + urlPath;
        String extension = StringUtils.substringAfterLast(urlPath, ".");

        response.setContentType(getContentType(extension));

        FileImageInputStream is = new FileImageInputStream(new File(filePath));

        int read = 0;
        byte[] bytes = new byte[1024];
        OutputStream os = response.getOutputStream();
        while ((read = is.read(bytes)) != -1) {
            os.write(bytes, 0, read);
        }
        os.flush();
        os.close();
    }

    private String getContentType(String extension) {
        if ("html".equalsIgnoreCase(extension)) {
            return CONTENT_TYPE_HTML;
        }
        if ("xml".equalsIgnoreCase(extension) || "xsl".equalsIgnoreCase(extension) || "xsd".equalsIgnoreCase(extension)) {
            return CONTENT_TYPE_XML;
        }
        return CONTENT_TYPE_TEXT;
    }
}
