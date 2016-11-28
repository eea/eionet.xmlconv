package eionet.gdem.qa;

import eionet.gdem.XMLConvException;
import eionet.gdem.test.ApplicationTestContext;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;


/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class XQScriptTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testConstructor1() {
        new XQScript("1 + 3", new String[]{});
    }

    @Test
    public void testConstructor2() {
        new XQScript("1 + 3", new String[]{}, "html");
    }

    @Test
    public void testInvalidScript() throws XMLConvException {
        exception.expect(XMLConvException.class);
        XQScript xq = new XQScript("xs:date(12+12+12)", new String[]{});
        xq.getResult();
    }

    @Test
    public void testValidScript1() throws XMLConvException {
        XQScript xq = new XQScript("1 + 3", new String[]{});
        xq.getResult();
    }

    @Test
    public void testValidScript2() throws XMLConvException {
        OutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            XQScript xq = new XQScript("1 + 3", new String[]{}, "xml");
            xq.getResult(baos);
        } finally {
            IOUtils.closeQuietly(baos);
        }
    }
}