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

package eionet.gdem.conversion.ssr;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Vector;

import com.tee.uit.security.AppUser;

import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.FileUpload;
import eionet.gdem.utils.MultipartFileUpload;


import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
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
     
     AppUser user = SecurityUtil.getUser(req, Names.USER_ATT);
  	 String user_name=null;
	   if (user!=null)
        user_name = user.getUserName();
        
     xslFolder=Properties.xslFolder; //props.getString("xsl.folder");
     if (!xslFolder.endsWith(File.separator))
        xslFolder = xslFolder + File.separator;

     if (action.equals( Names.XSL_ADD_ACTION) ) {
        HashMap req_params=null;
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
          MultipartFileUpload fu = new MultipartFileUpload(false);
          fu.processMultiPartRequest(req);

          req_params = fu.getRequestParams();
		  	  fu.setFolder(xslFolder);
		  	  fileName=fu.saveFile();
          
          //FileUpload fu = new FileUpload(xslFolder);
          //fu.uploadFile(req);
          //fileName=fu.getFileName();
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Uploading file: " + e.toString());
           return;          
        }


        if (fileName==null){
           req.setAttribute(Names.ERROR_ATT, "Filename is not defined");
           return;
        }

       String schema= (String)req_params.get("SCHEMA");
       String type= (String)req_params.get("CONTENT_TYPE");
       String descr= (String)req_params.get("DESCRIPTION");

       if (Utils.isNullStr(schema)){
         req.setAttribute(Names.ERROR_ATT, "XML schema cannot be empty.");
         return;
       }
       try{
         dbM= GDEMServices.getDbModule();
         
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
   else if (action.equals( Names.XSL_UPD_ACTION) ) {
        HashMap req_params=null;
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "u")){
             req.setAttribute(Names.ERROR_ATT, "You don't have permissions to update stylesheets!");
             return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
        try{
          MultipartFileUpload fu = new MultipartFileUpload(false);
          fu.processMultiPartRequest(req);
          req_params = fu.getRequestParams();
		  	  fu.setFolder(xslFolder);
		  	  fileName=fu.saveFile();
          
          //FileUpload fu = new FileUpload(xformFolder);
          //fu.uploadFile(req);
          //fileName=fu.getFileName();
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Uploading file: " + e.toString());
           return;          
        }


       if (req_params==null){
         req.setAttribute(Names.ERROR_ATT, "Cannot read request parameters.");
         return;
       }


       String schema_id= (String)req_params.get("SCHEMA_ID");
       String xsl_id= (String)req_params.get("XSL_ID");
       String descr= (String)req_params.get("DESCRIPTION");
       String content_type= (String)req_params.get("CONTENT_TYPE");
       String current_file= (String)req_params.get("FILE_NAME");

       if (Utils.isNullStr(schema_id)){
         req.setAttribute(Names.ERROR_ATT, "XML schema id cannot be empty.");
         return;
       }
       if (Utils.isNullStr(xsl_id)){
         req.setAttribute(Names.ERROR_ATT, "Stylesheet id cannot be empty.");
         return;
       }
       fileName = (fileName==null)?current_file:fileName;

       try{
         dbM= GDEMServices.getDbModule();
         

         dbM.updateStylesheet(xsl_id, schema_id, descr, fileName, content_type);
       }
       catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Error while saving info into database: " + e.toString());
          return;          
       }

   }   else if (action.equals( Names.XSL_DEL_ACTION) ) {
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
         dbM= GDEMServices.getDbModule();
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
          Utils.deleteFile(xslFolder + fileName);
       }
       catch (Exception e){
         req.setAttribute(Names.ERROR_ATT, "Cannot delete XSL file: " + e.toString());
         return;
       }
       
     }
     req.setAttribute(Names.SCHEMA_ID, schemaID);

  }

  /**
  * queries handling 
  * 
  */
  static void handleQueries(HttpServletRequest req, String action) {

     String schemaID=null;
     String queriesFolder=null;
     String fileName=null;
     
     AppUser user = SecurityUtil.getUser(req, Names.USER_ATT);
  	 String user_name=null;
	   if (user!=null)
        user_name = user.getUserName();
        
     queriesFolder=Properties.queriesFolder;
     if (!queriesFolder.endsWith(File.separator))
        queriesFolder = queriesFolder + File.separator;

     if (action.equals( Names.QUERY_ADD_ACTION) ) {
        HashMap req_params=null;
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "i")){
             req.setAttribute(Names.ERROR_ATT, "You don't have permissions to insert queries!");
             return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
        try{
          MultipartFileUpload fu = new MultipartFileUpload(false);
          fu.processMultiPartRequest(req);

          req_params = fu.getRequestParams();
  	  	    fu.setFolder(queriesFolder);
		  	 fileName=fu.saveFile();
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Uploading file: " + e.toString());
           return;          
        }

        if (fileName==null){
           req.setAttribute(Names.ERROR_ATT, "Filename is not defined");
           return;
        }

       String schema= (String)req_params.get("SCHEMA");
       String name= (String)req_params.get("SHORT_NAME");
       String descr= (String)req_params.get("DESCRIPTION");
       String content_type= (String)req_params.get("CONTENT_TYPE");

       if (Utils.isNullStr(schema)){
         req.setAttribute(Names.ERROR_ATT, "XML schema cannot be empty.");
         return;
       }
       try{
         dbM= GDEMServices.getDbModule();
         
         schemaID=dbM.getSchemaID(schema);
         if (schemaID==null)
            schemaID=dbM.addSchema(schema, null);

         dbM.addQuery(schemaID, name, fileName, descr, content_type);
       }
       catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Error while saving info into database: " + e.toString());
          return;          
       }
   }
   else if (action.equals( Names.QUERY_UPD_ACTION) ) {
        HashMap req_params=null;
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "u")){
             req.setAttribute(Names.ERROR_ATT, "You don't have permissions to update queries!");
             return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
        try{
          MultipartFileUpload fu = new MultipartFileUpload(false);
          fu.processMultiPartRequest(req);
          req_params = fu.getRequestParams();
		  	  fu.setFolder(queriesFolder);
		  	  fileName=fu.saveFile();
          
          //FileUpload fu = new FileUpload(xformFolder);
          //fu.uploadFile(req);
          //fileName=fu.getFileName();
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Uploading file: " + e.toString());
           return;          
        }


       if (req_params==null){
         req.setAttribute(Names.ERROR_ATT, "Cannot read request parameters.");
         return;
       }


       String schema_id= (String)req_params.get("SCHEMA_ID");
       String query_id= (String)req_params.get("QUERY_ID");
       String name= (String)req_params.get("SHORT_NAME");
       String descr= (String)req_params.get("DESCRIPTION");
       String content_type= (String)req_params.get("CONTENT_TYPE");
       String current_file= (String)req_params.get("FILE_NAME");

       if (Utils.isNullStr(schema_id)){
         req.setAttribute(Names.ERROR_ATT, "XML schema idcannot be empty.");
         return;
       }
       if (Utils.isNullStr(query_id)){
         req.setAttribute(Names.ERROR_ATT, "Query id cannot be empty.");
         return;
       }
       fileName = (fileName==null)?current_file:fileName;

       try{
         dbM= GDEMServices.getDbModule();
         

         dbM.updateQuery(query_id, schema_id,name,descr, fileName, content_type);
       }
       catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Error while saving info into database: " + e.toString());
          return;          
       }

   }
   else if (action.equals( Names.QUERY_DEL_ACTION) ) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "d")){
           req.setAttribute(Names.ERROR_ATT, "You don't have permissions to delete queries!");
           return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
       String del_id= (String)req.getParameter(Names.QUERY_DEL_ID);
       
       if (Utils.isNullStr(del_id)){
         req.setAttribute(Names.ERROR_ATT, "Query ID cannot be empty.");
         return;
       }
       try{
         dbM= GDEMServices.getDbModule();
         HashMap hash = dbM.getQueryInfo(del_id);
         fileName = (String)hash.get("query");
         schemaID= (String)req.getParameter("schema_id");
         dbM.removeQuery(del_id);
       }
       catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Error while deleting query from database: " + e.toString());
          return;          
       }
       try{
          Utils.deleteFile(queriesFolder + fileName);
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

    boolean hasOtherStuff = false;
    
     AppUser user = SecurityUtil.getUser(req, Names.USER_ATT);
  	 String user_name=null;
	   if (user!=null)
        user_name = user.getUserName();

    if (action.equals( Names.XSD_DEL_ACTION) || action.equals( Names.XSDQ_DEL_ACTION) ) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "d")){
           req.setAttribute(Names.ERROR_ATT, "You don't have permissions to delete schemas!");
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
         dbM= GDEMServices.getDbModule();
         if(action.equals( Names.XSD_DEL_ACTION)) {
            Vector stylesheets = dbM.getSchemaStylesheets(del_id);
            if (stylesheets!=null){
         		 for (int i=0; i<stylesheets.size(); i++){
           			HashMap hash = (HashMap)stylesheets.get(i);
             		String xslFile = (String)hash.get("xsl");
   
                 String xslFolder=Properties.xslFolder;
                 if (!xslFolder.endsWith(File.separator))
                    xslFolder = xslFolder + File.separator;
            
                 try{
                   Utils.deleteFile(xslFolder + xslFile);
                 }
                 catch (Exception e){
                   err_buf.append("Cannot delete XSL file: " + xslFile + "; " + e.toString() + "<BR>");
                   continue;
                 }
              }        
           	}
            if(dbM.getSchemaQueries(del_id) != null)
               hasOtherStuff = true;
         }
         else {  // action.equals( Names.XSDQ_DEL_ACTION ) 
            Vector queries = dbM.getSchemaQueries(del_id);
            if (queries!=null){
         		 for (int i=0; i<queries.size(); i++){
           			HashMap hash = (HashMap)queries.get(i);
             		String queryFile = (String)hash.get("query");
   
                 String queriesFolder=Properties.queriesFolder;
                 if (!queriesFolder.endsWith(File.separator))
                    queriesFolder = queriesFolder + File.separator;
            
                 try{
                   Utils.deleteFile(queriesFolder + queryFile);
                 }
                 catch (Exception e){
                   err_buf.append("Cannot delete XQuery file: " + queryFile + "; " + e.toString() + "<BR>");
                   continue;
                 }
              }        
           	}
            if(dbM.getSchemaStylesheets(del_id) != null)
               hasOtherStuff = true;
         }
         dbM.removeSchema(del_id, action.equals(Names.XSD_DEL_ACTION), action.equals(Names.XSDQ_DEL_ACTION), !hasOtherStuff);              
        }
        catch (Exception e){
          err_buf.append("Cannot delete Schema: " + e.toString() + del_id);
          //req.setAttribute(Names.ERROR_ATT, "Cannot delete Schema: " + e.toString() + del_id);
          //return;
        }
        
        
        if (err_buf.length()>0)
          req.setAttribute(Names.ERROR_ATT, err_buf.toString());
      }
      else if (action.equals( Names.XSD_UPD_ACTION)
              || action.equals(Names.XSD_UPDVAL_ACTION)) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "u")){
           req.setAttribute(Names.ERROR_ATT, "You don't have permissions to update schema!");
           return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
       String schema_id= (String)req.getParameter(Names.SCHEMA_ID);

       if (Utils.isNullStr(schema_id)){
         req.setAttribute(Names.ERROR_ATT, "XML schema id cannot be empty.");
         return;
       }
        try{
          dbM= GDEMServices.getDbModule();
          if (action.equals(Names.XSD_UPDVAL_ACTION)){
            String validate= (String)req.getParameter("VALIDATE");

            dbM.updateSchemaValidate(schema_id, validate);
          }
          else{
            String schema_name= (String)req.getParameter("XML_SCHEMA");
            String description= (String)req.getParameter("DESCRIPTION");
            String dtd_public_id= (String)req.getParameter("DTD_PUBLIC_ID");
      
            dbM.updateSchema(schema_id, schema_name, description, dtd_public_id);
          }
        
       }
       catch (Exception e){
         req.setAttribute(Names.ERROR_ATT, "Cannot update Schema: " + e.toString() + schema_id);
         return;
       }
      }
  }
  /**
  * root elements handling 
  * 
  */
  static void handleRootElems(HttpServletRequest req, String action) {

     String schema_id=null;
     String xslFolder=null;
     String fileName=null;


     schema_id = (String)req.getParameter(Names.SCHEMA_ID);
     AppUser user = SecurityUtil.getUser(req, Names.USER_ATT);
  	 String user_name=null;
	   if (user!=null)
        user_name = user.getUserName();
        

     if (action.equals( Names.ELEM_ADD_ACTION) ) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "i")){
             req.setAttribute(Names.ERROR_ATT, "You don't have permissions to insert root elements!");
             return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }

       String elem_name= (String)req.getParameter("ELEM_NAME");
       String namespace= (String)req.getParameter("NAMESPACE");

       if (Utils.isNullStr(schema_id)){
         req.setAttribute(Names.ERROR_ATT, "XML schema id cannot be empty.");
         return;
       }
       try{
         dbM= GDEMServices.getDbModule();

         dbM.addRootElem(schema_id, elem_name, namespace);
       }
       catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Error while saving info into database: " + e.toString());
          return;          
       }
   }
   else if (action.equals(Names.ELEM_DEL_ACTION) ) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "d")){
           req.setAttribute(Names.ERROR_ATT, "You don't have permissions to delete root element!");
           return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
       String del_id= (String)req.getParameter("ELEM_DEL_ID");
       //schemaID= (String)req.getParameter(Names.SCHEMA_ID);
       
       if (Utils.isNullStr(del_id)){
         req.setAttribute(Names.ERROR_ATT, "Root element ID cannot be empty.");
         return;
       }
       try{
         dbM= GDEMServices.getDbModule();
         dbM.removeRootElem(del_id);
       }
       catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Error while deleting root element from database: " + e.toString());
          return;          
       }
       
     }
     req.setAttribute(Names.SCHEMA_ID, schema_id);

  }
  /**
  * hosts handling 
  * 
  */
  static void handleHosts(HttpServletRequest req, String action) {

     AppUser user = SecurityUtil.getUser(req, Names.USER_ATT);
  	 String user_name=null;
	   if (user!=null)
        user_name = user.getUserName();

    if (action.equals( Names.HOST_DEL_ACTION) ) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_HOST_PATH, "d")){
           req.setAttribute(Names.ERROR_ATT, "You don't have permissions to delete hosts!");
           return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
       StringBuffer err_buf = new StringBuffer();
       String del_id= (String)req.getParameter("ID");

       try{
         dbM= GDEMServices.getDbModule();
          dbM.removeHost(del_id);              
        }
        catch (Exception e){
          err_buf.append("Cannot delete host: " + e.toString() + del_id);
          //req.setAttribute(Names.ERROR_ATT, "Cannot delete Schema: " + e.toString() + del_id);
          //return;
        }
        if (err_buf.length()>0)
          req.setAttribute(Names.ERROR_ATT, err_buf.toString());
      }
      else if (action.equals( Names.HOST_UPD_ACTION) ) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_HOST_PATH, "u")){
           req.setAttribute(Names.ERROR_ATT, "You don't have permissions to update host!");
           return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }
       String host_id= (String)req.getParameter("HOST_ID");

       if (Utils.isNullStr(host_id)){
         req.setAttribute(Names.ERROR_ATT, "HOST id cannot be empty.");
         return;
       }
       String host_name= (String)req.getParameter("HOST_NAME");
       String user_n= (String)req.getParameter("USER_NAME");
       String pwd= (String)req.getParameter("PASSWORD");

       try{
         dbM= GDEMServices.getDbModule();
         dbM.updateHost(host_id, host_name, user_n, pwd);
        }
        catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Cannot update host: " + e.toString() + host_id);
          return;
        }
      }
     if (action.equals( Names.HOST_ADD_ACTION) ) {
        try{
          if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_HOST_PATH, "i")){
             req.setAttribute(Names.ERROR_ATT, "You don't have permissions to insert new hosts!");
             return;                   
          }
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
           return;          
        }

       String host_name= (String)req.getParameter("HOST_NAME");
       String user_n= (String)req.getParameter("USER_NAME");
       String pwd= (String)req.getParameter("PASSWORD");

       try{
         dbM= GDEMServices.getDbModule();

         dbM.addHost(host_name, user_n, pwd);
       }
       catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Error while saving info into database: " + e.toString());
          return;          
       }
   }
  }

   static void handleWorkqueue(HttpServletRequest req, String action) {
      AppUser user = SecurityUtil.getUser(req, Names.USER_ATT);
      String user_name = null;
	   if(user != null)
         user_name = user.getUserName();

      if(action.equals(Names.WQ_DEL_ACTION)) {
         try {
            if(!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_WQ_PATH, "d")) {
               req.setAttribute(Names.ERROR_ATT, "You don't have permissions to delete jobs!");
               return;                   
            }
         }
         catch (Exception e) {
            req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
            return;          
         }

         StringBuffer err_buf = new StringBuffer();
         String del_id = (String)req.getParameter("ID");

         try {
            dbM = GDEMServices.getDbModule();
            if(del_id != null)
               dbM.endXQJob(del_id);
         }
         catch (Exception e) {
            err_buf.append("Cannot delete job: " + e.toString() + del_id);
         }
         if(err_buf.length() > 0)
            req.setAttribute(Names.ERROR_ATT, err_buf.toString());
      }
   }
}