/*
 * Created on 12.02.2008
 */
package eionet.gdem.conversion;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import eionet.gdem.GDEMException;
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
     * @throws GDEMException
     */
    Vector listConversions() throws GDEMException;

    /**
     * List available conversions for given schema. If schema is not given as a parameter, then all possible conversions will be
     * returned.
     *
     * @param schema
     *            XML Schema URL for which conversions will be returned.
     * @return List of conversions.
     * @throws GDEMException
     */
    Vector listConversions(String schema) throws GDEMException;

    /**
     * Converts the XML file to a specific format.
     *
     * @param sourceURL
     *            URL of the XML file to be converted
     * @param convertId
     *            ID of desired conversion as the follows: - If conversion ID begins with the DD DCM will generate appropriate
     *            stylesheet on the fly. - If conversion ID is number the DCM will consider consider hand coded conversion
     * @return Hashtable containing two elements: - content-type (String) - content (Byte array)
     * @throws GDEMException
     *             Thrown in case of errors
     */
    Hashtable convert(String sourceURL, String convertId) throws GDEMException;

    /**
     * Converts DataDictionary MS Excel file to XML
     *
     * @param String
     *            url: URL of the srouce Excel file
     * @return ConversionResultDto result object
     */
    Hashtable<String, Object> convertDD_XML(String sourceURL) throws GDEMException;

    /**
     * Converts DataDictionary MS Excel sheets to different XML files, where one xml file is dataset table.
     *
     * @param String
     *            url: URL of the srouce Excel file sheetParam: Sheetname to convert
     * @return ConversionResultDto result object
     */
    Hashtable<String, Object> convertDD_XML_split(String sourceURL, String sheetParam) throws GDEMException;

    /**
     * If Conversion Service is called through HTTP, then set the HTTP Response object
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
     * @return returns the Hashtable that has exactly the same structure as convert method.
     * @throws GDEMException
     */
    Hashtable convertPush(InputStream file, String convertId, String fileName) throws GDEMException;

    /**
     * Method for xml-rpc clients file is base64 encoded bytearray
     *
     * @param file
     *            base64 encoded bytearray
     * @param convertId
     *            Stylesheet ID
     * @param fileName
     * @return returns the Hashtable that has exactly the same structure as convert method.
     * @throws GDEMException
     */
    Hashtable convertPush(byte[] file, String convertId, String filename) throws GDEMException;

    /**
     * Set base64 authentication info for receiveing remote URLs
     *
     * @param _ticket
     */
    @Override
    void setTicket(String _ticket);

    /**
     * Get a distinct list of XML Schemas returned from listConversions() method
     *
     * @return
     * @throws GDEMException
     */
    List getXMLSchemas() throws GDEMException;

    /**
     * Converts excel file to a set of XML files by XSL teplates specified in xmlconv
     *
     * @param file
     *            excel file as an byte array
     * @param fileName
     *            excel file name
     * @return a vector of objects (strings) where the first element is status code, second is status description. Then goes a
     *         sequence of pairs: file name, xml data as a string
     * @throws GDEMException
     *             if some unpredictable error occurs.
     */
    Vector<Object> convertExcelToXMLPush(byte[] file, String fileName) throws GDEMException;

    /**
     * Converts excel file to a set of XML files by XSL teplates specified in xmlconv
     *
     * @param fileUrl
     *            the URL of the file.
     * @return a vector of objects (strings) where the first element is status code, second is status description. Then goes a
     *         sequence of pairs: file name, xml data as a string
     * @throws GDEMException
     *             if some unpredictable error occurs.
     */
    Vector<Object> convertExcelToXML(String fileUrl) throws GDEMException;

    /**
     * /** Converts DataDictionary MS Excel sheets to XML files.
     *
     * @param sourceURL
     *            URL of the srouce Excel file
     * @param split
     *            if true, then split the sheets into different files
     * @param sheetName
     *            Sheet name to convert
     * @return ConversionResultDto
     * @throws GDEMException
     */
    ConversionResultDto convertDD_XML(String sourceURL, boolean split, String sheetName) throws GDEMException;
}
