package eionet.gdem.qa.engines;

import eionet.gdem.GDEMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class SaxonImplTest {
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private PrintStream pr;
    @Before
    public void setUp() {
        pr = new PrintStream(baos);
    }
    @After
    public void tearDown() throws IOException {
        baos.close();
        pr.close();
    }
    @Test
    public void testSimpleQuery() throws Exception {
        XQScript xq = new XQScript("1 + 3", new String[]{});
        SaxonImpl sax = new SaxonImpl();
        sax.runQuery(xq, pr);
        Assert.assertEquals("4", baos.toString("UTF-8"));
    }

    @Test(expected = GDEMException.class)
    public void testException() throws Exception {
        XQScript xq = new XQScript("xquery version \"1.0\"; x || y", new String[]{});
        SaxonImpl sax = new SaxonImpl();
        sax.runQuery(xq, pr);
    }

}