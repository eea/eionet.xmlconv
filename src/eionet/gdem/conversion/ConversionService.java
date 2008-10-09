/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 * Contributors:   Nedeljko Pavlovic (ED)
 */

package eionet.gdem.conversion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import eionet.gdem.GDEMException;
import eionet.gdem.dcm.results.RemoteService;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

/**
 * Conversion Service Facade. 
 * The service is able to execute different conversions 
 * that are called through XML/RPC and HTTP POST and GET.
 *
 * @author Enriko Käsper
 */

public class ConversionService extends RemoteService 
				implements ConversionServiceIF{



	private static LoggerIF _logger = GDEMServices.getLogger();

	public ConversionService() {
	}
	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#listConversions()
	 */

	public Vector listConversions() throws GDEMException {
		return listConversions(null);
	}

	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#listConversions(java.lang.String)
	 */
	public Vector listConversions(String schema) throws GDEMException {

		ListConversionsMethod method = new ListConversionsMethod();
		Vector v = method.listConversions(schema);
	
		return v;
	}
	public Hashtable convert(String sourceURL, String convertId,
			String username, String password) throws GDEMException {
		
		try {
			setTicket(Utils.getEncodedAuthentication(username, password));
			setTrustedMode(false);
		
			ConvertXMLMethod convertMethod = new ConvertXMLMethod();
			setGlobalParameters(convertMethod);
			return convertMethod.convert(sourceURL, convertId);

		} catch (IOException ex) {
			_logger.error("Error creating ticket ", ex);
			throw new GDEMException("Error creating ticket", ex);
		}
	}

	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#convert(java.lang.String, java.lang.String)
	 */
	public Hashtable convert(String sourceURL, String convertId) throws GDEMException {
		
		if(!isHTTPRequest() && _logger.enable(LoggerIF.DEBUG))
			_logger.debug("ConversionService.convert method called through XML-rpc.");
		ConvertXMLMethod convertMethod = new ConvertXMLMethod();
		setGlobalParameters(convertMethod);
		return convertMethod.convert(sourceURL, convertId);	
	}

	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#convertDD_XML(java.lang.String)
	 */
	public Vector convertDD_XML(String sourceURL) throws GDEMException {
		
		if(!isHTTPRequest() && _logger.enable(LoggerIF.DEBUG))
			_logger.debug("ConversionService.convertDD_XML method called through XML-rpc.");

		ConvertDDXMLMethod convertDDXMLMethod = new ConvertDDXMLMethod();
		setGlobalParameters(convertDDXMLMethod);
		return convertDDXMLMethod.convertDD_XML(sourceURL);	
	}

	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#convertDD_XML_split(java.lang.String, java.lang.String)
	 */
	public Vector convertDD_XML_split(String sourceURL, String sheet_param) throws GDEMException {

		if(!isHTTPRequest() && _logger.enable(LoggerIF.DEBUG))
			_logger.debug("ConversionService.convertDD_XML_split method called through XML-rpc.");
		
		ConvertDDXMLMethod convertDDXMLMethod = new ConvertDDXMLMethod();
		setGlobalParameters(convertDDXMLMethod);
		return convertDDXMLMethod.convertDD_XML_split(sourceURL,sheet_param);	
	}

	public boolean existsXMLSchema(String xmlSchema) throws GDEMException {
		ListConversionsMethod method = new ListConversionsMethod();
		return method.existsXMLSchema(xmlSchema);
	}



	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#convertPush(byte[],java.lang.String,java.lang.String)
	 */
	public Hashtable convertPush(byte file[], String convertId, String filename)throws GDEMException {
		
		if(!isHTTPRequest() && _logger.enable(LoggerIF.DEBUG))
			_logger.debug("ConversionService.convertPush method called through XML-rpc.");

		InputStream input = null;
		
		try{
			input = new ByteArrayInputStream(file);
			ConvertXMLMethod convertMethod = new ConvertXMLMethod();
			setGlobalParameters(convertMethod);
			return convertMethod.convertPush(input, convertId, filename);	
		}
		finally{
			try{
				input.close();
			}
			catch(Exception e){}
			
		}
		
	}
	/* (non-Javadoc)
	 * @see eionet.gdem.conversion.ConversionServiceIF#convertPush(java.lang.String,java.lang.String)
	 */
	public Hashtable convertPush(InputStream fileInput, String convertId, String fileName) throws GDEMException {
		
		ConvertXMLMethod convertMethod = new ConvertXMLMethod();
		setGlobalParameters(convertMethod);
		return convertMethod.convertPush(fileInput, convertId, fileName);	
	}

	public Vector getXMLSchemas() throws GDEMException {
		ListConversionsMethod method = new ListConversionsMethod();
		return method.getXMLSchemas();
	}
}
