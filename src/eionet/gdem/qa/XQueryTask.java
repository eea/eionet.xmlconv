package eionet.gdem.qa;
import eionet.gdem.db.DbModuleIF;
import eionet.gdem.db.DbUtils;
import eionet.gdem.Utils;
import java.sql.SQLException;
import java.io.IOException;
import java.io.File;


/**
* A task executing the XQuery task and storing the result of processing
*/
public class XQueryTask extends Thread {

  private static DbModuleIF _db;

  private String _scriptFile;
  private String _resultFile;
  private String _jobId;

  private String _url; //source url for XML

  //int status;

  public XQueryTask(String jobId)  {
    _jobId=jobId;

    try {
      if(_db==null)
        _db = DbUtils.getDbModule();

    } catch (Exception e ) {
      _db=null; //fix me!!!!
    }
    //inits variables from DB where the waiting task is stored
    //URL, XQ_FILE, RESULT_FILE
    initVariables();

    //set MIN priority for this thread->
    setPriority(MIN_PRIORITY);
    
  }

  /**
  * run XQuery: consists of steps:
  * - download the source from URL
  * - run XQuery
  * - store the result in a text file
  */
  public void run() {
    try {
_l("job " + _jobId + " started getting source file.");      

    //Status to DOWNLOADING source:
    _db.changeJobStatus(_jobId, Utils.XQ_DONWLOADING_SRC);
    //read soruce from the URL and store it:
    String srcFile=null;
    try {
      srcFile=Utils.saveSrcFile(_url);
    } catch (Exception e ) {
      handleError(e.toString());
      return;
    }
    //saved ok:
    
    //status to -processing
    _db.changeJobStatus(_jobId, Utils.XQ_PROCESSING);
_l("** job " + _jobId + " processing started");     

    //CHANGE ME TO USE QUERYPROCESSOR
    String xqParam=Utils.XQ_SOURCE_PARAM_NAME + "=" + srcFile;
    String[] args = {"-o", _resultFile, _scriptFile, xqParam};
 
      try {
_l("** query starts: " + _jobId + " params: " + _resultFile + " " + xqParam);

//FIX ME using main() is not correct and does not handle errors!!
       net.sf.saxon.Query.main(args);
        
      } catch (Exception e ) {
        handleError("Error processing XQ:" + e.toString())      ;
        return;
      }

     _db.changeJobStatus(_jobId, Utils.XQ_READY);
_l("** job " + _jobId + " done");     
      //all done, thread stops here, job is waiting for pulling from the client side      
  
      //Thread.sleep(_sleepTime);
_l("End of = " + _jobId);      
    } catch (Exception ee) {
      handleError("Error in thread run():" + ee.toString());
      
    }
  }
  
private static void _l(String s ){
  System.out.println ("=========================================");
  System.out.println (s);
  System.out.println ("=========================================");  

}
  //read data from the DB where it is stored for further processing
  private void initVariables()  {
    try {

      //URL, XQ, RESULT
      String[] jobData = _db.getXQJobData(_jobId);

      if (jobData==null)
          handleError("No such job: " + _jobId);

      _url=jobData[0];          
      _scriptFile=jobData[1];
      _resultFile=jobData[2]; //just a file name file is not created yet

      
      //_resultFile=Utils.tmpFolder + "gdem_" + System.currentTimeMillis() + ".txt";
    } catch (SQLException sqe ){
      handleError("Error getting WQ data from the DB: " + sqe.toString());
    }
  }


  /**
  * Changes the status to ERROR and finishes the thread normally 
  * saves the error message as the result of the job?
  */
  private void handleError(String error) {
    try {
      _db.changeJobStatus(_jobId, Utils.XQ_ERROR);

      //if result file already ok, store the error message in the file:
      if (_resultFile==null) {
        _resultFile= Utils.tmpFolder + "gdem_error" + _jobId;
        //to DB as well?!?
        
      }
        
      Utils.saveStrToFile(_resultFile, "<error>" + error + "</error>", "xml");

      //change the name in the DB?
            
    } catch (Exception e) {
      //what to do if exception occurs here...
      System.out.println("=======================================");
      System.out.println(e.toString());
      System.out.println("=======================================");      
    }
  }

  //possible clear temporary files
  private void cleanup() {
  }
}