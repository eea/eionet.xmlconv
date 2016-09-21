/*
 * Created on 12.02.2008
 */
package eionet.gdem.conversion;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dcm.remote.RemoteServiceIF;
import eionet.gdem.dto.ConversionResultDto;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ConversionServiceIF
 */

public interface ConversionServiceIF extends RemoteServiceIF {

    /**
     * List available conversions.
     *
     * @return List of conversions.
     * @throws XMLConvException If an error occurs
     */
    Vector listConversions() throws XMLConvException;

    /**
     * List available conversions for given schema. If schema is not given as a parameter, then all possible conversions will be
     * returned.
     *
     * @param schema
     *            XML Schema URL for which conversions will be returned.
     * @return List of conversions.
     * @throws XMLConvException If an error occurs
     */
    Vector listConversions(String schema) throws XMLConvException;

    /**
     * Converts the XML file to a specific format.
     *
     * @param sourceURL
     *            URL of the XML file to be converted
     * @param convertId
     *            ID of desired conversion as the follows: - If conversion ID begins with the DD DCM will generate appropriate
     *            stylesheet on the fly. - If conversion ID is number the DCM will consider consider hand coded conversion
     * @return Hashtable containing two elements: - content-type (String) - content (Byte array)
     * @throws XMLConvException
     *             Thrown in case of errors
     */
    Hashtable convert(String sourceURL, String convertId) throws XMLConvException;

    /**
     * Converts DataDictionary MS Excel file to XML
     *
     * @param sourceURL - URL of the source Excel file
     * @return ConversionResultDto result object
     * @throws XMLConvException If an error occurs
     */
    Hashtable<String, Object> convertDD_XML(String sourceURL) throws XMLConvException;

    /**
     * Converts DataDictionary MS Excel sheets to different XML files, where one xml file is dataset table.
     *
     * @param sourceURL URL of the source Excel file
     * @param sheetParam - Sheetname to convert
     * @return ConversionResultDto result object
     * @throws XMLConvException If an error occurs
     */
    Hashtable<String, Object> convertDD_XML_split(String sourceURL, String sheetParam) throws XMLConvException;

    /**
     * If Conversion Service is called through HTTP, then set the HTTP Response object.
     *
     * @param httpResponse
     */
    @Override
    void setHttpResponse(HttpMethodResponseWrapper httpResponse);

    /**
     * Convert an XML file with given XSL. The file is stored into file system by HTTP action already.
     *
     * @param file
     *            file content as InputStream
     * @param convertId
     *            Stylesheet ID.
     * @param fileName File name
     * @return returns the Hashtable that has exactly the same structure as convert method.
     * @throws XMLConvException If an error occurs
     */
    Hashtable convertPush(InputStream file, String convertId, String fileName) throws XMLConvException;

    /**
     * Method for xml-rpc clients file is base64 encoded bytearray.
     *
     * @param file
     *            base64 encoded bytearray
     * @param convertId
     *            Stylesheet ID
     * @param filename File name
     * @return returns the Hashtable that has exactly the same structure as convert method.
     * @throws XMLConvException If an error occurs
     */
    Hashtable convertPush(byte[] file, String convertId, String filename) throws XMLConvException;

    /**
     * Set base64 authentication info for receiveing remote URLs.
     *
     * @param _ticket Ticket
     */
    @Override
    void setTicket(String _ticket);

    /**
     * Get a distinct list of XML Schemas returned from listConversions() method.
     *
     * @return List of XML Schemas
     * @throws XMLConvException If an error occurs
     */
    List getXMLSchemas() throws XMLConvException;

    /**
     * /** Converts DataDictionary MS Excel sheets to XML files.
     *
     * @param sourceURL
     *            URL of the srouce Excel file
     * @param split
     *            if true, then split the sheets into different files
     * @param sheetName
     *            Sheet name to convert
     * @return ConversionResultDto Result transfer object
     * @throws XMLConvException If an error occurs
     */
    ConversionResultDto convertDD_XML(String sourceURL, boolean split, String sheetName) throws XMLConvException;
}
