package eionet.gdem.web.struts.qasandbox;

import eionet.gdem.Properties;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Uploads files
 * @author George Sofianos
 */

@MultipartConfig(fileSizeThreshold=1024*1024, maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
public class TmpUploadServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmpUploadServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        try {
            String tmpdir = Properties.appRootFolder + File.separator + "tmpfile";
            String sessionDir = tmpdir + File.separator + session.getId();
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            ProgressListenerImpl progressListener = new ProgressListenerImpl();
            upload.setProgressListener(progressListener);
            List<FileItem> items = upload.parseRequest(req);
            for (FileItem item : items) {
                if (item.isFormField()) {
                    // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString();
                    // ... (do your job here)
                } else {
                    // Process form file field (input type="file").
                    String fieldName = item.getFieldName();
                    String fileName = FilenameUtils.getName(item.getName());
                    InputStream fileContent = item.getInputStream();
                    try {
                        Files.createDirectory(Paths.get(sessionDir));
                    } catch (FileAlreadyExistsException e) {
                        // Ignore
                    }

                    File uploadFile = new File(sessionDir + File.separator + fileName);
                    LOGGER.info("Saving temporary file: " + item.getName() + " for SessionId: " + session.getId());
                    item.write(uploadFile);
                    fileContent.close();
                    resp.setContentType("application/json");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    String outputUrl = "https://" + Properties.appHost + ("".equals(req.getContextPath()) ? "" : req.getContextPath()) + "/tmpfile/" + session.getId() + "/" + fileName;
                    PrintWriter out = resp.getWriter();
                    out.write("{ \"url\": \"" + outputUrl + "\" }");
                    out.close();
                }
            }
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        } catch (Exception e) {
            throw new ServletException("Unexpected error occurred.", e);
        }
    }

    private class ProgressListenerImpl implements ProgressListener {

        private long num100Ks = 0;

        private long theBytesRead = 0;
        private long theContentLength = -1;
        private int whichItem = 0;
        private int percentDone = 0;
        private boolean contentLengthKnown = false;

        @Override
        public void update(long bytesRead, long contentLength, int items) {
            if (contentLength > -1) {
                contentLengthKnown = true;
            }
            theBytesRead = bytesRead;
            theContentLength = contentLength;
            whichItem = items;

            long nowNum100Ks = bytesRead / 1000000;
            // Only run this code once every 1MB
            if (nowNum100Ks > num100Ks) {
                num100Ks = nowNum100Ks;
                if (contentLengthKnown) {
                    percentDone = (int) Math.round(100.00 * bytesRead / contentLength);
                }
                //LOGGER.info("File transferred: " + getMessage());
            }

        }

        public String getMessage() {
            if (theContentLength == -1) {
                return "" + theBytesRead + " of Unknown-Total bytes have been read.";
            } else {
                return "" + theBytesRead + " of " + theContentLength + " bytes have been read (" + percentDone + "% done).";
            }
        }
    }


}
