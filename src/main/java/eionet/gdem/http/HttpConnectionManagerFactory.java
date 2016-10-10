package eionet.gdem.http;

import eionet.gdem.Properties;
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
            cm.setMaxTotal(Properties.HTTP_MANAGER_TOTAL);
            cm.setDefaultMaxPerRoute(Properties.HTTP_MANAGER_ROUTE);
            manager = cm;
        }
        return manager;
    }

}
