package eionet.gdem.dcm.business;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * The class acts as a adapter between XQuery engine and source file to be analyzed.
 * If QA application knows the login information for source file, then it appends the
 * information to the URL of source file parameter.
 * <p/>
 * XQuery engine asks the file from generated URL from xmlconv instead of the original URL.
 *
 * @author kaspeenr
 */
public class SourceFileManager {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceFileManager.class);
    /**
     * Buffer size used when reading InputStream.
     */
    private static final int BYTE_BUF = 1024;

    /**
     * Reads file from remote URL and writes it to the response stream using HTTP basic authentication.
     *
     * @param httpResponse - HTTP Servlet Response
     * @param ticket       basic authentication token.
     * @param sourceUrl    - the URL to fetch.
     * @throws IOException - in case something happens during streaming the URL source.
     */
    public void getFileBasicAuthentication(HttpServletResponse httpResponse, String ticket, String sourceUrl) throws IOException {

        InputFile sourceFile = null;
        InputStream sourceFileInputStream = null;
        int bufLen = 0;
        try {
            LOGGER.info("Start to download file: " + sourceUrl);

            sourceFile = new InputFile(sourceUrl);
            URL url = sourceFile.getURL();
            // open connection to source URL
            URLConnection uc = url.openConnection();

            if (ticket != null) {
                uc.addRequestProperty("Authorization", " Basic " + ticket);
                LOGGER.info("Add basic authorization to request.");
            }

            // read response properties from URLConnection
            String contentType = uc.getContentType();
            int contentLength = uc.getContentLength();
            String contentEncoding = uc.getContentEncoding();

            // log response header
            StringBuilder logBuilder = new StringBuilder("Response header properties: ");
            logBuilder.append(contentType != null ? "Content-Type=" + contentType + "; " : "");
            logBuilder.append("Content-Length=" + contentLength);
            logBuilder.append(contentEncoding != null ? "Content-Encoding=" + contentEncoding + "; " : "");
            LOGGER.info(logBuilder.toString());

            sourceFileInputStream = uc.getInputStream();

            // If content type is null, then fall back to most likely content type
            if (contentType == null || "text/xml".equals(contentType)) {
                contentType = "text/xml;charset=utf-8";
            }
            // set response properties
            httpResponse.setContentType(contentType);
            httpResponse.setContentLength(contentLength);
            httpResponse.setCharacterEncoding(contentEncoding);

            // stream data to servlet response
            byte[] buf = new byte[BYTE_BUF];
            while ((bufLen = sourceFileInputStream.read(buf)) != -1) {
                httpResponse.getOutputStream().write(buf, 0, bufLen);
            }
        } catch (IOException ioe) {
            LOGGER.error("Failed to download file: " + sourceUrl + ". The exception is: " + ioe.toString());
            if (bufLen > 0) {
                LOGGER.info("Bytes read: " + bufLen);
            }
            throw ioe;
        } catch (Exception e) {
            LOGGER.error("Failed to download file: " + sourceUrl + ". The exception is: " + e.toString());
            if (bufLen > 0) {
                LOGGER.info("Bytes read: " + bufLen);
            }
        } finally {
            sourceFile.close();
            IOUtils.closeQuietly(sourceFileInputStream);
            try {
                httpResponse.getOutputStream().close();
            } catch (IOException e) {
                LOGGER.error("Failed to close HttpServletResponse OutputStream for file: " + sourceUrl + ". The exception is: " + e
                        .toString());
                throw e;
            }
            LOGGER.info("All resources closed for file: " + sourceUrl);
        }
    }

    /**
     * Reads file from remote URL and writes it to the response stream without authentication.
     *
     * @param httpResponse - HTTP Servlet Response
     * @param sourceUrl    - the URL to fetch.
     * @throws IOException - in case something happens during streaming the URL source.
     */
    public void getFileNoAuthentication(HttpServletResponse httpResponse, String sourceUrl) throws IOException {
        getFileBasicAuthentication(httpResponse, null, sourceUrl);
    }

    /**
     * Generates the URL for retrieving source file through QA with credentials.
     *
     * @param ticket Ticket Id
     * @param source_url Source URL
     * @param isTrustedMode Is trusted mode
     * @return URL
     * @throws IOException IO Exception
     * @throws MalformedURLException Malformed URL
     */
    public static String getSourceFileAdapterURL(String ticket, String source_url, boolean isTrustedMode)
            throws MalformedURLException, IOException {
        StringBuffer ret = new StringBuffer();
        InputFile source = null;

        try {
            source = new InputFile(source_url);

            // the source URL is already the URL of Source file Adapter
            if (source_url.indexOf(Constants.GETSOURCE_URL) > -1) {
                return source.toString();
            }

            source.setTrustedMode(isTrustedMode);
            if (Utils.isNullStr(ticket)) {
                ticket = source.getAuthentication();
            }

            /*
             * pass authentication information as ticket parameter and source file URl as source_url parameter
             */
            if (!Utils.isNullStr(ticket)) {
                ret.append(Properties.gdemURL);
                ret.append(Constants.GETSOURCE_URL);
                ret.append("?");
                ret.append(Constants.TICKET_PARAM);
                ret.append("=");
                ret.append(ticket);
                // ret.append("&");
                // ret.append(Constants.AUTH_PARAM);
                // ret.append("=BASIC");
                ret.append("&");
                ret.append(Constants.SOURCE_URL_PARAM);
                ret.append("=");
                ret.append(source.toString());
            } else {
                /*
                 * if we don't know the login information, then there's no sense to ask the source file throgh QA getsource adapter
                 * Use the direct link to the source file instead.
                 */
                ret.append(source_url);
            }
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret.toString();
    }
}
