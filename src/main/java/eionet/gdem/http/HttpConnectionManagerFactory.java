package eionet.gdem.http;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 *
 * @author George Sofianos
 */

public final class HttpConnectionManagerFactory {

    private HttpConnectionManagerFactory() {
        // do nothing
    }

    private static PoolingHttpClientConnectionManager manager;

    public static PoolingHttpClientConnectionManager getInstance() {
        if (manager == null) {
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(50);
            cm.setDefaultMaxPerRoute(50);
            manager = cm;
        }
        return manager;
    }

}
