/*
 * Created on 25.05.2007
 */
package eionet.gdem.dcm.remote;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * XML Result strategy.
 */
public abstract class XMLResultStrategy {

    private int status = HttpServletResponse.SC_OK;

    private static final String ROOT_TAG = "response";
    private static final String RESPONSE_CODE_ATTR = "code";

    private ByteArrayOutputStream outputStreamBuffer = null;

    protected TransformerHandler hd = null;

    /**
     * Default constructor
     */
    public XMLResultStrategy() {
    }

    /**
     * Writes elements.
     * @throws Exception If an error occurs.
     */
    protected abstract void writeElements() throws Exception;

    /**
     * The abstract class builds XML result for servlet request Override writeElements() method for writing xml tags
     *
     * @throws Exception
     */

    /**
     * Write the result into temporary outputstream
     * @throws Exception If an error occurs.
     */
    public void writeXML() throws Exception {
        outputStreamBuffer = new ByteArrayOutputStream();
        writeXML(new StreamResult(outputStreamBuffer));
    }

    /**
     * Writes the date into given outputstream
     *
     * @param out
     *            eg ServletOutputStream
     * @throws Exception If an error occurs.
     */
    public void writeXML(OutputStream out) throws Exception {
        if (outputStreamBuffer == null)
            // write the data into given outputstream
            writeXML(new StreamResult(out));
        else
            // outputStream is already filled, now pull the tmp outputstream to actual
            outputStreamBuffer.writeTo(out);

    }

    /**
     * Build the XML and write it to the given result
     *
     * @param streamResult Stream result
     * @throws Exception If an error occurs.
     * XXX: Replace XALAN when possible.
     */
    public void writeXML(StreamResult streamResult) throws Exception {
        SAXTransformerFactory tf = new org.apache.xalan.processor.TransformerFactoryImpl();
        // SAX2.0 ContentHandler.
        hd = tf.newTransformerHandler();
        Transformer serializer = hd.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, getEncoding());
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "", RESPONSE_CODE_ATTR, "int", String.valueOf(getStatus()));

        hd.setResult(streamResult);
        hd.startDocument();
        hd.startElement("", "", ROOT_TAG, attrs);
        writeElements();
        hd.endElement("", "", ROOT_TAG);
        hd.endDocument();
    }

    /**
     * Create XML element without attributes
     *
     * @param tag_name
     *            element name
     * @param tag_value
     *            element value
     * @throws SAXException If an error occurs.
     */
    protected void writeSimpleElement(String tag_name, String tag_value) throws SAXException {
        writeSimpleElement(tag_name, tag_value, null);
    }

    /**
     * Create XML element with attributes
     *
     * @param tag_name
     *            element name
     * @param tag_value
     *            element value
     * @param attrs
     *            AttributesImpl
     * @throws SAXException If an error occurs.
     */
    protected void writeSimpleElement(String tag_name, String tag_value, AttributesImpl attrs) throws SAXException {

        hd.startElement("", "", tag_name, attrs);
        hd.characters(tag_value.toCharArray(), 0, tag_value.length());
        hd.endElement("", "", tag_name);
    }

    /**
     * get XML encoding
     *
     * @return
     */
    public String getEncoding() {
        return "UTF-8";
    }

    /**
     * get XML content type
     *
     * @return
     */
    public String getContentType() {
        return "text/xml";
    }

    /**
     * Get HTTP status code
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get ouput content length, if available
     *
     * @return
     */
    public int getContentLength() {
        if (outputStreamBuffer != null)
            return outputStreamBuffer.size();
        else
            return 0;
    }

}
