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

import eionet.gdem.Constants;
import java.io.IOException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.services.*;
import eionet.gdem.utils.Utils;
import eionet.gdem.services.LoggerIF;

import java.sql.SQLException;
import java.io.FileNotFoundException;
import java.util.Vector;


/**
* Container for holding XQueryService XML/RPC methods
* and other common methods
*/
public class XQueryService  implements Constants {

  private static DbModuleIF db; //DbModule
 
  private static LoggerIF _logger;
  
  
  public XQueryService()  {
    _logger=GDEMServices.getLogger();
  }  
  /**
  * List all possible XQueries for this namespace
  */
  public Vector listQueries(String schema) throws GDEMException {

    if (db==null)
      db = GDEMServices.getDbModule();

    Vector v = null;
    try {
      v=db.listQueries(schema);
    } catch (Exception e ) {
      throw new GDEMException("Error getting data from the DB " + e.toString(), e);
    }

    return v;
    

  }
  /**
  * Request from XML/RPC client
  * Stores the source files and starts a job in the workqueue
  * @param Hashtable files: Structure with XMLschemas as a keys and values are list of XML Files
  * @return Hashtable result: Structure with JOB ids as a keys and source files as values
  */
  public Vector analyzeXMLFiles(Hashtable files) throws GDEMException {
      
      Vector result = new Vector();
      
      if (files==null) return result;
      
  		Enumeration _schemas = files.keys();
      while (_schemas.hasMoreElements()){
        String _schema = _schemas.nextElement().toString();
        Vector _files = (Vector)files.get(_schema);
        if (Utils.isNullVector(_files)) continue;
        
        for(int i=0;i<_files.size();i++){
          String _file = (String)_files.get(i);
          analyzeXMLFiles(_schema, _file, result);          
        }
      }
      return result;
  }  
   /**
  * Stores one source file and starts a job in the workqueue
  * @param String schema: XML Schema URL
  * @param String file: Source file URL
  * @return Hashtable result: Structure with JOB ids as a keys and source files as values
  */
 // public Hashtable analyze(String schema, String file) throws GDEMException{
 //   return analyze(schema,file, null);
 // }
  public Vector analyzeXMLFiles(String schema, String file, Vector result) throws GDEMException{
  
      if (result==null) result = new Vector();
      //get all possible xqueries from db
      String newId="-1"; //should not be returned with value -1;
      
      Vector _queries = listQueries(schema);
      db=GDEMServices.getDbModule();
      if (Utils.isNullVector(_queries)) return result;
      
      for (int j=0;j<_queries.size();j++){
        Hashtable _querie = (Hashtable)_queries.get(j);
        String query_id = (String)_querie.get("query_id");
        String query_file = (String)_querie.get("query");
        String content_type = (String)_querie.get("content_type_out");
        String resultFile=Properties.tmpFolder + "gdem_q" + query_id + "_" + 
            System.currentTimeMillis() + "." + content_type.toLowerCase();
        try {
          int int_qID =0;
          try { 
            int_qID=Integer.parseInt(query_id);
          } catch(NumberFormatException n) {
            int_qID = 0;
          }           
          newId=db.startXQJob(file, Properties.queriesFolder + query_file, resultFile, int_qID);
        } catch (SQLException sqe ) {
          throw new GDEMException("DB operation failed: " + sqe.toString());
        }
        Vector _res = new Vector();
        _res.add(newId);
        _res.add(file);
        result.add(_res);
      }
      //checks if the validation is a part of QA Service. If yes, then add it to work queue
      try {
        String db_schema_id = db.getSchemaID(schema);
        HashMap _oSchema = db.getSchema(db_schema_id);
        String validate = (String)_oSchema.get("validate");
        if (validate.equals("1")){
          String resultFile=Properties.tmpFolder + "gdem_validate_" + System.currentTimeMillis() + ".html";
          newId=db.startXQJob(file, schema, resultFile, JOB_VALIDATION);

          Vector _res = new Vector();
          _res.add(newId);
          _res.add(file);
          result.add(_res);
          
        }
      } catch (SQLException sqe ) {
        throw new GDEMException("DB operation failed: " + sqe.toString());
      }
      
      return result;
  }
  /**  
  * Request from XML/RPC client
  * Stores the xqScript and starts a job in the workqueue
  * @param String url: URL of the srouce XML
  * @param String xqScript: XQueryScript to be processed
  */
  public String analyze(String sourceURL, String xqScript) throws GDEMException {
    String  xqFile="";

    _logger.debug("XML/RPC call for analyze xml: " + sourceURL);
    //save XQScript in a text file for the WQ
    try {
      xqFile=Utils.saveStrToFile(xqScript, "xql");
    } catch (FileNotFoundException fne) {
      throw new GDEMException("Folder does not exist: :" + fne.toString());
     } catch (IOException ioe ) {
       throw new GDEMException("Error storing XQScript into file:" + ioe.toString());
    }
      
    //name for temporary output file where the esult is stored:
    String resultFile=Properties.tmpFolder + "gdem_" + System.currentTimeMillis() + ".txt";
    String newId="-1"; //should not be returned with value -1;

    //init DBModule
    db=GDEMServices.getDbModule();
      
    //start a job in the Workqueue
    try {
      newId=db.startXQJob(sourceURL, xqFile, resultFile);
    } catch (SQLException sqe ) {
      throw new GDEMException("DB operation failed: " + sqe.toString());
    }
    return newId; 
  }

