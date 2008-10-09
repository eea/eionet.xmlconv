/*
 * Created on 12.02.2008
 */
package eionet.gdem.conversion;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import eionet.gdem.dcm.results.HttpMethodResponseWrapper;
import eionet.gdem.dcm.results.RemoteServiceIF;

import eionet.gdem.GDEMException;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ConversionServiceIF
 */

public interface ConversionServiceIF extends RemoteServiceIF{

	/**
	 * List all possible conversions
	 */

	public Vector listConversions() throws GDEMException;

	/**
	 * List all possible conversions for this namespace
	 */
	public Vector listConversions(String schema) throws GDEMException;

	/**
	 * Converts the XML file to a specific format.
	 *
	 * @param sourceURL
	 *            URL of the XML file to be converted
	 * @param convertId
	 *            ID of desired conversion as the follows: - If conversion ID
	 *            begins with the DD DCM will generate appropriate stylesheet on
	 *            the fly. - If conversion ID is number the DCM will consider
	 *            consider hand coded conversion
	 * @return Hashtable containing two elements: - content-type (String) -
	 *         content (Byte array)
	 * @throws GDEMException
	 *             Thrown in case of errors
	 */
	public Hashtable convert(String sourceURL, String convertId)
			throws GDEMException;

	/**
	 * Converts DataDictionary MS Excel file to XML
	 *
	 * @param String
	 *            url: URL of the srouce Excel file
	 * @return Vector result: error_code, xml_url, error_message
	 */
	public Vector convertDD_XML(String sourceURL) throws GDEMException;

	/**
	 * Converts DataDictionary MS Excel sheets to
	 * different XML files, where one xml file is dataset table.
	 *
	 * @param String
	 *            url: URL of the srouce Excel file
	 * @return Vector result: error_code, xml_url, error_message
	 */
	public Vector convertDD_XML_split(String sourceURL, String sheet_param)
			throws GDEMException;

	/**
	 * If Conversion Service is called through HTTP, then set the HTTP Response object
	 * @param httpResponse
	 */
	public void setHttpResponse(HttpMethodResponseWrapper httpResponse);
	
	/**
	 * Convert an XML file with given XSL. The file is stored into file system by HTTP action already.
	 * 
	 * @param file			file content as InputStream
	 * @param convertId		Stylesheet ID.
	 * @return				returns the Hashtable that has exactly the same structure as convert method.
	 * @throws GDEMException
	 */
	public Hashtable convertPush(InputStream file, String convertId, String fileName)throws GDEMException;
	
	/**
	 * Method for xml-rpc clients file is base64 encoded bytearray
	 * @param file	base64 encoded bytearray
	 * @param convertId		Stylesheet ID
	 * @param fileName
	 * @return				returns the Hashtable that has exactly the same structure as convert method.
	 * @throws GDEMException
	 */
	public Hashtable convertPush(byte[] file, String convertId, String filename)throws GDEMException;

	/**
	 * Set base64 authentication info for receiveing remote URLs
	 * @param _ticket
	 */
	public void setTicket(String _ticket);

	/**
	 * Get a distinct list of XML Schemas returned from listConversions() method 
	 * @return
	 * @throws GDEMException
	 */
	public List getXMLSchemas() throws GDEMException ;
}