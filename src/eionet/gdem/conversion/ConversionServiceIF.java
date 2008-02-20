/*
 * Created on 12.02.2008
 */
package eionet.gdem.conversion;

import java.util.Hashtable;
import java.util.Vector;

import eionet.gdem.dcm.results.HttpResultWrapper;

import eionet.gdem.GDEMException;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ConversionServiceIF
 */

public interface ConversionServiceIF {

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
	 * Request from XML/RPC client Converts DataDictionary MS Excel file to XML
	 *
	 * @param String
	 *            url: URL of the srouce Excel file
	 * @return Vector result: error_code, xml_url, error_message
	 */
	public Vector convertDD_XML(String sourceURL) throws GDEMException;

	/**
	 * Request from XML/RPC client Converts DataDictionary MS Excel sheets to
	 * different XML files, where one xml file is dataset table.
	 *
	 * @param String
	 *            url: URL of the srouce Excel file
	 * @return Vector result: error_code, xml_url, error_message
	 */
	public Vector convertDD_XML_split(String sourceURL, String sheet_param)
			throws GDEMException;

	
	public void setHTTPResult(HttpResultWrapper httpResult);
	
	public boolean isHTTPRequest();
	
	public Hashtable convertPush(String fileName, String convertId)throws GDEMException;
	
	public Hashtable convertPush(byte[] fileBase64, String convertId, String fileName)throws GDEMException;
}