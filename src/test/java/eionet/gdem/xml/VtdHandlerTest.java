package eionet.gdem.xml;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author George Sofianos
 */
public class VtdHandlerTest {
    @Test
    public void parseString() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VtdHandler a = new VtdHandler();
        a.parseString("<html><div class=\"feedbacktext\">test</div></html>", out);
        System.out.println(out);
    }

}