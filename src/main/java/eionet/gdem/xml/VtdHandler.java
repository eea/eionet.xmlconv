package eionet.gdem.xml;

import com.ximpleware.*;
import eionet.gdem.GDEMException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Implements a handler for the VTD-XML parser. This is a fast an efficient XML parser
 * that can make small modifications to XML files like adding a warning message to the result HTML.
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

    @Override
    public void addWarningMessage(String xml, String warningMessage, OutputStream out) throws GDEMException {
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
            throw new GDEMException("Error: " + e.getMessage());
        } catch (EntityException e) {
            throw new GDEMException("Error: " + e.getMessage());
        } catch (XPathParseException e) {
            throw new GDEMException("Error: " + e.getMessage());
        } catch (ParseException e) {
            throw new GDEMException("Error: " + e.getMessage());
        } catch (NavException e) {
            throw new GDEMException("Error: " + e.getMessage());
        } catch (XPathEvalException e) {
            throw new GDEMException("Error: " + e.getMessage());
        } catch (ModifyException e) {
            throw new GDEMException("Error: " + e.getMessage());
        } catch (TranscodeException e) {
            throw new GDEMException("Error: " + e.getMessage());
        } catch (IOException e) {
            throw new GDEMException("Error: " + e.getMessage());
        }
    }
}
