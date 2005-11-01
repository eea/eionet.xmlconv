package eionet.gdem.dcm.conf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class DcmProperties {

	private static LoggerIF _logger=GDEMServices.getLogger();	
	
	public void setDbParams(String url, String user, String psw) throws DCMException { 

		String filePath = Properties.appHome + File.separatorChar + "gdem.properties";
		
        try { 
			
			   BufferedReader reader = new BufferedReader(new FileReader(filePath));
			   String line = null;
			   StringBuffer st = new StringBuffer();
			   
			   while((line = reader.readLine()) != null) {
			    // process the line 
				   line = findSetProp(line,"db.url", url);
				   line = findSetProp(line,"db.user", user);
				   line = findSetProp(line,"db.pwd", psw);
				   st.append(line);
				   st.append("\n");
			   }
			   
			   BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
			   out.write(st.toString());
		       out.close();			   
        } catch (IOException e) { 
		   _logger.debug("Saving database parameters failed!");
		   e.printStackTrace();
		   throw new DCMException(BusinessConstants.EXCEPTION_PARAM_DB_FAILED);
        }      
     }

	public void setLdapParams(String url) throws DCMException { 

		String filePath = Properties.appHome + File.separatorChar + "eionetdir.properties";
		
        try { 
			
			   BufferedReader reader = new BufferedReader(new FileReader(filePath));
			   String line = null;
			   StringBuffer st = new StringBuffer();
			   
			   while((line = reader.readLine()) != null) {
			    // process the line 
				   line = findSetProp(line,"ldap.url", url);
				   st.append(line);
				   st.append("\n");
			   }
			   
			   BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
			   out.write(st.toString());
		       out.close();			   
        } catch (IOException e) { 
		   _logger.debug("Saving ldap parameters failed!");
		   e.printStackTrace();
		   throw new DCMException(BusinessConstants.EXCEPTION_PARAM_LDAP_FAILED);
        }      
     }
	
	private String findSetProp(String line,String key, String value){		
		if(line.startsWith(key+"=")){
			line = key+"=" + value;
		}
		return line;
	}
	
	
}
