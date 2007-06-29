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
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem.conversion.ssr;

import javax.servlet.http.HttpServletRequest;


import java.util.HashMap;
import java.util.Vector;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.tee.uit.security.AppUser;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.MultipartFileUpload;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IQueryDao;



/**
* Handler of storing methods for the GDEM
*/
public class SaveHandler {

  private static LoggerIF _logger = GDEMServices.getLogger();
  private static final int BUF_SIZE = 1024;

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
           schemaID=GDEMServices.getDaoService().getSchemaDao().getSchemaID(schema);
         if (schemaID==null)
             schemaID=GDEMServices.getDaoService().getSchemaDao().addSchema(schema, null);


         GDEMServices.getDaoService().getStyleSheetDao().addStylesheet(schemaID, type, fileName, descr);

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
         GDEMServices.getDaoService().getStyleSheetDao().updateStylesheet(xsl_id, schema_id, descr, fileName, content_type);

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
         HashMap hash = GDEMServices.getDaoService().getStyleSheetDao().getStylesheetInfo(del_id);

         fileName = (String)hash.get("xsl");
         schemaID= (String)req.getParameter("schema_id");
         GDEMServices.getDaoService().getStyleSheetDao().removeStylesheet(del_id);

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

     /*
      * Handle add query
      */
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
          fileName = fu.getFileName();
          if (Utils.isNullStr(fileName)){
    	    req.setAttribute(Names.ERROR_ATT, "No file found to upload.");
		    return;
          }
          //check if file exists in filesystem or in database
  		  if (fu.getFileExists() || GDEMServices.getDaoService().getQueryDao().checkQueryFile(fileName)){
  	        req.setAttribute(Names.ERROR_ATT, "File already exists. Rename the file and upload it again.");
  		    return;
  		  }
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

    	 schemaID=GDEMServices.getDaoService().getSchemaDao().getSchemaID(schema);
         if (schemaID==null)
             schemaID=GDEMServices.getDaoService().getSchemaDao().addSchema(schema, null);


