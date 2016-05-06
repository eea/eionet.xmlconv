package eionet.gdem.xml;

import java.io.OutputStream;

/**
 * @author George Sofianos
 */
public interface XmlHandler {

    /**
     * Parses XML to find any errors
     * @param xml XML Input
     * @return True if no errors found
     */
    boolean parseString(String xml);

    /**
     * Parses XML to find any errors
     * @param xml XML Input
     * @param out XML Output
     * @return True if no errors found
     */
    boolean parseString(String xml, OutputStream out);
}
