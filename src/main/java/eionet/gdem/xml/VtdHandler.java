package eionet.gdem.xml;

import com.ximpleware.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author George Sofianos
 */
public class VtdHandler implements XmlHandler {

    @Override
    public boolean parseString(String xml) {
        return false;
    }

    @Override
    public boolean parseString(String xml, OutputStream out) {
        return false;
    }


    public void addWarningMessage(String xml, String warningMessage, OutputStream out) {
        try {
            VTDGen vg = new VTDGen();
            vg.setDoc(xml.getBytes());
            vg.parse(true);
            VTDNav vn = vg.getNav();
            AutoPilot ap = new AutoPilot(vn);
            XMLModifier xm = new XMLModifier(vn);
            ap.selectXPath("//div[@class = 'feedbacktext']");
            if (ap.evalXPath() != -1) {
                xm.insertAfterHead("<div class=\"error-msg\">" + warningMessage + "</div>");
            }
            xm.output(out);
            out.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (EntityException e) {
            e.printStackTrace();
        } catch (XPathParseException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NavException e) {
            e.printStackTrace();
        } catch (XPathEvalException e) {
            e.printStackTrace();
        } catch (ModifyException e) {
            e.printStackTrace();
        } catch (TranscodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
