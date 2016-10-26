package eionet.gdem.http;

import eionet.gdem.test.ApplicationTestContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class HttpCacheClientFactoryTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void test() {
        CloseableHttpClient client = HttpCacheClientFactory.getInstance();
        assertNotNull("Error while requesting client", client);
    }
}