package eionet.gdem.dcm.business;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;

/**
 * The class acts as a adapter between XQuery engine and source file to be analyzed. If QA knows the login information for source
 * file, then it appends the information to the URL of source file parameter.
 * 
 * XQuery engine asks the file from generated URL from xmlconv instead of the original URL.
 * 
 * @author kaspeenr
 * 
 */
public class SourceFileManager {

    private static final int BYTE_BUF = 1024;

    private static LoggerIF _logger = GDEMServices.getLogger();

    /**
     * reads file from remote URL and writes it to the response stream using HTTP basic authentication
     * 
     * @param httpResponse
     * @param ticket
     * @param auth
     * @param source_url
     * @throws IOException
     */
    public void getFileBasicAuthentication(HttpServletResponse httpResponse, String ticket, String source_url) throws IOException {

        InputFile source = null;
        InputStream is = null;
        try {
            source = new InputFile(source_url);
            URL url = source.getURL();

            // open connection to source URL
            URLConnection uc = url.openConnection();

            if (ticket != null) {
                uc.addRequestProperty("Authorization", " Basic " + ticket);
            }

            // read response properties from URLConnection
            String contentType = uc.getContentType();
            int contentLength = uc.getContentLength();
            String contentEncoding = uc.getContentEncoding();
            is = uc.getInputStream();

            // set response properties
            httpResponse.setContentType(contentType);
            httpResponse.setContentLength(contentLength);
            httpResponse.setCharacterEncoding(contentEncoding);

            // write data into response
            int bufLen = 0;
            byte[] buf = new byte[BYTE_BUF];

            while ((bufLen = is.read(buf)) != -1)
                httpResponse.getOutputStream().write(buf, 0, bufLen);
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (Exception e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            httpResponse.getOutputStream().close();
            httpResponse.getOutputStream().flush();
        }
    }

    /**
     * reads file from remote URL and writes it to the response stream without authentication
     * 
     * @param httpResponse
     * @param ticket
     * @param auth
     * @param source_url
     * @throws IOException
     */
    public void getFileNoAuthentication(HttpServletResponse httpResponse, String source_url) throws IOException {

        InputFile source = null;
        InputStream is = null;
        try {
            source = new InputFile(source_url);
            URL url = source.getURL();

            // open connection to source URL
            URLConnection uc = url.openConnection();

            // read response properties from URLConnection
            String contentType = uc.getContentType();
            int contentLength = uc.getContentLength();
            String contentEncoding = uc.getContentEncoding();
            is = uc.getInputStream();

            // set response properties
            httpResponse.setContentType(contentType);
            httpResponse.setContentLength(contentLength);
            httpResponse.setCharacterEncoding(contentEncoding);

            // write data into response
            int bufLen = 0;
            byte[] buf = new byte[BYTE_BUF];

            while ((bufLen = is.read(buf)) != -1)
                httpResponse.getOutputStream().write(buf, 0, bufLen);
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (Exception e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            httpResponse.getOutputStream().close();
            httpResponse.getOutputStream().flush();
        }
    }

    /**
     * Generates the URL for retreiving source file through QA with credentials
     * 
     * @param ticket
     * @param source_url
     * @param isTrustedMode
     * @return URL
     * @throws IOException
     * @throws MalformedURLException
     */
    public static String getSourceFileAdapterURL(String ticket, String source_url, boolean isTrustedMode)
            throws MalformedURLException, IOException {
        StringBuffer ret = new StringBuffer();
        InputFile source = null;

        try {
            source = new InputFile(source_url);

            // the source URL is already the URL of Source file Adapter
            if (source_url.indexOf(Constants.GETSOURCE_URL) > -1)
                return source.toString();

            source.setTrustedMode(isTrustedMode);
            if (Utils.isNullStr(ticket))
                ticket = source.getAuthentication();

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
                }
            }
        }
        return ret.toString();
    }
}
