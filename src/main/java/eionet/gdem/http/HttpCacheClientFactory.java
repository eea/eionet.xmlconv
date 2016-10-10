package eionet.gdem.http;

import eionet.gdem.Properties;
import eionet.gdem.cache.CacheManagerUtil;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.client.cache.ehcache.EhcacheHttpCacheStorage;

/**
 *
 * @author George Sofianos
 */
public final class HttpCacheClientFactory {

    private HttpCacheClientFactory() {
        // do nothing
    }
    private static CloseableHttpClient client;

    public static CloseableHttpClient getInstance() {
        if (client == null) {
            EhcacheHttpCacheStorage ehcacheHttpCacheStorage = new EhcacheHttpCacheStorage(CacheManagerUtil.getHttpCache());
            CacheConfig cacheConfig = CacheConfig.custom()
                    .setSharedCache(false)
                    .setMaxCacheEntries(Properties.HTTP_CACHE_ENTRIES)
                    .setMaxObjectSize(Properties.HTTP_CACHE_OBJECTSIZE)
                    .build();
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(Properties.HTTP_SOCKET_TIMEOUT)
                    .setConnectTimeout(Properties.HTTP_CONNECT_TIMEOUT)
                    .build();
            client = CachingHttpClients.custom()
                    .setCacheConfig(cacheConfig)
                    .setHttpCacheStorage(ehcacheHttpCacheStorage)
                    .setDefaultRequestConfig(requestConfig)
                    .setConnectionManager(HttpConnectionManagerFactory.getInstance())
                    .build();
        }
        return client;
    }

}
