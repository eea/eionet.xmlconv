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

import java.util.Hashtable;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.services.*;
import eionet.gdem.utils.Utils;

import java.sql.SQLException;
import java.io.FileNotFoundException;


/**
* Container for holding XQueryService XML/RPC methods
* and other common methods
*/
public class XQueryService  implements Constants {

  private static DbModuleIF db; //DbModule
 
  
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
    //init DBModule
    db=GDEMServices.getDbModule();    

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

		//remove the job from the queue / DB when the status won't change= FATAL or READY
		if (status == XQ_FATAL_ERR || status == XQ_READY)
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
				resultValue=Utils.readStrFromFile(jobData[2]);	
			} catch (Exception ioe ) {
				resultCode=JOB_FATAL_ERROR;
				resultValue= "<error>Error reading the XQ value from the file:" + jobData[2] + "</error>";
			}
			
		}

		h.put(RESULT_CODE_PRM, Integer.toString(resultCode));
		h.put(RESULT_VALUE_PRM, resultValue);

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

}