package eionet.gdem.qa.engines;

import eionet.gdem.test.ApplicationTestContext;
import org.basex.BaseXServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class BasexImplTest {

    private BaseXServer server = null;
    private BaseXClient client = null;
    @Before
    public void setUp() throws Exception {
        server = new BaseXServer();
        client = new BaseXClient("localhost", 1984, "admin", "admin");
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        server = null;
    }

    @Test
    public void testSimpleQuery() throws Exception {
        String result = client.query("1").execute();
        Assert.assertEquals("1", result);
    }

    @Test
    public void testBaseXClient() throws IOException {
        String result = client.execute("INFO");
        Assert.assertNotNull("test", result);
    }
}