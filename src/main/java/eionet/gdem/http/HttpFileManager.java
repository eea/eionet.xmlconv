package eionet.gdem.http;

import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IHostDao;
import eionet.gdem.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Provides a custom Http File manager.
 * Its purpose is to download files and serve them to the application in an efficient way.
 * @author George Sofianos
 */
public class HttpFileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFileManager.class);
    private CloseableHttpClient client;
    private CloseableHttpResponse response;

    public HttpFileManager() {
        this.client = HttpCacheClientFactory.getInstance();
    }

    /**
     * Fills outputstream with file content.
     * @param response Servlet response
     * @param ticket Authorization ticket
     * @param url File url
     * @throws IOException When an IO error occurs.
     * @throws URISyntaxException When the URL provided isn't a valid URI.
     */
    public void getHttpResponse(HttpServletResponse response, String ticket, String url) throws IOException, URISyntaxException {
        HttpEntity entity = getFileEntity(url, ticket);

        String contentType = null;
        Header contentTypeHeader = entity.getContentType();
        if (contentTypeHeader != null) {
            contentType = contentTypeHeader.getValue();
        }
        long contentLength = entity.getContentLength();

        String contentEncoding = null;
        Header contentEncodingHeader = entity.getContentEncoding();
        if (contentEncodingHeader != null) {
            contentEncoding = contentEncodingHeader.getValue();
        }

        // If content type is null, then fall back to most likely content type
        if (contentType == null || "text/xml".equals(contentType)) {
            contentType = "text/xml;charset=utf-8";
        }
        // set response properties
        response.setContentType(contentType);
        response.setContentLengthLong(contentLength);
        /*response.setCharacterEncoding(contentEncoding);*/
        entity.writeTo(response.getOutputStream());
    }

    public static CloseableHttpResponse getHeaderResponse(String ticket, String url, boolean isTrustedMode) throws IOException, URISyntaxException, XMLConvException {
        HttpHead httpHead = new HttpHead(url);

        if (  ! Utils.isNullStr(ticket) && isTrustedMode ) {
            httpHead.addHeader(HttpHeaders.AUTHORIZATION, " Basic " + ticket);
        }

        httpHead.addHeader(HttpHeaders.ACCEPT, "*/*");
        CloseableHttpResponse response = HttpClientBuilder.create().disableContentCompression().build().execute(httpHead);

        int statusCode = response.getStatusLine().getStatusCode();

        if ( statusCode == 200)
             return response;
        else
            throw new XMLConvException("Remote file status code not ok: " + statusCode);

    }

    public static long getSourceURLSize(String ticket, String url, boolean isTrustedMode) throws XMLConvException {
        try {
            return Long.parseLong(getHeaderResponse(ticket, url, isTrustedMode).getFirstHeader("Content-Length").getValue() );
        } catch (Exception e) {
            LOGGER.error( "ERROR Retrieving Content Length for " + url , e );
            return -1;
        }
    }

    public static String getSourceUrlWithTicket(String ticket, String sourceUrl, boolean isTrustedMode) throws URISyntaxException {
        CustomURI uri = new CustomURI(sourceUrl);
        if (Utils.isNullStr(ticket) && isTrustedMode) {
            ticket = getHostCredentials(uri.getHost());
        }
        StringBuffer url = new StringBuffer();
        if (isTrustedMode && !Utils.isNullStr(ticket)) {
                url.append("http://localhost:8080");
                url.append(Constants.GETSOURCE_URL);
                url.append("?");
                url.append(Constants.TICKET_PARAM);
                url.append("=");
                url.append(ticket);
                url.append("&");
                url.append(Constants.SOURCE_URL_PARAM);
                url.append("=");
                url.append(sourceUrl);
        } else {
            url.append(sourceUrl);
        }
        return url.toString();
    }

    /**
     * Opens stream to file url.
     * @param srcUrl File Url
     * @param ticket Authorization ticket
     * @param isTrustedMode Request is from a trusted source.
     * @return File input stream
     * @throws IOException When an IO error occurs.
     * @throws URISyntaxException When the URL provided isn't a valid URI.
     * TODO: check if possible to remove and use getFileInputStream.
     */
    public InputStream getInputStream(String srcUrl, String ticket, boolean isTrustedMode) throws IOException, URISyntaxException {
        CustomURI customURL = new CustomURI(srcUrl);
        URL url = customURL.getURL();
        URLConnection uc = url.openConnection();

        if (ticket == null && isTrustedMode) {
            ticket = getHostCredentials(customURL.getHost());
        }
        uc.addRequestProperty("Accept", "*/*");

        if (ticket != null) {
            // String auth = Utils.getEncodedAuthentication(user,pwd);
            uc.addRequestProperty("Authorization", " Basic " + ticket);
        }
        LOGGER.info("Opened stream to file: " + url.toString());
        return uc.getInputStream();
    }

    /**
     * Opens Stream to file URL
     * @param url File url
     * @param ticket Authorization ticket
     * @param isTrustedMode Request is from a trusted source.
     * @return File input stream
     * @throws IOException When an IO error occurs.
     * @throws URISyntaxException When the URL provided isn't a valid URI.
     */
    public InputStream getFileInputStream(String url, String ticket, boolean isTrustedMode) throws IOException, URISyntaxException {
        CustomURI customURL = new CustomURI(url);
        if (ticket == null && isTrustedMode) {
            ticket = getHostCredentials(customURL.getHost());
        }
        HttpEntity entity = getFileEntity(url, ticket);
        if (entity != null) {
            return entity.getContent();
        }
        return null;
    }

    /**
     * Returns response entity that should include the requested file content.
     * @param url File url
     * @param ticket Authorization ticket
     * @return Entity
     * @throws IOException When an IO error occurs.
     * @throws URISyntaxException When the URL provided isn't a valid URI.
     */
    private HttpEntity getFileEntity(String url, String ticket) throws IOException, URISyntaxException {
        if (StringUtils.contains(url, Constants.SOURCE_URL_PARAM)) {
            LOGGER.error("File proxy URL detected: " + url);
            throw new URISyntaxException(url, "File proxy URL detected, aborting download");
        }
        LOGGER.info("Start to download file: " + url);
        HttpCacheContext context = HttpCacheContext.create();
        HttpGet httpget = new HttpGet(url);
        if (ticket != null) {
            httpget.addHeader(HttpHeaders.AUTHORIZATION, " Basic " + ticket);
        }
        httpget.addHeader(HttpHeaders.ACCEPT, "*/*");
        httpget.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
        response = client.execute(httpget, context);

        CacheResponseStatus responseStatus = context.getCacheResponseStatus();
        switch (responseStatus) {
            case VALIDATED:
                LOGGER.info("The response was generated from the cache after validating the entry with the origin server.");
                break;
            case CACHE_MISS:
                LOGGER.info("Entry not found in cache.");
                break;
            default:
                LOGGER.info("Response from cache: " + responseStatus);
        }
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpServletResponse.SC_OK) {
            return response.getEntity();
        } else {
            throw new IOException("The file could not be retrieved");
        }
    }

    public void closeQuietly() {
        try {
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            LOGGER.error("Could not close resource: " + e);
        }
    }

    private static String getHostCredentials(String host) {
        try {
            IHostDao hostDao = GDEMServices.getDaoService().getHostDao();
            Vector v = hostDao.getHosts(host);

            if (v != null && v.size() > 0) {
                Hashtable h = (Hashtable) v.get(0);
                String user = (String) h.get("user_name");
                String pwd = (String) h.get("pwd");
                return Utils.getEncodedAuthentication(user, pwd);
            }

        } catch (Exception e) {
            LOGGER.error("Error getting host data from the DB " + e.toString());
            LOGGER.error("Conversion proceeded");
        }
        return null;
    }

}
