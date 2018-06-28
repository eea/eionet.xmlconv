package eionet.gdem.http;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Default HTTP Client implementation.
 */
public final class HttpDefaultClientFactory {

    private static volatile CloseableHttpClient client;

    /**
     * Private constructor to deal with reflection.
     */
    private HttpDefaultClientFactory() {
        throw new AssertionError();
    }

    /**
     * Get default Http Client (using system properties).
     * @return Default HTTP Client
     */
    public static CloseableHttpClient getInstance() {
        if (client == null) {
            synchronized (CloseableHttpClient.class) {
                if (client == null) {
                    client = HttpClients.createSystem();
                }
            }
        }
        return client;
    }
}
