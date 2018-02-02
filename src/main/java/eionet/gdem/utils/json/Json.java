package eionet.gdem.utils.json;

import java.io.IOException;
import eionet.gdem.utils.xml.dom.DomContext;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import eionet.gdem.utils.xml.XmlException;

/**
 * The class implements some static methods for converting JSON contents to XML format.
 *
 * The methods can be used in XQuery scripts. Eg.:
 * <pre>
 *     declare namespace xmlconv-ext="java:eionet.gdem.qa.functions.Json";
 *     let $jsonResultXmlDoc := xmlconv-ext:jsonRequest2xml($URL)
 * </pre>
 *
 * @author Enriko Käsper
 */
public class Json {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(Json.class);

    /**
     * Method converts the URL response into XML Document object. If the response is not in JSON format,
     * then JsonError object is converted to XML.
     * @param requestUrl Request URL to JSON format content.
     * @return Document object Returns the JSON or {@link JsonError} object in XML format.
     * @throws XmlException If an error occurs.
     */
    public static Document jsonRequest2xml(String requestUrl) throws XmlException {
        // XXX: This might consume a lot of resources.
        String xml = jsonRequest2xmlString(requestUrl);
        DomContext domContext = new DomContext();
        domContext.checkFromString(xml);
        return domContext.getDocument();
    }

    /**
     * Method converts the URL response body into XML format and returns it as String. If the response is not in JSON format, then JsonError object is converted to XML.
     * @param requestUrl Request URL to JSON format content.
     * @return String of XML
     */
    public static String jsonRequest2xmlString(String requestUrl) {
        JsonError error = null;
        String responseString = null;
        String xml = null;
        HttpGet method = null;

        // Create an instance of HttpClient.
        CloseableHttpClient client = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler(3, false)).build();
        CloseableHttpResponse response = null;
        try {
            // Create a method instance.
            method = new HttpGet(requestUrl);

            // Provide custom retry handler is necessary
            //method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
            // Execute the method.
            response = client.execute(method);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.error("Method failed: " + response.getStatusLine());
                error = new JsonError(statusCode, response.getStatusLine().getReasonPhrase());
            } else {
                // Read the response body.
                HttpEntity entity = response.getEntity();
                byte[] responseBody = IOUtils.toByteArray(entity.getContent());
                responseString = new String(responseBody, "UTF-8");
            }
        /*} catch (HttpException e) {
            LOGGER.error("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
            error = new JsonError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Fatal protocol violation.");*/
        } catch (IOException e) {
            LOGGER.error("Fatal transport error: " + e.getMessage());
            error = new JsonError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Fatal transport error.");
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.error("Error: " + e.getMessage());
            e.printStackTrace();
            error = new JsonError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error." + e.getMessage());
        } finally {
            // Release the connection.
            if (method != null) {
                method.releaseConnection();
            }
        }
        if (responseString != null) {
            xml = jsonString2xml(responseString);
        } else if (error != null) {
            xml = jsonString2xml(error);
        } else {
            xml = jsonString2xml(new JsonError());
        }
        return xml;
    }

    /**
     * Method converts the given JSON format String or any other POJO into XML. The return type is String.
     * @param jsonObject JSON format String or any other Java Object (POJO)
     * @return String of XML
     */
    public static String jsonString2xml(Object jsonObject) {
        String xml = null;
        try {
            JSON json = JSONSerializer.toJSON(jsonObject);
            XMLSerializer xmlSerializer = new XMLSerializer();
            xmlSerializer.setRootName("root");
            xmlSerializer.setElementName("element");
            xmlSerializer.setTypeHintsEnabled(false);
            xml = xmlSerializer.write(json);
        } catch (Exception e) {
            LOGGER.error("Unable to serialise JSON object to XML: " + e.getMessage());
            if (!(jsonObject instanceof JsonError)) {
                JsonError errorObject = new JsonError("Unable to serialise JSON object to XML: " + e.getMessage());
                xml = jsonString2xml(errorObject);
            }
        }
        return xml;
    }
}
