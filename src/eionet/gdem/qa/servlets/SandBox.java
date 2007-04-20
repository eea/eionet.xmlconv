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

package eionet.gdem.qa.servlets;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.business.SourceFileManager;
import eionet.gdem.qa.XQueryService;
import eionet.gdem.validation.ValidationService;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.ServletException;

import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Utils;

import eionet.gdem.qa.XQScript;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.GDEMException;

public class SandBox  extends HttpServlet implements Constants {

	private static final String HTML_CONTENT_TYPE = "text/html";
	private  IQueryDao queryDao =GDEMServices.getDaoService().getQueryDao();
	private  IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {
		res.sendRedirect("sandbox.jsp"); //GET redirects to JSP
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {

		
		String result=null;
		String fileName=null;


		//save changes in XQ script to repository
		if(!Utils.isNullStr(req.getParameter("save"))) {

			String q_id = req.getParameter("ID");
			String xqFileName = req.getParameter("file_name");
			String xqScript = req.getParameter(XQ_SCRIPT_PARAM);

			if(Utils.isNullStr(xqScript)) {
				writeHTMLMessage(res, "The script cannot be empty!");
				return;
			}
			try{
				Utils.saveStrToFile(xqFileName, xqScript, null);
				res.sendRedirect(Names.SANDBOX_JSP + "?ID=" + q_id);
			} catch (Exception ge){
				writeHTMLMessage(res, "Error occured during saving file: " + xqFileName + "<br/>"+ ge.getMessage());
			}
			return;
		}

		String dataURL = req.getParameter(XQ_SOURCE_PARAM_NAME);
		if (Utils.isNullStr(dataURL)){
			writeHTMLMessage(res, "The data URL cannot be empty!");
			return;
		}
		try{
	      InputFile inputFile = new InputFile(dataURL);
	      dataURL = inputFile.toString();
	      fileName = inputFile.getFileNameNoExtension();
		}
		catch(Exception e){
			writeHTMLMessage(res, "The data URL is not an URI! " + e.toString());
			return;			
		}

		String sandboxtype = req.getParameter("sandboxtype");
		if (Utils.isNullStr(sandboxtype)){
			writeHTMLMessage(res, "Sandbox type cannot be empty!");
			return;
		}
		//search all scripts according to XML Schema and send back to sandbox
		if(!Utils.isNullStr(req.getParameter("findscripts"))) {
			res.sendRedirect(Names.SANDBOX_JSP + "?SOURCE_URL=" + dataURL);
			return;
		}
		if (sandboxtype.equals("SCHEMA")){ //execute all the scripts for 1 schema
			String xml_schema = req.getParameter("xml_schema");

			if (Utils.isNullStr(xml_schema)){
				writeHTMLMessage(res, "XML Schema cannot be empty!");
				return;
			}
			
			
			// Run immediately
			//
			if(!Utils.isNullStr(req.getParameter("runnow"))) {
				String xqScriptId = req.getParameter("script");
				String xqScript=null;
				if (xqScriptId.equals(String.valueOf(JOB_VALIDATION))){
					res.setContentType(HTML_CONTENT_TYPE);
					try{
						ValidationService vs = new ValidationService();
						vs.setTrustedMode(false);
						vs.setTicket(getTicket(req));

						//result = vs.validateSchema(dataURL, xml_schema);
						result = vs.validate(dataURL);
					} catch (GDEMException ge){
						result = ge.getMessage();
					}
					res.getWriter().write(result);
				}
				//run QA script
				else{
					HashMap query = getQueryInfo(xqScriptId);
					String xqScriptFile = getQueryFile(query);
					try{
						xqScript = Utils.readStrFromFile( xqScriptFile);
					}
					catch(Exception e){
						writeHTMLMessage(res, "Could not read script from file: " + xqScriptFile);
						return;
					}

					res.setContentType(getContentType(query));
					
					//get the trusted URL from source file adapter
					dataURL = SourceFileManager.getSourceFileAdapterURL(
							getTicket(req),dataURL,false);

					String[] pars = new String[1];
					pars[0] = XQ_SOURCE_PARAM_NAME + "=" + dataURL;

					XQScript xq = new XQScript(xqScript, pars);
					OutputStream outstream = res.getOutputStream();
					try {
						xq.getResult(outstream);
					} catch (GDEMException ge){
						result = ge.getMessage();
						outstream.write(result.getBytes());
					}
				}
			}
			// Add jobs to workqueue engine
			//
			else {
				res.setContentType(HTML_CONTENT_TYPE);
				XQueryService xqE = new XQueryService();
				try {
					xqE.setTicket(getTicket(req));
					xqE.setTrustedMode(false);

					Hashtable h = new Hashtable();
					Vector files = new Vector();
					files.add(dataURL);
					h.put(xml_schema, files);
					Vector v_result = xqE.analyzeXMLFiles(h);
					PrintWriter writer = res.getWriter();
					if (Utils.isNullVector(v_result)){
						writer.write("<html>No jobs has been added to the workqueue!</html>");
					}
					else{
						writer.write("<html>The following jobs has  been added to the <a href='workqueue.jsp'>workqueue</a>.");
						for (int i=0;i<v_result.size();i++){
							Vector v = (Vector)v_result.get(i);
							writer.write("<br/>" + String.valueOf(i+1) + ". job ID: " + (String)v.get(0));
						}
					}
				} catch (GDEMException ge){
					result = ge.getMessage();
					res.getWriter().write(result);
				}
			}
		}
		else if(sandboxtype.equals("SCRIPT")){  
//			execute only 1 script from textarea

			String q_id = req.getParameter("ID");
			String xqScript = req.getParameter(XQ_SCRIPT_PARAM);

			//get the trusted URL from source file adapter
			dataURL = SourceFileManager.getSourceFileAdapterURL(
					getTicket(req),dataURL,false);

			String[] pars = new String[1];
			pars[0] = XQ_SOURCE_PARAM_NAME + "=" + dataURL;

			XQScript xq = null;
			if(!Utils.isNullStr(xqScript)) {
				// Run immediately
				//
				if(!Utils.isNullStr(req.getParameter("runnow"))) {

					HashMap query = getQueryInfo(q_id);
					res.setContentType(getContentType(query));

					xq = new XQScript(xqScript, pars);
					OutputStream outstream = res.getOutputStream();
					try {
						//System.out.println("siin2");
						xq.getResult(outstream);
						//result = xq.getResult();
					} catch (GDEMException ge){
						result = ge.getMessage();
						outstream.write(result.getBytes());
					}
				}
				// Add job to workqueue engine
				//
				else {
					XQueryService xqE = new XQueryService();
					xqE.setTicket(getTicket(req));
					xqE.setTrustedMode(false);
					try {
						result = xqE.analyze(dataURL, xqScript);
						writeHTMLMessage(res, "Job (id: " + result + ") successfully added to the <a href='workqueue.jsp'>workqueue</a>.");
					} catch (GDEMException ge){
						result = ge.getMessage();
						writeHTMLMessage(res, result);
					}
				}
			}
			else
				writeHTMLMessage(res, "The script cannot be empty!");
		}
	}
	/*
	 * Methods writes simple text message on HTML page inot response outputstream
	 */
	private void writeHTMLMessage(HttpServletResponse res, String message) throws IOException{
		res.setContentType(HTML_CONTENT_TYPE);
		res.getWriter().write("<html>");
		res.getWriter().write(message);
		res.getWriter().write("</html>");


	}

	/*
	 * loads Query info from database
	 */
	private HashMap getQueryInfo(String id){
		HashMap query = null;
		if(id != null) {
			try{
				query = queryDao.getQueryInfo(id);
			}
			catch(Exception e){

			}
		}
		return query;
	}
	/*
	 * method returns sqript content type from hashmap or default content type
	 */
	private String getContentType(HashMap hash){
		String content_type = null;
		if (hash!=null && hash.containsKey("content_type")){
			String typeID = (String)hash.get("content_type");
			try{
				Hashtable hashType = convTypeDao.getConvType(typeID);
				if (hash!=null){
					content_type = (String)hashType.get("content_type");
				}
			}
			catch(Exception e){
				//do nothing, return default content type
			}

		}
		if(Utils.isNullStr(content_type))
			return HTML_CONTENT_TYPE;

		return content_type;

	}
	/*
	 * Method returns the script file location in filesystem
	 */
	private String getQueryFile(HashMap hash){
		String query_file = null;
		if (hash!=null && hash.containsKey("query")){
			query_file = Properties.queriesFolder + (String)hash.get("query");
		}
		return query_file;
	}


	private String getTicket(HttpServletRequest req){
		String ticket=null;
		HttpSession httpSession = req.getSession(false);
		if (httpSession != null) {
			ticket = (String)httpSession.getAttribute(Names.TICKET_ATT);
		}

		return ticket;
	}

	/*private static void _l(String s ){
	 System.out.println ("=========================================");
	 System.out.println (s);
	 System.out.println ("=========================================");
	 }*/

}