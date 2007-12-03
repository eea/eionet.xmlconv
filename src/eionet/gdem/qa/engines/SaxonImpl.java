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

package eionet.gdem.qa.engines;

import java.util.Properties;

import java.io.Reader;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

import eionet.gdem.qa.XQEngineIF;
import eionet.gdem.Constants;
import eionet.gdem.GDEMException;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.Utils;

import net.sf.saxon.Configuration;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.DynamicQueryContext;
//import net.sf.saxon.query.QueryProcessor;
import net.sf.saxon.query.XQueryExpression;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;


public class SaxonImpl implements XQEngineIF {

  private LoggerIF _logger;

  public SaxonImpl() {
    _logger=GDEMServices.getLogger();
  }
  public String getResult(String xqScript, String[] params) throws GDEMException  {
    String res=null;
    try {
      res=runQuery(xqScript, params);

      if (_logger.enable(_logger.DEBUG))
        _logger.debug("RESULT: \n" + res);

     } catch(Exception e) {
        throw new GDEMException(e.toString());
    }

    return res;
  }
  public void getResult(String xqScript, String[] params, OutputStream out) throws GDEMException  {
    try {
      runQuery(xqScript, params, out);

     } catch(Exception e) {
        throw new GDEMException(e.toString());
    }
  }
  public String getResult(String xqScript) throws GDEMException  {
    return getResult(xqScript, null);
  }


//  public void setParameters(String[] params) {}


 /**
  * executes
  * code extracted from Saxon 7 source
  * and modified
  */
  private String runQuery(String script, String xqParams[]) throws GDEMException  {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    String s="";
   	runQuery(script,xqParams, result);
    try{
    	s = result.toString("UTF-8");
    	//result.close(); //??
    } catch (Exception e) {
    	_logger.debug("==== CATCHED EXCEPTION " + e.toString() );
    }
    return s;
  }
  private void runQuery(String script, String xqParams[], OutputStream result) throws GDEMException  {

    boolean wrap=false;
    //Source sourceInput = null;
    //StringBuffer err_buf = new StringBuffer();

    Configuration config = new Configuration();

    //our own extension of Saxon's error listener to send feedback to the user
    SaxonListener listener = new SaxonListener();
    config.setErrorListener(listener);

    //config.setRecoveryPolicy(Configuration.DO_NOT_RECOVER);

    config.setHostLanguage(config.XQUERY);
    StaticQueryContext staticEnv = new StaticQueryContext(config);
    //staticEnv.setConfiguration(config);
    DynamicQueryContext dynamicEnv = new DynamicQueryContext(config);

    SaxonListener dynamicListener = new SaxonListener();
    dynamicEnv.setErrorListener(dynamicListener);

    Properties outputProps = new Properties();
    outputProps.setProperty(OutputKeys.INDENT, "yes");

    //query script
    Reader queryReader = new StringReader(script);

//    staticEnv.setBaseURI(new File(script).toURI().toString());
    String xmlFilePathURI = Utils.getURIfromPath(eionet.gdem.Properties.xmlfileFolderPath);
    
    
    if(xmlFilePathURI!=null){
   		staticEnv.setBaseURI(xmlFilePathURI);
    }

    String s = "";

  try {
      //handle xq Parameters, extract from Saxon code
      if (xqParams!=null)
        for (int p=0; p<xqParams.length; p++) {
          String arg = xqParams[p];
          int eq = arg.indexOf("=");
          if (eq<1 || eq>=arg.length()-1) {
              throw new GDEMException("Bad param=value pair");
              //handleError("Bad param=value pair", true);
          }
          String argname = arg.substring(0,eq);
          if (argname.startsWith("!")) {
            // parameters starting with "!" are taken as output properties
            outputProps.setProperty(argname.substring(1), arg.substring(eq+1));
          }
          else if (argname.startsWith("+")) {
            // parameters starting with "+" are taken as inputdocuments
            //List sources = Transform.loadDocuments(arg.substring(eq+1), true, config);
            //dynamicEnv.setParameter(argname.substring(1), sources);
           }
           else
              dynamicEnv.setParameter(argname, new StringValue(arg.substring(eq+1)));

         }

      //QueryProcessor xquery = new QueryProcessor(config, staticEnv);

      //compile Query
      XQueryExpression exp;
      try {
        exp = staticEnv.compileQuery(queryReader);
        queryReader.close(); //KL 040218
        staticEnv=exp.getStaticContext();
      }catch(net.sf.saxon.trans.XPathException e){
        throw e;
      }catch(java.io.IOException e){
    	  throw e;
        }

      try {

          //evaluating
          exp.run(dynamicEnv,new StreamResult(result),outputProps);
          result.close();
          }catch(net.sf.saxon.trans.XPathException e){
               listener.error(e);
          }catch (java.io.IOException e){
               throw e;
          }


      //s = result.getBuffer().toString();
      //result.close(); //??

  } catch (Exception e) {
    String errMsg = (listener.hasErrors() ? listener.getErrors() : e.toString());
    try{
    	errMsg = parseErrors(errMsg,staticEnv);
    }
    catch(Exception ex){
    	_logger.error("Unable to parse exception string: " + ex.toString() );
    }

  	_logger.error("==== CATCHED EXCEPTION " + errMsg );
    throw new GDEMException (errMsg);
    //listener.error(e);
  }
  finally {
		if (listener.hasErrors() || dynamicListener.hasErrors() ){
			String errMsg = listener.getErrors() + dynamicListener.getErrors();
			try{
		    	errMsg = parseErrors(errMsg,staticEnv);
		    }
		    catch(Exception ex){
		    	_logger.error("Unable to parse exception string: " + ex.toString() );
		    }
		  	_logger.error(errMsg);
		    throw new GDEMException (errMsg);
		}
	}
//return s;
}

// if URL contains ticket information, then remove it
//if the error messages contains staticEnv.baseURI, then remove it
private String parseErrors(String err, StaticQueryContext staticEnv){

	if(err==null) return null;

	String search_base=Constants.TICKET_PARAM + "=";
	String baseURI = (staticEnv==null)?null:staticEnv.getBaseURI();


	if (baseURI!= null && err.indexOf(baseURI)>0){
		err = eionet.gdem.utils.Utils.Replace(err,baseURI,"xquery");
	}
	StringBuffer buf = new StringBuffer();
    int found = 0;
    int last=0;

    while ((found = err.indexOf(search_base, last)) >= 0) {
        buf.append(err.substring(last, found));
        last = err.indexOf("&", found);
        if(last<0)
      	  last = err.indexOf(" ", found);
        if(last<0)
      	  last = err.length()-1;
    }
    buf.append(err.substring(last));

    return  buf.toString();
}
/*
  public static void main(String [] a) throws Exception {
    String s =  eionet.gdem.Utils.readStrFromFile("\\einrc\\webs\\gdem\\xquery\\sum_emissions.xql");
    String p[] = {eionet.gdem.Utils.XQ_SOURCE_PARAM_NAME + "=" + "http://localhost:8080/gdem/s.xml"};

    eionet.gdem.qa.XQScript x = new eionet.gdem.qa.XQScript(s, p);

    x.getResult();
  }
*/
}