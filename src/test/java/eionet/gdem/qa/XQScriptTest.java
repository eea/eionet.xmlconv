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

}