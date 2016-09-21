package eionet.gdem.xml;

import eionet.gdem.XMLConvException;

import java.io.OutputStream;

/**
 * Interface for XML parsers / writers.
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

    /**
     * Adds warning message to feedbacktext
     * @param xml Input XML
     * @param warningMessage Warning message
     * @param out Output Stream
     * @throws XMLConvException In case of a parser error
     */
    void addWarningMessage(String xml, String warningMessage, OutputStream out) throws XMLConvException;
}
