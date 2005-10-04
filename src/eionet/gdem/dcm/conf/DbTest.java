package eionet.gdem.dcm.conf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class DbTest {

	private static LoggerIF _logger=GDEMServices.getLogger();
	
	public void tstDbParams(String url, String user, String psw) throws Exception {

	    Connection con = null;
	    Statement stmt = null;
	    ResultSet rset = null;

		try{
			//Class.forName(Properties.dbDriver);
			Class.forName(Properties.dbDriver);
			
			con = DriverManager.getConnection(url, user, psw); 
			stmt = con.createStatement();
			  String sql = "SELECT 1";
		      rset = stmt.executeQuery(sql);
			 
		}catch(Exception e){
			 _logger.debug("Testing database connection failed!");
			 _logger.debug(e.getMessage());
			e.printStackTrace();
			throw new DCMException(BusinessConstants.EXCEPTION_PARAM_DB_TEST_FAILED);			
		} finally {
			 // Close connection
			if (rset != null)
	            rset.close();
	         if (stmt != null) {
	            stmt.close();
	            if (!con.getAutoCommit())
	               con.commit();
	         }
            if (con!=null){
				 con.close();
				 con = null;				
            }

		    }
	}
	
	
}