         GDEMServices.getDaoService().getQueryDao().addQuery(schemaID, name, fileName, descr, content_type);

       }
       catch (Exception e){
          req.setAttribute(Names.ERROR_ATT, "Error while saving info into database: " + e.toString());
          return;
       }
   }
     /*
      * Update query page
      */
   else if (action.equals( Names.QUERY_UPD_ACTION) ) {
        HashMap req_params=null;
        String schema_id=null;
        String query_id= null;
        String name= null;
        String descr= null;
        String content_type= null;
        String current_file= null;
        boolean save_source = false;
        String file_data = null;
        String strChecksum = null;
		String newChecksum = null;

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
            schema_id= (String)req_params.get("SCHEMA_ID");
            query_id= (String)req_params.get("QUERY_ID");
            name= (String)req_params.get("SHORT_NAME");
            descr= (String)req_params.get("DESCRIPTION");
            content_type= (String)req_params.get("CONTENT_TYPE");
            current_file= (String)req_params.get("FILE_NAME");
            file_data= (String)req_params.get("FILEDATA");
            strChecksum= (String)req_params.get("CHECKSUM");

            save_source = (req_params.get("SAVE")==null) ? false : true;

            if(save_source){
        		//save the file source from textarea into file
        		if(!Utils.isNullStr(current_file) && !Utils.isNullStr(file_data) &&
        				file_data.indexOf(IQueryDao.FILEREAD_EXCEPTION)==-1) {

        			//compare checksums
        			try{
        				newChecksum = Utils.getChecksumFromString(file_data);
        			}
        			catch(Exception e){
        			 _logger.error("unable to create checksum");
        			}
        			if(strChecksum==null)strChecksum="";
        			if(newChecksum==null)newChecksum="";

        			if(!strChecksum.equals(newChecksum)){
        				Utils.saveStrToFile(Properties.queriesFolder + File.separator + current_file, file_data,null);
        			}
        		}

        	}
        	else{
        		//	upload file
               	if (!Utils.isNullStr(fu.getFileName())){
               		if (Utils.isNullStr(current_file)){
               			//	Didn't have filename in database
        				current_file = fu.getFileName();
        				//	check if file exists
        				if (fu.getFileExists() || GDEMServices.getDaoService().getQueryDao().checkQueryFile(current_file)){
        					req.setAttribute(Names.ERROR_ATT, "File already exists. Rename the file and upload it again.");
        					return;
      		  			}
        			}
        			fu.setFolder(queriesFolder);
        			fu.saveFileAs(current_file,true);
        		}
        	}
        }
        catch (Exception e){
           req.setAttribute(Names.ERROR_ATT, "Upadating query: " + e.toString());
           return;
        }


       if (req_params==null){
         req.setAttribute(Names.ERROR_ATT, "Cannot read request parameters.");
         return;
       }



       if (Utils.isNullStr(schema_id)){
         req.setAttribute(Names.ERROR_ATT, "XML schema idcannot be empty.");
         return;
       }
       if (Utils.isNullStr(query_id)){
         req.setAttribute(Names.ERROR_ATT, "Query id cannot be empty.");
         return;
       }

       try{


         GDEMServices.getDaoService().getQueryDao().updateQuery(query_id, schema_id,name,descr, current_file, content_type);

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

         HashMap hash = GDEMServices.getDaoService().getQueryDao().getQueryInfo(del_id);

         fileName = (String)hash.get("query");
         schemaID= (String)req.getParameter("schema_id");
         GDEMServices.getDaoService().getQueryDao().removeQuery(del_id);

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

         if(action.equals( Names.XSD_DEL_ACTION)) {
            Vector stylesheets = GDEMServices.getDaoService().getSchemaDao().getSchemaStylesheets(del_id);
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
            if(GDEMServices.getDaoService().getSchemaDao().getSchemaQueries(del_id) != null)
               hasOtherStuff = true;
         }
         else {  // action.equals( Names.XSDQ_DEL_ACTION )
            Vector queries = GDEMServices.getDaoService().getSchemaDao().getSchemaQueries(del_id);
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
            if(GDEMServices.getDaoService().getSchemaDao().getSchemaStylesheets(del_id) != null)
               hasOtherStuff = true;
         }
         GDEMServices.getDaoService().getSchemaDao().removeSchema(del_id, action.equals(Names.XSD_DEL_ACTION), action.equals(Names.XSDQ_DEL_ACTION), !hasOtherStuff);
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

          if (action.equals(Names.XSD_UPDVAL_ACTION)){
            String validate= (String)req.getParameter("VALIDATE");

            GDEMServices.getDaoService().getSchemaDao().updateSchemaValidate(schema_id, validate);
          }
          else{
            String schema_name= (String)req.getParameter("XML_SCHEMA");
            String description= (String)req.getParameter("DESCRIPTION");
            String dtd_public_id= (String)req.getParameter("DTD_PUBLIC_ID");

            GDEMServices.getDaoService().getSchemaDao().updateSchema(schema_id, schema_name, description, dtd_public_id);

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

           GDEMServices.getDaoService().getRootElemDao().addRootElem(schema_id, elem_name, namespace);

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
           GDEMServices.getDaoService().getRootElemDao().removeRootElem(del_id);
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

           GDEMServices.getDaoService().getHostDao().removeHost(del_id);

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
           GDEMServices.getDaoService().getHostDao().updateHost(host_id, host_name, user_n, pwd);
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

        GDEMServices.getDaoService().getHostDao().addHost(host_name, user_n, pwd);
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
         //String del_id = (String)req.getParameter("ID");
         String[] jobs= req.getParameterValues("jobID");

         try {
            if(jobs.length>0)
                GDEMServices.getDaoService().getXQJobDao().endXQJobs(jobs);

         }
         catch (Exception e) {
            err_buf.append("Cannot delete job: " + e.toString() + jobs);
         }
         if(err_buf.length() > 0)
            req.setAttribute(Names.ERROR_ATT, err_buf.toString());
      }
      else if(action.equals(Names.WQ_RESTART_ACTION)) {
          try {
             if(!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_WQ_PATH, "u")) {
                req.setAttribute(Names.ERROR_ATT, "You don't have permissions to restart the jobs!");
                return;
             }
          }
          catch (Exception e) {
             req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
             return;
          }

          StringBuffer err_buf = new StringBuffer();
          String[] jobs= req.getParameterValues("jobID");

          try {
             if(jobs.length>0)
                 GDEMServices.getDaoService().getXQJobDao().changeXQJobsStatuses(jobs, Constants.XQ_RECEIVED);

          }
          catch (Exception e) {
             err_buf.append("Cannot restart jobs: " + e.toString() + jobs);
          }
          if(err_buf.length() > 0)
             req.setAttribute(Names.ERROR_ATT, err_buf.toString());
       }}
}
