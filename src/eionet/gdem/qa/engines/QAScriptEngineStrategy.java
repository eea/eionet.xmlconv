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
 * The Original Code is XMLCONV.
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 * 
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.qa.engines;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.batik.gvt.TextNode;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.qa.XQEngineIF;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.IXmlSerializer;
import eionet.gdem.utils.xml.XmlContext;
import eionet.gdem.utils.xml.XmlException;
import eionet.gdem.utils.xml.XmlSerialization;

/**
 * @author Enriko Käsper, Tieto Estonia
 * QAScriptEngineStrategy
 */

public abstract class QAScriptEngineStrategy  implements XQEngineIF{


	private static LoggerIF _logger = GDEMServices.getLogger();
	private String encoding = null;
	private String outputType = null;

	protected abstract void runQuery(XQScript script, OutputStream result) throws GDEMException;

	public void getResult(XQScript script,OutputStream out) throws GDEMException  {
		try {
			setOutputType(script.getOutputType());
			runQuery(script, out);

		} catch(Exception e) {
			throw new GDEMException(e.toString());
		}
	}
	
	public String getResult(XQScript script) throws GDEMException  {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		String res="";
		getResult(script, result);
		try{
			res = result.toString(DEFAULT_ENCODING);
			if (_logger.enable(_logger.DEBUG))
				_logger.debug("RESULT: \n" + res);
			
		} catch (Exception e) {
			_logger.error("==== CATCHED EXCEPTION " + e.toString() );
		}

		//add "red coloured warning" if script is expired
		if (script.getOutputType() .equals(script.SCRIPT_RESULTTYPE_HTML) && 
				script.getSchema() != null && script.getSchema().isExpired() ) {
			
			res=addExpWarning(res, Utils.getFormat( script.getSchema().getExpireDate() , "dd.MM.yyyy") );
		}
		
		return res;
	}

	public String getEncoding() {
		if(Utils.isNullStr(encoding))encoding=DEFAULT_ENCODING;

		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getOutputType() {
		if(Utils.isNullStr(outputType))outputType=DEFAULT_OUTPUTTYPE;
		return outputType;
	}
	public void setOutputType(String _outputType) {
		outputType= (_outputType==null) ? DEFAULT_OUTPUTTYPE : _outputType.trim().toLowerCase();
		outputType = (outputType.equals("txt"))?"text":outputType;

		if(outputType.equals("xml") || outputType.equals("html") ||
				outputType.equals("text") ||outputType.equals("xhtml"))
			this.outputType = outputType;
		else
			this.outputType = DEFAULT_OUTPUTTYPE;
	}
	public HashMap parseParams(String[] xqParams) throws GDEMException{
		HashMap<String,String> paramsMap = new HashMap<String,String>();

		if (xqParams!=null){
			for (int p=0; p<xqParams.length; p++) {
				String arg = xqParams[p];
				int eq = arg.indexOf("=");
				if (eq<1 || eq>=arg.length()-1) {
					throw new GDEMException("Bad param=value pair");
					//handleError("Bad param=value pair", true);
				}
				String argname = arg.substring(0,eq);
				paramsMap.put(argname, arg.substring(eq+1));
			}

		}
		return paramsMap;
	}
	
	private String addExpWarning(String htmlResult, String expDate) {

		try {
			IXmlCtx ctx=new XmlContext();
			ctx.checkFromString(  htmlResult);
			IXQuery xQuery=ctx.getQueryManager();
			
			NodeList divElements = ctx.getDocument().getElementsByTagName("div");
			boolean foundFeedbackDiv=parseDivNodes(divElements, expDate);
			
			//searching node is case insensitive in XPath - do it twice:
			if (!foundFeedbackDiv) {
				divElements = ctx.getDocument().getElementsByTagName("DIV");
				foundFeedbackDiv=parseDivNodes(divElements, expDate);
			}
			
			if (!foundFeedbackDiv) {
				return htmlResult;
			}
			else {
				IXmlSerializer serializer = new XmlSerialization(ctx);
				return serializer.serializeToString();
			}
		} catch (Exception e) {
			_logger.error("addExpWarning() Error parsing HTML, returning original HTML: " + e.toString());
		}
		
		return htmlResult;
	}
	
	private boolean parseDivNodes(NodeList divElements, String expDate) throws XmlException {
		boolean feedBackDivFound = false;
		try {
			for (int i = 0; divElements != null && i < divElements.getLength() ; i++) {
				Node divNode = divElements.item(i);
				Node classNode = divNode.getAttributes().getNamedItem("class");
				
				if (classNode != null && classNode.getNodeValue().equalsIgnoreCase("feedbacktext")) {
					//found feedback div
					feedBackDivFound = true;
					
					Node firstChild = divNode.getFirstChild();
					Document doc = firstChild.getOwnerDocument();
			
					Node warningNode = 
						  DocumentBuilderFactory.newInstance()
						    .newDocumentBuilder()
						      .parse( new InputSource(new StringReader("<span style='color:red; font-size:110%'>" +  
						    		  Properties.getMessage("label.stylesheet.warning.expired", new String[]{expDate}) +  "</span>"))).getFirstChild();
					warningNode = doc.importNode(warningNode, true);
					if (firstChild == null) {
						divNode.appendChild(warningNode);
					}
					else {
						warningNode = divNode.insertBefore(warningNode, firstChild);
					}
					//
					break;
				}
			}
		} catch (Exception e) {
			_logger.error("Error processing divNodes " + e);
		}
		return feedBackDivFound;
	}
}

