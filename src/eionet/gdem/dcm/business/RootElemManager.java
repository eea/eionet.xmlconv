package eionet.gdem.dcm.business;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.SecurityUtil;

public class RootElemManager {

	private static LoggerIF _logger=GDEMServices.getLogger();
	
	
	public void delete(String user, String elemId) throws DCMException{

        try{
			if(!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "d")){
				  _logger.debug("You don't have permissions to delete root element!");
				  throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_ELEMENT_DELETE);                              
	          }
	        }
	        catch (DCMException e){					    
				throw e;
	        }
			catch (Exception e){			
				_logger.debug(e.toString());
				throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);          
	        }
			
	       //StringBuffer err_buf = new StringBuffer();
	       //String del_id= (String)req.getParameter(Names.XSD_DEL_ID);

	       try{
			   DbModuleIF dbM= GDEMServices.getDbModule();
			   dbM.removeRootElem(elemId);
	        }
	        catch (Exception e){
				_logger.debug(e.toString());
				throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);          
	        }		
	}

	public void add(String user, String schemaId, String elemName, String namespace) throws DCMException{

        try{
			if(!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "i")){
				  _logger.debug("You don't have permissions to insert root elements!");
				  throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_ELEMENT_INSERT);                              
	          }
	        }
	        catch (DCMException e){					    
				throw e;
	        }
			catch (Exception e){			
				_logger.debug(e.toString());
				throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);          
	        }
			

	       try{
			   DbModuleIF dbM= GDEMServices.getDbModule();
			   dbM.addRootElem(schemaId, elemName, namespace);
	        }
	        catch (Exception e){
				_logger.debug(e.toString());
				throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);          
	        }								
	}
	
	
}
