package eionet.gdem.utils.xml;

import com.ximpleware.*;

public class XMLUtils {

    /**
     * Returns a string after an XPATH expression evaluation.
     * @param xml Input XML
     * @param xpath Xpath Xpath expression
     * @return Result as string
     */
    public static String getXpathText(byte[] xml, String xpath) {
        VTDGen vg = new VTDGen();
        try {
        vg.setDoc(xml);
        vg.parse(true);
        VTDNav vn = vg.getNav();
        AutoPilot ap = new AutoPilot(vn);
        ap.selectXPath(xpath);
        return ap.evalXPathToString();
        } catch (XPathParseException e) {
            // do nothing
        } catch (EncodingException e) {
            // do nothing
        } catch (EOFException e) {
            // do nothing
        } catch (EntityException e) {
            // do nothing
        } catch (ParseException e) {
            // do nothing
        }
        return "";
    }
}
