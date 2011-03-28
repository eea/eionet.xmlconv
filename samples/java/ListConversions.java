/*
 * Created on 27.03.2008
 */

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/**
 * Sample java code for calling ConversionService listConversion method
 *
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ListConversions
 */

public class ListConversions {

    private final static String server_url = "http://80.235.29.171:8080/xmlconv/api";

    public static List<Map<String,String>> listConversions(String schema){
        String method_path = "/listConversions";
        List<Map<String,String>> conversions = new ArrayList<Map<String,String>>();

        try{

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            URL url = new URL(server_url.concat(method_path).concat("?schema=").concat(schema));
            InputStream stream = url.openStream();
            Document doc = docBuilder.parse(stream);

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            NodeList nodeList = doc.getElementsByTagName("conversion");
            for (int s = 0; s < nodeList.getLength(); s++) {
                Element elem = (Element)nodeList.item(s);
                Node elemConvertIt = elem.getElementsByTagName("convert_id").item(0).getFirstChild();
                String convert_id = (elemConvertIt==null) ? "":elemConvertIt.getNodeValue();
                Node elemResultType = elem.getElementsByTagName("result_type").item(0).getFirstChild();
                String result_type = (elemResultType==null) ? "":elemResultType.getNodeValue();
                Node elemDescription = elem.getElementsByTagName("description").item(0).getFirstChild();
                String description =(elemDescription==null) ? "":elemDescription.getNodeValue();
                Map<String,String> conversion = new HashMap<String,String>();
                conversion.put("convert_id",convert_id);
                conversion.put("result_type",result_type);
                conversion.put("description",description);
                conversions.add(conversion);
            }
        }catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line "
                 + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
        }catch (SAXException e) {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
        }catch (Throwable t) {
            t.printStackTrace ();
        }
        return conversions;
    }
    public static void main(String args[]) {
        String schema = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";
        List conversions = ListConversions.listConversions(schema);
        System.out.println (conversions);
    }
}
