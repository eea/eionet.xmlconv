package eionet.gdem.dcm.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.struts.upload.FormFile;

import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ConvType;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.dto.UplSchema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.MultipartFileUpload;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.struts.stylesheet.ConvTypeHolder;

public class StylesheetManager {
	private static LoggerIF _logger=GDEMServices.getLogger();
	
	public void delete(String user, String stylesheetId) throws DCMException{
	
    
		
        try{
          if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "d")){
			  _logger.debug("You don't have permissions to delete stylesheet!");
			  throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_DELETE);                              
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
         dbM= GDEMServices.getDbModule();
         HashMap hash = dbM.getStylesheetInfo(stylesheetId);
         String fileName = (String)hash.get("xsl");
         String xslFolder=Properties.xslFolder;
         if (!xslFolder.endsWith(File.separator))
            xslFolder = xslFolder + File.separator;		 
		 Utils.deleteFile(xslFolder + fileName);
		 dbM.removeStylesheet(stylesheetId);
		 
       }
       catch (Exception e){
			_logger.debug(e.toString());
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);          
       }
       
     }		
		
	public ConvTypeHolder getConvTypes() throws DCMException{
		ConvTypeHolder ctHolder = new ConvTypeHolder();

		Vector hcSchemas;
		ArrayList convs;
		
		
		try {
			
			convs = new ArrayList();
			
			DbModuleIF dbM= GDEMServices.getDbModule();
			Vector convTypes = dbM.getConvTypes();
			
            for (int i=0; i<convTypes.size(); i++){
                Hashtable hash = (Hashtable)convTypes.get(i);
				String conv_type=(String)hash.get("conv_type");
				
				ConvType conv = new ConvType();
				conv.setConvType(conv_type);
				convs.add(conv);				
            }			
			ctHolder.setConvTypes(convs);
		} catch (Exception e) {			
			_logger.debug(e.toString());
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);          
		}
		return ctHolder;
		
	}
	
	public void add(String user,String schema, FormFile file, String type, String descr) throws DCMException{
        
        try{
          if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "i")){
			 throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_INSERT);
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
			String fileName=file.getFileName();
			InputStream in=file.getInputStream();
			String filepath=new String(Properties.xslFolder+"/"+file.getFileName());
			OutputStream w= new FileOutputStream(filepath);
			int bytesRead = 0;
	        byte[] buffer = new byte[8192];
	        while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
	               w.write(buffer, 0, bytesRead);
			}
			w.close();
	        in.close();
			file.destroy(); 
		
	        DbModuleIF dbM = GDEMServices.getDbModule();
	         
	        String schemaID=dbM.getSchemaID(schema);
	        if (schemaID==null)
	            schemaID=dbM.addSchema(schema, null);
	        dbM.addStylesheet(schemaID, type, fileName, descr);
       }
       catch (Exception e){
			_logger.debug(e.toString());
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);          
       }
		
	}
	
	public Stylesheet getStylesheet(String stylesheetId) throws DCMException{
		Stylesheet st = new Stylesheet();

		
		try {
			DbModuleIF dbM= GDEMServices.getDbModule();
			if (!stylesheetId.equals("")){
				HashMap xsl = dbM.getStylesheetInfo(stylesheetId);
				if (xsl==null) xsl=new HashMap();

				st.setSchema((String)xsl.get("xml_schema"));
				st.setXsl_descr((String)xsl.get("description"));
				st.setType((String)xsl.get("content_type_out"));
				st.setXsl(Names.XSL_FOLDER + xsl.get("xsl"));
				st.setConvId(stylesheetId);
			}
			

		} catch (Exception e) {			
			_logger.debug(e.toString());
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);          
		}
		return st;
		
	}
		

	public void update(String user,String xsl_id,String schema, FormFile file, String type, String descr) throws DCMException{
        try{
          if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_STYLESHEETS_PATH, "u")){
			 throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_STYLEHEET_UPDATE);
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
			String fileName=file.getFileName().trim();
	        DbModuleIF dbM = GDEMServices.getDbModule();
			
			if(fileName!=null && !fileName.equals("")){		 
				InputStream in=file.getInputStream();
				String filepath=new String(Properties.xslFolder+"/"+fileName);
				OutputStream w= new FileOutputStream(filepath);
				int bytesRead = 0;
		        byte[] buffer = new byte[8192];
		        while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
		               w.write(buffer, 0, bytesRead);
				}
				w.close();
		        in.close();
				file.destroy(); 

				//delete Old xsl
				HashMap hash = dbM.getStylesheetInfo(xsl_id);
				
		        String fileNameOld = (String)hash.get("xsl");
		        String xslFolder=Properties.xslFolder;
		        //if (!xslFolder.endsWith(File.separator))
		        //    xslFolder = xslFolder + File.separator;		
				
				
				 Utils.deleteFile(xslFolder + fileNameOld);
				
				
			}
			
	         
	        String schemaID=dbM.getSchemaID(schema);
	        if (schemaID==null)
	            schemaID=dbM.addSchema(schema, null);
			
			dbM.updateStylesheet(xsl_id, schemaID, descr, fileName, type);
	        
       }
       catch (Exception e){
		   e.printStackTrace();
			_logger.debug(e.toString());
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);          
       }
		
	}

	

}
