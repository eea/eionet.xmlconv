package eionet.gdem.utils.xml;

import com.ximpleware.*;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLUtils {


    /**
     * Returns a string after an XPATH expression evaluation.
     *
     * @param xml   Input XML
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
            String result = ap.evalXPathToString();
            return result;
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

    public static String removeEmptyElements(String xml) {
        String[] replaceEmptyElementsFromXmlRegex = new String[]{
                // This will remove empty elements that look like <ElementName/>
                "\\s*<\\w+/>",
                // This will remove empty elements that look like <ElementName></ElementName>
                "\\s*<\\w+></\\w+>",
                // This will remove empty elements that look like
                // <ElementName>
                // </ElementName>
                "\\s*<\\w+>\n*\\s*</\\w+>"
        };
        for (String pattern : replaceEmptyElementsFromXmlRegex) {
            Matcher matcher = Pattern.compile(pattern).matcher(xml);
            xml = matcher.replaceAll("");
        }
        return xml;
    }
}
