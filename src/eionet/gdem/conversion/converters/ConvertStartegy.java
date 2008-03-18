/*
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
 * The Original Code is Web Dashboards Service
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Nedeljko Pavlovic (ED)
 *    							 Alfeldi Istvan (ED) 
 */

package eionet.gdem.conversion.converters;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Driver;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

public abstract class ConvertStartegy {
	
	private HashMap xslParams = null;
	private static LoggerIF _logger = GDEMServices.getLogger();
	public String xslFolder = Properties.xslFolder+ File.separatorChar; //props.getString("xsl.folder");
	public String tmpFolder = Properties.tmpFolder+ File.separatorChar; //props.getString("tmp.folder");
	public static final String XML_FOLDER_URI_PARAM="xml_folder_uri"; 
	public static final String DD_DOMAIN_PARAM="dd_domain"; 
	
	public abstract String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws GDEMException, Exception;
	
	/*
	 *  sets the map of xsl global paramters for this strategy 
	 */
	public void setXslParams(HashMap map){
		this.xslParams = map;
	}
	
	protected void runXalanTransformation(InputStream in, InputStream xslStream, OutputStream out) throws GDEMException {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			TransformerErrorListener errors = new TransformerErrorListener();
			tFactory.setErrorListener(errors);

			Transformer transformer = tFactory.newTransformer(new StreamSource(xslStream));
			transformer.setErrorListener(errors);

			transformer.setParameter(DD_DOMAIN_PARAM,Properties.ddURL);
			setTransformerParameters(transformer);		
            long l = 0L;
            if(_logger.enable(LoggerIF.DEBUG))
                l = System.currentTimeMillis();
			transformer.transform(new StreamSource(in), new StreamResult(out));
            if(_logger.enable(LoggerIF.DEBUG))
            	_logger.debug((new StringBuilder()).append("generate: transformation needed ").append(System.currentTimeMillis() - l).append(" ms").toString());
			//System.out.println((new StringBuilder()).append("generate: transformation needed ").append(System.currentTimeMillis() - l).append(" ms").toString());
		} catch (TransformerConfigurationException tce) {
			throw new GDEMException("Error transforming XML - incorrect stylesheet file: " + tce.toString(), tce);
		} catch (TransformerException tfe) {
			throw new GDEMException("Error transforming XML - it's not probably well-formed xml file: " + tfe.toString(), tfe);
		} catch (Throwable th) {
			_logger.error("Error " + th.toString(), th);
			th.printStackTrace(System.out);
			throw new GDEMException("Error transforming XML: " + th.toString());
		}
	}


	protected void runFOPTransformation(InputStream in, InputStream xsl, OutputStream out) throws GDEMException {
		try {
			Driver driver = new Driver();
			driver.setRenderer(Driver.RENDER_PDF);
			driver.setOutputStream(out);
			Result res = new SAXResult(driver.getContentHandler());
			Source src = new StreamSource(in);
			Source xsltSrc = new StreamSource(xsl);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			TransformerErrorListener errors = new TransformerErrorListener();

			transformerFactory.setErrorListener(errors);
			Transformer transformer = transformerFactory.newTransformer(xsltSrc);
			setTransformerParameters(transformer);
			transformer.setErrorListener(errors);

            long l = 0L;
            if(_logger.enable(LoggerIF.DEBUG))
                l = System.currentTimeMillis();
			transformer.transform(src, res);
            if(_logger.enable(LoggerIF.DEBUG))
            	_logger.debug((new StringBuilder()).append("generate: transformation needed ").append(System.currentTimeMillis() - l).append(" ms").toString());
            
		} catch (TransformerConfigurationException tce) {
			throw new GDEMException("Error transforming XML to PDF - incorrect stylesheet file: " + tce.toString(), tce);
		} catch (TransformerException tfe) {
			throw new GDEMException("Error transforming XML to PDF - it's not probably well-formed xml file: " + tfe.toString(), tfe);
		} catch (Throwable e) {
			_logger.error("Error " + e.toString(), e);
			throw new GDEMException("Error transforming XML to PDF " + e.toString());
		}
	}
	/*
	 * sets the map of xsl global parameters to xsl transformer
	 */
	private void setTransformerParameters(Transformer transformer){
		
		if(xslParams==null)return;

		Iterator keys = xslParams.keySet().iterator();
		while ( keys.hasNext()) {
			String key = (String)keys.next();
			String value = (String)xslParams.get(key);
			if(value!=null)
				transformer.setParameter(key,value);
		}

		//sets base URI for xmlfiles uploaded into xmlconv
	    String xmlFilePathURI = Utils.getURIfromPath(eionet.gdem.Properties.xmlfileFolderPath,true);
	    
	    if(xmlFilePathURI!=null){
			transformer.setParameter(XML_FOLDER_URI_PARAM,xmlFilePathURI);
	    }
		
	}


}