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

      //init DBModule
      if(db==null)
        db=DbUtils.getDbModule();
      
      //start a job in the Workqueue
      try {
        db.startXQJob(sourceURL, xqFile, resultFile);
      } catch (SQLException sqe ) {
        throw new GDEMException("DB operation failed: " + sqe.toString());
      }

      return "OK"; //XML/RPC does not support null that's why we return a STRING
  }

  /**
  * Checks if the job is ready (or error) and returns the result (or error message)
  * @param String jobId
  * @return String fileName where the client can download the result   
  * Returns an empty String if not ready yet
  */
  public String getResult(String jobId) throws GDEMException {
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

    //if the job is ready (or error happened, return the URL of the result where the
    //XML/RPC client can download the file(s) from
    if (status==Utils.XQ_READY) {
      //try {
        //db.changeJobStatus(jobId, Utils.XQ_PULLED);
      /*} catch (SQLException sql) {
        throw new GDEMException("*** Error changing status = " + sql.toString());
      } */
      return composeUrl(jobData[2]); //valid url of the result file
    }
    else
      return ""; //job is not ready, let's wait...
      
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