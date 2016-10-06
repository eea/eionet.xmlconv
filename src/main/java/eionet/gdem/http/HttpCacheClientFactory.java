package eionet.gdem.http;

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
                    .setMaxCacheEntries(1000)
                    .setMaxObjectSize(524288000)
                    .build();
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(30000)
                    .setConnectTimeout(30000)
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
