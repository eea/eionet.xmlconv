package eionet.gdem.qa;
import java.util.TimerTask;
import eionet.gdem.db.DbModuleIF;
import eionet.gdem.db.DbUtils;
import eionet.gdem.Utils;
import java.sql.SQLException;

//import java.util.Timer;

/**
* Periodical check of received jobs for the XQEngine
*/

public class WQChecker extends TimerTask {
  private static DbModuleIF _db;

  public WQChecker() {
    if (_db==null)
      try {
        _db=DbUtils.getDbModule();
      } catch (Exception e) {
        _db=null;
      }

  }
	/**
	* override of Thread run() method
	*/

	public void run() {

    //getting new received jobs from the DB
    String[] newJobs=null;
    try {
      newJobs=_db.getJobs(Utils.XQ_RECEIVED);
    } catch(SQLException e ) {
			//!! improve me
		   System.out.println("*** error getting jobs from DB: " + e.toString());
    }
    
    XQueryTask xq;
		//long a, b;
    if (newJobs!=null)
      for (int i=0; i<newJobs.length; i++) {
          System.out.println("*** waiting job: " + newJobs[i]);
          xq = new XQueryTask(newJobs[i]);
          xq.start();
      }
    /*else {
      System.out.println("***************** no jobs waiting: " + System.currentTimeMillis() );
		} */
  }



}