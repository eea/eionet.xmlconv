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
 * The Original Code is "EINRC-6 / AIT project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem;

//import net.sf.saxon.Query;
import java.io.IOException;

import java.util.Hashtable;

import eionet.gdem.db.*;
import java.sql.SQLException;
import java.io.FileNotFoundException;

/**
* Container for holding XQueryService XML/RPC methods
*/
public class XQueryService {

  private static DbModuleIF db; //DbModule
  
  /*public XQueryService()  {
  } */
  
  /**
  * Request from XML/RPC client
  * Stores the xqScript and starts a job in the workqueue
  * @param String url: URL of the srouce XML
  * @param String xqScript: XQueryScript to be processed
  */
  public String analyze(String sourceURL, String xqScript) throws GDEMException {

      String  xqFile="";

      //save XQScript in a text file for the WQ
      try {
        xqFile=Utils.saveStrToFile(xqScript, "xql");
      } catch (FileNotFoundException fne) {
        throw new GDEMException("Folder does not exist: :" + fne.toString());
      } catch (IOException ioe ) {
        throw new GDEMException("Error storing XQScript into file:" + ioe.toString());
      }
      
      //name for temporary output file where the esult is stored:
      String resultFile=Utils.tmpFolder + "gdem_" + System.currentTimeMillis() + ".txt";
			String newId="-1"; //should not be returned with value -1;
      //init DBModule
      if(db==null)
        db=DbUtils.getDbModule();
      
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
    //init DBModule
    if(db==null)
      db=DbUtils.getDbModule();    

    String[] jobData=null;
    try {
      jobData=db.getXQJobData(jobId);
    } catch (SQLException sqle) {
      throw new GDEMException("Error gettign XQJob data from DB: " + sqle.toString());
    }

    if (jobData==null) //no such job
      throw new GDEMException("** No such job with ID=" + jobId + " in the queue.");

    int status= Integer.valueOf(jobData[3]).intValue();
		

		Hashtable ret =  result(status, jobData);      
		try {
      db.endXQJob(jobId);
    } catch (SQLException sqle) {
      throw new GDEMException("Error gettign XQJob data from DB: " + sqle.toString());
    }


		return ret;
  } 
	
	//Hashtable to be composed for the getResult() method return value
	private Hashtable result(int status, String[] jobData) {
		Hashtable h = new Hashtable();
		int resultCode;
		String resultValue;
		if (status==Utils.XQ_RECEIVED || status==Utils.XQ_DOWNLOADING_SRC || status==Utils.XQ_PROCESSING) {
			resultCode=Utils.JOB_NOT_READY;
			resultValue="";
		}
		else  {
			if (status==Utils.XQ_READY)
				resultCode=Utils.JOB_READY;
			else if (status==Utils.XQ_FATAL_ERR)
				resultCode=Utils.JOB_FATAL_ERROR;
			else if (status==Utils.XQ_LIGHT_ERR)
				resultCode=Utils.JOB_LIGHT_ERROR;
			else
				resultCode=-1; //not expected to reach here
			try {
				resultValue=Utils.readStrFromFile(jobData[2]);	
			} catch (Exception ioe ) {
				resultCode=Utils.JOB_FATAL_ERROR;
				resultValue= "<error>Error reading the XQ value from the file:" + jobData[2] + "</error>";
			}
			
		}

		h.put(Utils.RESULT_CODE_PRM, Integer.toString(resultCode));
		h.put(Utils.RESULT_VALUE_PRM, resultValue);

		return h;
	
	}

  /**
  * Confirms that the client has receievd the result and it may be removed from the workqueue
  * @param String jobId
  */
	/*
  public String jobReceived(String jobId) throws GDEMException {
    try {
      //all done, remove the job from the queue
      if(db==null)
        db=DbUtils.getDbModule();    
      
      db.endXQJob(jobId);
    } catch (SQLException sql) {
      throw new GDEMException("*** Error ending the job = " + sql.toString());
    }
    return "OK";
  } 
*/
/*
  public static void main(String [] a) throws Exception {
    XQueryService x = new XQueryService();
    String url="http://cdr.eionet.eu.int/ie/eper/colpofz1w/envpucriq/Ireland_XML.xml";
    String xs=Utils.readStrFromFile("\\einrc\\webs\\gdem\\xquery\\sum_emissions.xql");
    //x.analyze(url, xs);
    //x.analyze(url, xs);    
    //Utils.log(    x.jobReceived("22"));
  }
*/
  /**
  * Converts the full file path of temporary result file to XML/RPC client
  * understandable URL
  */
  private String composeUrl(String fullFilePath) {
    int lastSlash = fullFilePath.lastIndexOf("/");

    if (lastSlash !=-1)
      fullFilePath = fullFilePath.substring(lastSlash+1);

    return Utils.urlPrefix + fullFilePath;
  }
}