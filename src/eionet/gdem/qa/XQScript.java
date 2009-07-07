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
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem.qa;
import java.io.OutputStream;

import eionet.gdem.Constants;
import eionet.gdem.GDEMException;
import eionet.gdem.qa.engines.SaxonImpl;
import eionet.gdem.qa.engines.XGawkQueryEngine;
import eionet.gdem.qa.engines.XslEngineImpl;


/**
 * Class for XQ script 
 * used by the workqueue XQTask and XQ sandbox
 */
public class XQScript {
	private String[] params; //parameter name + value pairs
	private String strResultFile;
	private String scriptSource; //XQuery script
	private String outputType; 	// html, txt, xml
	private String scriptType; // xquery, xsl, xgawk
	private String scriptFileName; // full path of script file
	private String srcFileUrl; 
	
	private boolean srcFileDownloaded; 

	public static final String SCRIPT_LANG_XQUERY ="xquery";
	public static final String SCRIPT_LANG_XSL ="xsl";
	public static final String SCRIPT_LANG_XGAWK ="xgawk";

	public static String[] SCRIPT_LANGS = {SCRIPT_LANG_XQUERY, SCRIPT_LANG_XSL, SCRIPT_LANG_XGAWK} ;
	//XQ Engine instance
	private XQEngineIF _engine;


	/**
	 * @param xqScript
	 * @param params XQ parameter name + value pairs in an array
	 * in format {name1=value1, name2=value2, ... , nameN=valueN}
	 * if no parameters, null should be passed
	 */
	public XQScript(String xqScript, String[] scriptParams)  {
		this(xqScript,scriptParams,XQEngineIF.DEFAULT_OUTPUTTYPE);
	}
	public XQScript(String xqScript, String[] scriptParams, String _outputType)  {
		scriptSource = xqScript;  
		params=scriptParams;
		outputType=_outputType;	  
		scriptType=SCRIPT_LANG_XQUERY;
	}

	/**
	 * Result of the XQsrcipt
	 */
	public String getResult() throws GDEMException {
		initEngine();
		return _engine.getResult(this);
	}
	public void getResult(OutputStream out) throws GDEMException {
		initEngine();
		_engine.getResult(this, out);
	}
	private void initEngine() throws GDEMException{

		if (_engine==null){
			try {
				if(XQScript.SCRIPT_LANG_XSL.equals(scriptType)){
					_engine= new XslEngineImpl();
				}
				else if(XQScript.SCRIPT_LANG_XGAWK.equals(scriptType)){
					_engine = new XGawkQueryEngine();
				}
				else{//default is xquery
					_engine = new SaxonImpl();    		
				}
			} catch (Exception e ) {
				throw new GDEMException("Error initializing engine  " +e.toString());
			}
		}
	}
	public String getOrigFileUrl() {
		if(srcFileUrl!=null && srcFileUrl.indexOf(Constants.GETSOURCE_URL)>-1 &&
				srcFileUrl.indexOf(Constants.SOURCE_URL_PARAM)>-1){
			
			return (srcFileUrl.substring(srcFileUrl.indexOf(Constants.SOURCE_URL_PARAM)+ Constants.SOURCE_URL_PARAM.length() + 1));
		}
		
			
		return srcFileUrl;
	}


	public void setResulFile(String fileName){
		strResultFile = fileName;
	}
	public String getStrResultFile() {
		return strResultFile;
	}
	public void setStrResultFile(String strResultFile) {
		this.strResultFile = strResultFile;
	}
	public String getScriptType() {
		return scriptType;
	}
	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}
	public String getSrcFileUrl() {
		return srcFileUrl;
	}
	public void setSrcFileUrl(String srcFileUrl) {
		this.srcFileUrl = srcFileUrl;
	}
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	public String getScriptSource() {
		return scriptSource;
	}
	public void setScriptSource(String scriptSource) {
		this.scriptSource = scriptSource;
	}
	public String getOutputType() {
		return outputType;
	}
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
	public String getScriptFileName() {
		return scriptFileName;
	}
	public void setScriptFileName(String scriptFileName) {
		this.scriptFileName = scriptFileName;
	}
	public boolean isSrcFileDownloaded() {
		return srcFileDownloaded;
	}
	public void setSrcFileDownloaded(boolean srcFileDownloaded) {
		this.srcFileDownloaded = srcFileDownloaded;
	}
}