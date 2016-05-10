package eionet.gdem.xml;

import com.ximpleware.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author George Sofianos
 */
public class VtdHandlerTest {

    @Test
    public void VtdModifierTest() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String xml = "<html><div class=\"feedbacktext\">test</div></html>";
        VTDGen vg = new VTDGen();
        vg.setDoc(xml.getBytes());
        vg.parse(true);
        VTDNav vn = vg.getNav();
        AutoPilot ap = new AutoPilot(vn);
        XMLModifier xm = new XMLModifier(vn);
        ap.selectXPath("//div[@class = 'feedbacktext']");
        if (ap.evalXPath() != -1) {
            xm.insertAfterHead("<div class=\"error-msg\">Test</div>");
        }
        xm.output(out);
        out.flush();
        assertEquals("Wrong result, XML parser error: ", "<html><div class=\"feedbacktext\"><div class=\"error-msg\">Test</div>test</div></html>", new String(out.toByteArray()));
    }

}