  /**
  * Checks if the job is ready (or error) and returns the result (or error message)
  * @param String jobId
  * @return String fileName where the client can download the result   
  * Returns a Hash including code and result
  */
  public Hashtable getResult(String jobId) throws GDEMException {

    _logger.debug("XML/RPC call for getting result with JOB ID: " + jobId);

    //init DBModule
    db=GDEMServices.getDbModule();    

    String[] jobData=null;
    HashMap scriptData=null;
    try {
      jobData=db.getXQJobData(jobId);
      scriptData=db.getQueryInfo(jobData[5]);
    } catch (SQLException sqle) {
      throw new GDEMException("Error gettign XQJob data from DB: " + sqle.toString());
    }

    if (jobData==null) //no such job
      throw new GDEMException("** No such job with ID=" + jobId + " in the queue.");

    int status= Integer.valueOf(jobData[3]).intValue();

    _logger.debug("XQuerySrevice found status for job: " + String.valueOf(status));
    
    Hashtable ret =  result(status, jobData, scriptData, jobId);
    _logger.debug("result: " + ret.toString());

		//remove the job from the queue / DB when the status won't change= FATAL or READY
		if (status == XQ_FATAL_ERR || status == XQ_READY)
			try {
				db.endXQJob(jobId);
        _logger.debug("Delete the job: " + jobId);
			} catch (SQLException sqle) {
				throw new GDEMException("Error gettign XQJob data from DB: " + sqle.toString());
			}

		return ret;
  } 
	
	//Hashtable to be composed for the getResult() method return value
	private Hashtable result(int status, String[] jobData, HashMap scriptData, String jobId) throws GDEMException{
		Hashtable h = new Hashtable();
		int resultCode;
		String resultValue;
		String metatype="";
		String script_title="";
    
		if (status==XQ_RECEIVED || status==XQ_DOWNLOADING_SRC || status==XQ_PROCESSING) {
			resultCode=JOB_NOT_READY;
			resultValue="*** Not ready ***";
		}
		else  {
			if (status==XQ_READY)
				resultCode=JOB_READY;
			else if (status==XQ_FATAL_ERR)
				resultCode=JOB_FATAL_ERROR;
			else if (status==XQ_LIGHT_ERR)
				resultCode=JOB_LIGHT_ERROR;
			else
				resultCode=-1; //not expected to reach here

			try {
        int xq_id = 0;
        try { 
          xq_id=Integer.parseInt(jobData[5]);
        } catch(NumberFormatException n) {}           
        
        if (xq_id == JOB_VALIDATION){
          metatype = "text/html";
          script_title = "XML Schema validation";
        }
        else if(xq_id >0){          
          metatype = (String)scriptData.get("meta_type");
          script_title = (String)scriptData.get("short_name");
        }
        
				resultValue=Utils.readStrFromFile(jobData[2]);	
			} catch (Exception ioe ) {
				resultCode=JOB_FATAL_ERROR;
				resultValue= "<error>Error reading the XQ value from the file:" + jobData[2] + "</error>";
			}
			
		}
    try{
      h.put(RESULT_CODE_PRM, Integer.toString(resultCode));
      h.put(RESULT_VALUE_PRM, resultValue);
      h.put(RESULT_METATYPE_PRM, metatype);
      h.put(RESULT_SCRIPTTITLE_PRM, script_title);
    }
    catch(Exception e){
      String err_mess="JobID: " + jobId + "; Creating result Hashtable for getResult method failed result: " + e.toString();
      _logger.error(err_mess);
      throw new GDEMException(err_mess, e);
    }

		return h;
	
	}

  /**
  * returns an instance of the best XQEngine :)
  * implementator class name specified in the props file
  */
  static XQEngineIF getEngine() throws GDEMException {
    String className=Properties.engineClass; // "eionet.gdem.qa.engines.SaxonImpl";
    XQEngineIF engine = null;
    try {
      Class engineClass =  Class.forName(className);
      engine = (XQEngineIF)engineClass.newInstance();
    }  catch (ClassNotFoundException cn) {
      throw new GDEMException("No such class: " + className);
    } catch (Exception e ) {
       throw new GDEMException("Error initializing engine  " +e.toString());    
    }
    
    return engine; //new SaxonImpl();
  }
  public static void main(String args[]) {
    try{
      XQueryService xqs = new XQueryService();
      Vector v = xqs.listQueries("http://dd.eionet.eu.int/GetSchema?id=TBL2552");
      //Hashtable h = xqs.getResult("383");
      System.out.println(v.toString());
      //System.out.println("h.toString()");
    }
    catch(Exception e ){
      System.out.println(e.toString());
    }
    
}

}