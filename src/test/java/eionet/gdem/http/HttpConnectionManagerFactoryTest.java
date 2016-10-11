package eionet.gdem.http;

import eionet.gdem.Properties;
import eionet.gdem.test.ApplicationTestContext;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 *
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class HttpConnectionManagerFactoryTest {

    private PoolingHttpClientConnectionManager manager;

    @Test
    public void test() {
        manager = HttpConnectionManagerFactory.getInstance();
        assertEquals("Wrong total connections", Properties.HTTP_MANAGER_TOTAL, manager.getMaxTotal());
        assertEquals("Wrong default route connections", Properties.HTTP_MANAGER_ROUTE, manager.getDefaultMaxPerRoute());
    }

}