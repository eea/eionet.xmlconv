/*
 * Created on 27.03.2008
 */

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Sample java code for calling ConversionService getXMLSchemas method
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS ListConversions
 */

public class GetXMLSchemas {

    private final static String server_url = "http://80.235.29.171:8080/xmlconv/api";

    public static List<String> getXMLSchemas() {
        String method_path = "/getXMLSchemas";
        List<String> schemas = new ArrayList<String>();

        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            URL url = new URL(server_url.concat(method_path));
            InputStream stream = url.openStream();
            Document doc = docBuilder.parse(stream);

            // normalize text representation
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("schema");
            for (int s = 0; s < nodeList.getLength(); s++) {
                Node nodeSchema = nodeList.item(s).getFirstChild();
                String schema = (nodeSchema == null) ? "" : nodeSchema.getNodeValue();
                schemas.add(schema);
            }
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return schemas;
    }

    public static void main(String args[]) {
        List schemas = GetXMLSchemas.getXMLSchemas();
        System.out.println(schemas);
    }
}
