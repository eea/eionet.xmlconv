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


/**
* XQuery job in the workqueue
*/

package eionet.gdem.qa;

import eionet.gdem.Constants;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.InputFile;
import eionet.gdem.Properties;

import java.sql.SQLException;
import java.util.Iterator;


/**
* A task executing the XQuery task and storing the result of processing
*/
public class XQueryTask extends Thread implements Constants {

  private static DbModuleIF _db;
  private LoggerIF _logger;

  private String _scriptFile;
  private String _resultFile;
  private String _jobId;

  private String _url; //source url for XML

  //int status;

  public XQueryTask(String jobId)  {
    _jobId=jobId;
    _logger=GDEMServices.getLogger();
    
    try {
     _db = GDEMServices.getDbModule();
    } catch (Exception e ) {
      _logger.fatal("** Initializing DB module failed " + e.toString());
      _db=null; //fix me!!!!
    }

    //inits variables from DB where the waiting task is stored
    //URL, XQ_FILE, RESULT_FILE
    initVariables();

    //set MIN priority for this thread->
    setPriority(MIN_PRIORITY);
  }

  /**
  * run XQuery script: steps:
  * - download the source from URL
  * - run XQuery
  * - store the result in a text file
  */
  public void run() {
    try {
      if (_logger.enable(_logger.INFO))
  			_logger.info("Job ID=  " + _jobId + " started getting source file.");      

    	//Status to DOWNLOADING source:
      changeStatus(XQ_DOWNLOADING_SRC);

			//read source from the URL and store it:
			String srcFile=null;
      
			try {
        InputFile inputfile = new InputFile(_url);
        srcFile=inputfile.saveSrcFile();
				//srcFile=Utils.saveSrcFile(_url);

        if(_logger.enable(_logger.DEBUG))
          _logger.debug("==== Source XML was stored to " + srcFile);
          
			//if the URL is not responding, set the status to easy_err and try again in 2 hrs or smth...
			} catch (Exception e ) {
				handleError(e.toString(), true);
				return;
			}
    //saved ok:
    
    //status to -processing
     changeStatus(XQ_PROCESSING);    
     if (_logger.enable(_logger.INFO))
      _logger.info("Job ID=" + _jobId + " XQ processing started");     

			String[] xqParam={XQ_SOURCE_PARAM_NAME + "=" + srcFile};


      try {
        if (_logger.enable(_logger.DEBUG))
          _logger.debug("** XQuery starts, ID=" + _jobId + 
            " params: "  + (xqParam==null ? "<< no params >>" : xqParam[0]) +
            " result will be stored to " + _resultFile );


        String xqScript = Utils.readStrFromFile(_scriptFile);

        if (_logger.enable(_logger.DEBUG))
          _logger.debug("Script: \n" + xqScript );
        
        XQScript xq = new XQScript(xqScript, xqParam);

        String result = xq.getResult();
        
        if (_logger.enable(_logger.DEBUG))
          _logger.debug("Script proceeded, now store to the result file");
        

        Utils.saveStrToFile(_resultFile.substring(0, _resultFile.lastIndexOf(".")), result, "txt");        
        
        
      } catch (Exception e ) {
        handleError("Error processing XQ:" + e.toString(), true);
        return;

      }

      changeStatus(XQ_READY);
        if (_logger.enable(_logger.DEBUG))
          _logger.debug("Job ID=" + _jobId + " succeeded");

      //all done, thread stops here, job is waiting for pulling from the client side      
  

    } catch (Exception ee) {
      handleError("Error in thread run():" + ee.toString(), true);
    }
  }
  
/*private static void _l(String s ){
  System.out.println ("=========================================");
  System.out.println (s);
  System.out.println ("=========================================");  
} */
  //read data from the DB where it is stored for further processing
  private void initVariables()  {
    try {

      //URL, XQ, RESULT
      String[] jobData = _db.getXQJobData(_jobId);

      if (jobData==null)
          handleError("No such job: " + _jobId, true);

      _url=jobData[0];          
      _scriptFile=jobData[1];
      _resultFile=jobData[2]; //just a file name, file is not created yet

    } catch (SQLException sqe ){
      handleError("Error getting WQ data from the DB: " + sqe.toString(), true);
    }
  }


  /**
  * Changes the status to ERROR and finishes the thread normally 
  * saves the error message as the result of the job?
  */
  private void handleError(String error, boolean fatal) {

    _logger.error("Error handling started: <<< " + error + " >>> ");

    try {
			int err_status;

      if (fatal)
				err_status=XQ_FATAL_ERR;
			else
				err_status=XQ_LIGHT_ERR;

			//_db.changeJobStatus(_jobId, err_status);
      changeStatus(err_status);

      //if result file already ok, store the error message in the file:
      if (_resultFile==null) 
        _resultFile= Properties.tmpFolder + "gdem_error" + _jobId;
			else
				_resultFile= _resultFile.substring(0, _resultFile.lastIndexOf("."));
      
      if (_logger.enable(_logger.INFO))
        _logger.info("******* The error message is stored to: " + _resultFile);
      
      if(error==null)
        error="No error message for job=" + _jobId;

      Utils.saveStrToFile(_resultFile, "<error>" + error + "</error>", "txt");

            
    } catch (Exception e) {
      //what to do if exception occurs here...
      _logger.fatal("** Error occured when handling XQ error: " + e.toString());
      
      //probably not needed -> 3 rows
      System.err.println("=============================================================================");
      System.err.println("** EXTREMELY FATAL ERROR OCCURED WHEN HANDLING ERROR: " + e.toString());
      System.err.println("=============================================================================");
    }
  }

  //possible clear temporary files
  private void cleanup() {
  }
  
  private void changeStatus(int status)  {
    try {
			_db.changeJobStatus(_jobId, status);  
    } catch (Exception e ) {
      handleError(e.toString(), true);
    }
  }


}