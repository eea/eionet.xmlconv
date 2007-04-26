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
import java.util.List;

import java.io.Reader;
/*import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter; */
import java.io.File;

/*
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource; */
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

import org.w3c.css.sac.SACMediaList;
import org.xml.sax.InputSource;

import eionet.gdem.qa.XQEngineIF;
import eionet.gdem.Constants;
import eionet.gdem.GDEMException;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.GDEMServices;


import net.sf.saxon.instruct.TerminationException;
import net.sf.saxon.Configuration;
import net.sf.saxon.Transform;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.DynamicQueryContext;
//import net.sf.saxon.query.QueryProcessor;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.query.QueryResult;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.NodeInfo;
//import net.sf.saxon.value.Type;
//import net.sf.saxon.xpath.XPathException;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
/*
import net.sf.saxon.StandardErrorListener;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.transform.ErrorListener;
*/

//import net.sf.saxon.om.EmptyIterator;

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
    StringWriter result = new StringWriter();
    String s="";
   // runQuery(script,xqParams, result);
    try{
    	s = result.getBuffer().toString();
    	result.close(); //??
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

    staticEnv.setBaseURI(new File(script).toURI().toString());

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
      }catch(net.sf.saxon.trans.XPathException e)
        {System.err.println(e.getMessage());
        throw e;
      }catch(java.io.IOException e){
    	  System.err.println(e.getMessage());
    	  throw e;
        }
       
      try {

          //evaluating
          exp.run(dynamicEnv,new StreamResult(result),outputProps);
          result.close();
          }catch(net.sf.saxon.trans.XPathException e)
               {System.err.println(e.getMessage());
               listener.error(e);
          }catch (java.io.IOException e)
               {System.err.println(e.getMessage());
               throw e;
               }
    	  

      //s = result.getBuffer().toString();
      //result.close(); //??
      
  } catch (Exception e) {
  	_logger.debug("==== CATCHED EXCEPTION " + e.toString() );
    String errMsg = (listener.hasErrors() ? listener.getErrors() : e.toString());
    throw new GDEMException (errMsg);
    //listener.error(e);
  } finally {
		if (listener.hasErrors() || dynamicListener.hasErrors() )
			throw new GDEMException (parseErrors(listener.getErrors() + dynamicListener.getErrors()));
	}
//return s;
}

// if URL contains ticket information, then remove it
private String parseErrors(String err){
	  String search_base=Constants.TICKET_PARAM + "=";
	  
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