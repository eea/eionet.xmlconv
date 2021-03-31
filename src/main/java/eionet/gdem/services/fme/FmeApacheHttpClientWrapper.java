package eionet.gdem.services.fme;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;


@Service("fmeApacheHttpClient")
public class FmeApacheHttpClientWrapper implements ApacheHttpClientWrapper {

    private static CloseableHttpClient client;

    @Override
    public CloseableHttpClient getClient() {
        if (client == null) {
            synchronized (FmeApacheHttpClientWrapper.class) {
                if (client == null) {
                    client = HttpClients.createDefault();
                }
            }
        }
        return client;
    }
}
