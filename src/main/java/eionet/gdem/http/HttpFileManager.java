package eionet.gdem.http;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.cache.CacheManagerUtil;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IHostDao;
import eionet.gdem.utils.Utils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.client.cache.ehcache.EhcacheHttpCacheStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author George Sofianos
 */
public class HttpFileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFileManager.class);

    public void getHttpResponse(HttpServletResponse response, String ticket, String url) throws IOException, URISyntaxException {
        HttpEntity entity = downloadFile(url, ticket, false);
        entity.writeTo(response.getOutputStream());
    }

    public static String getSourceUrlWithTicket(String ticket, String sourceUrl, boolean isTrustedMode) {
        StringBuffer url = new StringBuffer();
        if (isTrustedMode && !Utils.isNullStr(ticket)) {
                url.append(Properties.gdemURL);
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

    public InputStream getInputStream(String url, String ticket, boolean isTrustedMode) throws IOException, URISyntaxException {
        HttpEntity entity = downloadFile(url, ticket, isTrustedMode);
        if (entity != null) {
            return entity.getContent();
        } else
            return null;
    }

    private HttpEntity downloadFile(String url, String ticket, boolean isTrustedMode) throws IOException, URISyntaxException {
        LOGGER.info("Start to download file: " + url);
        CustomURL file = new CustomURL(url);
        EhcacheHttpCacheStorage ehcacheHttpCacheStorage = new EhcacheHttpCacheStorage(CacheManagerUtil.getHttpCache());
        CacheConfig cacheConfig = CacheConfig.custom()
                .setSharedCache(false)
                .setMaxCacheEntries(1000)
                .setMaxObjectSize(524288000)
                .build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(30000)
                .setConnectTimeout(30000)
                .build();
        CloseableHttpClient client = CachingHttpClients.custom()
                .setCacheConfig(cacheConfig)
                .setHttpCacheStorage(ehcacheHttpCacheStorage)
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpCacheContext context = HttpCacheContext.create();
        HttpGet httpget = new HttpGet(url);
        if (ticket != null) {
            httpget.addHeader("Authorization", " Basic " + ticket);
        } else if (ticket == null && isTrustedMode) {
            ticket = getHostCredentials(file.getHost());
            httpget.addHeader("Authorization", " Basic " + ticket);
        }
        CloseableHttpResponse response = client.execute(httpget, context);

        CacheResponseStatus responseStatus = context.getCacheResponseStatus();
        HttpEntity entity = response.getEntity();
        //entity.writeTo(System.out);

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

        // log response header
        StringBuilder logBuilder = new StringBuilder("Response header properties: ");
        logBuilder.append(contentType != null ? "Content-Type=" + contentType + "; " : "");
        logBuilder.append("Content-Length=" + contentLength);
        logBuilder.append(contentEncoding != null ? "Content-Encoding=" + contentEncoding + "; " : "");
        LOGGER.info(logBuilder.toString());

        // If content type is null, then fall back to most likely content type
        if (contentType == null || "text/xml".equals(contentType)) {
            contentType = "text/xml;charset=utf-8";
        }
        // set response properties
        //httpResponse.setContentType(contentType);
        //httpResponse.addHeader("Content-Length", Long.toString(contentLength));
        //httpResponse.setContentLength(contentLength);
        //httpResponse.setCharacterEncoding(contentEncoding);
        client.close();
        //entity.writeTo(httpResponse.getOutputStream());
        response.close();
        return entity;
    }

    private String getHostCredentials(String host) {
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
