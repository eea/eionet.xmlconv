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
 * The Original Code is "EINRC-5 / UIT project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem.ssr;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Vector;
import com.tee.uit.security.AppUser;
import eionet.gdem.Utils;
import eionet.gdem.db.DbModuleIF;
import eionet.gdem.db.DbUtils;
import java.io.File;

import java.io.IOException;

/**
* Handler of storing methods for the GDEM
*/
public class SaveHandler {

  private static final int BUF_SIZE = 1024;
  private static DbModuleIF dbM=null;

  /**
  * stylesheets handling 
  * 
  */
  static void handleStylesheets(HttpServletRequest req, String action) {

     String schemaID=null;
     String xslFolder=null;
     String fileName=null;
     
     AppUser user = SecurityUtil.getUser(req);
  	 String user_name=null;
	   if (user!=null)
        user_name = user.getUserName();
        
     xslFolder=Utils.xslFolder; //props.getString("xsl.folder");
     if (!xslFolder.endsWith(File.separator))
        xslFolder = xslFolder + File.separator;

     if (action.equals( Names.XSL_ADD_ACTION) ) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "i")){
             req.setAttribute(Names.ERROR_ATT, "You don't have permissions to insert stylesheets!");
             return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
        try{
          FileUpload fu = new FileUpload(xslFolder);
          fu.uploadFile(req);
          fileName=fu.getFileName();
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Uploading file: " + e.toString());
           return;          
        }


        if (fileName==null){
           req.setAttribute(Names.ERROR_ATT, "Filename is not defined");
           return;
        }

       String schema= (String)req.getParameter("SCHEMA");
       String type= (String)req.getParameter("CONTENT_TYPE");
       String descr= (String)req.getParameter("DESCRIPTION");

       if (Utils.isNullStr(schema)){
         req.setAttribute(Names.ERROR_ATT, "XML schema cannot be empty.");
         return;
       }
       try{
         dbM= DbUtils.getDbModule();
         schemaID=dbM.getSchemaID(schema);
         if (schemaID==null)
            schemaID=dbM.addSchema(schema, null);


         dbM.addStylesheet(schemaID, type, fileName, descr);
       }
       catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Error while saving info into database: " + e.toString());
          return;          
       }
   }
   else if (action.equals( Names.XSL_DEL_ACTION) ) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "d")){
           req.setAttribute(Names.ERROR_ATT, "You don't have permissions to delete stylesheets!");
           return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
       String del_id= (String)req.getParameter(Names.XSL_DEL_ID);
       //schemaID= (String)req.getParameter(Names.SCHEMA_ID);
       
       if (Utils.isNullStr(del_id)){
         req.setAttribute(Names.ERROR_ATT, "Stylesheet ID cannot be empty.");
         return;
       }
       try{
         dbM= DbUtils.getDbModule();
         HashMap hash = dbM.getStylesheetInfo(del_id);
         fileName = (String)hash.get("xsl");
         schemaID= (String)req.getParameter("schema_id");
         dbM.removeStylesheet(del_id);
       }
       catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Error while deleting stylesheet from database: " + e.toString());
          return;          
       }
       try{
          deleteFile(xslFolder + fileName);
       }
       catch (Exception e){
         req.setAttribute(Names.ERROR_ATT, "Cannot delete XSL file: " + e.toString());
         return;
       }
       
     }
     req.setAttribute(Names.SCHEMA_ID, schemaID);

  }
  /**
  * schemas handling 
  * 
  */
  static void handleSchemas(HttpServletRequest req, String action) {

     AppUser user = SecurityUtil.getUser(req);
  	 String user_name=null;
	   if (user!=null)
        user_name = user.getUserName();

    if (action.equals( Names.XSD_DEL_ACTION) ) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "d")){
           req.setAttribute(Names.ERROR_ATT, "You don't have permissions to delete stylesheets!");
           return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
       StringBuffer err_buf = new StringBuffer();
       String del_id= (String)req.getParameter(Names.XSD_DEL_ID);

       try{
         dbM= DbUtils.getDbModule();
         Vector stylesheets = dbM.getSchemaStylesheets(del_id);
         if (stylesheets!=null){
      		 for (int i=0; i<stylesheets.size(); i++){
        			HashMap hash = (HashMap)stylesheets.get(i);
          		String xslFile = (String)hash.get("xsl");

              String xslFolder=Utils.xslFolder; //props.getString("xsl.folder");
              if (!xslFolder.endsWith(File.separator))
                 xslFolder = xslFolder + File.separator;
          
              try{
                deleteFile(xslFolder + xslFile);
              }
              catch (Exception e){
                err_buf.append("Cannot delete XSL file: " + xslFile + "; " + e.toString() + "<BR>");
                //req.setAttribute(Names.ERROR_ATT, "Cannot delete XSL file: " + xslFile + "; " + e.toString());
                continue;
              }
           }        
        	}
          dbM.removeSchema(del_id);              
        }
        catch (Exception e){
          err_buf.append("Cannot delete Schema: " + e.toString() + del_id);
          //req.setAttribute(Names.ERROR_ATT, "Cannot delete Schema: " + e.toString() + del_id);
          //return;
        }
        if (err_buf.length()>0)
          req.setAttribute(Names.ERROR_ATT, err_buf.toString());
      }
  }
  static private void deleteFile(String fileName) throws IOException{
     File file = new File(fileName);
     file.delete();
     
 }

 }