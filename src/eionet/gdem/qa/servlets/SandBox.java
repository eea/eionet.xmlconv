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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tee.uit.security.AppUser;

import eionet.gdem.Constants;
import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.business.SourceFileManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.qa.XQueryService;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IConvTypeDao;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.ValidationService;

public class SandBox  extends HttpServlet implements Constants {

	private static final String HTML_CONTENT_TYPE = "text/html";
	private static final String HTML_CHARACTER_ENCODING = "utf-8";
	private  IQueryDao queryDao =GDEMServices.getDaoService().getQueryDao();
	private  IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();


	public void doGet(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {
		res.sendRedirect("sandbox.jsp"); //GET redirects to JSP
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {

		req.setCharacterEncoding(HTML_CHARACTER_ENCODING);

		String result=null;
		String fileName=null;

		String dataURL = req.getParameter(XQ_SOURCE_PARAM_NAME);
		req.setAttribute(XQ_INS_SOURCE_PARAM_NAME, dataURL);

		//go back to sandbox
		if(!Utils.isNullStr(req.getParameter("backToSandbox"))) {
		    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
			return;
		}
		
		//get user info
		AppUser user = null;
		boolean wqPrm = false;
		boolean wquPrm = false;
		try{
			user = SecurityUtil.getUser(req, Names.USER_ATT);
			wqPrm = user!=null && SecurityUtil.hasPerm(user.getUserName(), "/" + Names.ACL_WQ_PATH, "i");
			wquPrm = user!=null && SecurityUtil.hasPerm(user.getUserName(), "/" + Names.ACL_WQ_PATH, "u");
		} catch (Exception ge){
			req.setAttribute(Names.ERROR_ATT, "Error reading permissions"+ ge.getMessage());
		    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
			return;
		}

		//save changes in XQ script to repository
		if(!Utils.isNullStr(req.getParameter("save"))) {
			String xqFileName = req.getParameter("file_name");
			String xqScript = req.getParameter(XQ_SCRIPT_PARAM);

			if(!wquPrm){
				req.setAttribute(Names.ERROR_ATT, "Access denied!");
			    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
				return;				
			}
			if(Utils.isNullStr(xqScript)) {
				//writeHTMLMessage(res, "The script cannot be empty!");
				req.setAttribute(Names.ERROR_ATT, "Cannot save empty script!");
			    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
				return;
			}
			try{
				Utils.saveStrToFile(xqFileName, xqScript, null);
				req.setAttribute(Names.SUCCESS_ATT, "Script saved successfully. (".concat(Utils.getDateTime(new Date())).concat(")"));
			    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
			} catch (Exception ge){
				req.setAttribute(Names.ERROR_ATT, "Error occured during saving file: " + xqFileName + " - "+ ge.getMessage());
			    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
				//writeHTMLMessage(res, "Error occured during saving file: " + xqFileName + "<br/>"+ ge.getMessage());
			}
			return;
		}


		if (Utils.isNullStr(dataURL)){
			req.setAttribute(Names.ERROR_ATT, "The data URL cannot be empty!");
		    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
			//writeHTMLMessage(res, "The data URL cannot be empty!");
			return;
		}
		try{
	      InputFile inputFile = new InputFile(dataURL);
	      dataURL = inputFile.toString();
	      fileName = inputFile.getFileNameNoExtension();
		}
		catch(Exception e){
			req.setAttribute(Names.ERROR_ATT, "The data URL is not an URI! " + e.toString());
		    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
			//writeHTMLMessage(res, "The data URL is not an URI! " + e.toString());
			return;
		}

		String sandboxtype = req.getParameter("sandboxtype");
		if (Utils.isNullStr(sandboxtype)){
			req.setAttribute(Names.ERROR_ATT, "Sandbox type cannot be empty!");
		    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
			//writeHTMLMessage(res, "Sandbox type cannot be empty!");
			return;
		}
		//search all scripts according to XML Schema and send back to sandbox
		if(!Utils.isNullStr(req.getParameter("findscripts"))) {
			res.sendRedirect(Names.SANDBOX_JSP + "?findscripts=true&source_url=" + dataURL);
			return;
		}
		if (sandboxtype.equals("SCHEMA")){ //execute all the scripts for 1 schema
			String xml_schema = req.getParameter("xml_schema");

			if (Utils.isNullStr(xml_schema)){
				req.setAttribute(Names.ERROR_ATT, "XML Schema cannot be empty!");
			    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
				//writeHTMLMessage(res, "XML Schema cannot be empty!");
				return;
			}


			// Run immediately
			//
			if(!Utils.isNullStr(req.getParameter("runnow"))) {
				String xqScriptId = req.getParameter("script");
				
				if (Utils.isNullStr(xqScriptId)){
					req.setAttribute(Names.ERROR_ATT, "Could not find script ID!");
				    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
					//writeHTMLMessage(res, "XML Schema cannot be empty!");
					return;
				}

				String xqScript=null;
				if (xqScriptId.equals(String.valueOf(JOB_VALIDATION))){
					res.setContentType(HTML_CONTENT_TYPE);
					try{
						ValidationService vs = new ValidationService();
						vs.setTrustedMode(false);
						vs.setTicket(getTicket(req));

						//result = vs.validateSchema(dataURL, xml_schema);
						result = vs.validate(dataURL);
					} catch (DCMException de){
						result = de.getMessage();
					}
					req.setAttribute(XQ_RESULT_ATT, result);
				    req.getRequestDispatcher(Names.SANDBOX_RESULT_JSP).forward(req,res);				
					//res.getWriter().write(result);
				}
				//run QA script
				else{
					HashMap query = getQueryInfo(xqScriptId);
					String xqScriptFile = getQueryFile(query);
					try{
						xqScript = Utils.readStrFromFile( xqScriptFile);
					}
					catch(Exception e){
						req.setAttribute(Names.ERROR_ATT, "Could not read script from file: " + xqScriptFile);
					    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
						//writeHTMLMessage(res, "Could not read script from file: " + xqScriptFile);
						return;
					}

					String resultContentType = getContentType(query);
					String xqOutputType=(query!=null && query.get("content_type")!=null) ?
							(String)query.get("content_type"):null;

					//get the trusted URL from source file adapter
					dataURL = SourceFileManager.getSourceFileAdapterURL(
							getTicket(req),dataURL,false);

					String[] pars = new String[1];
					pars[0] = XQ_SOURCE_PARAM_NAME + "=" + dataURL;

					XQScript xq = new XQScript(xqScript, pars,  xqOutputType);
					xq.setScriptType((String)query.get("script_type"));
	  				xq.setSrcFileUrl(dataURL);
					
					
					//OutputStream outstream = res.getOutputStream();
					OutputStream output = null;					
					try {
						if(!resultContentType.equals(HTML_CONTENT_TYPE)){
							res.setContentType(resultContentType);
							res.setCharacterEncoding(HTML_CHARACTER_ENCODING);
							output = res.getOutputStream();
							xq.getResult(output);
							return;
						}
						else{
							result = xq.getResult();
							req.setAttribute(XQ_RESULT_ATT, result);
						    req.getRequestDispatcher(Names.SANDBOX_RESULT_JSP).forward(req,res);
							return;
						}
					} catch (GDEMException ge){
						result = ge.getMessage();
						if(output==null){
							req.setAttribute(Names.ERROR_ATT, result);
						    req.getRequestDispatcher(Names.SANDBOX_RESULT_JSP).forward(req,res);																		
							return;
						}
						else{
							output.write(result.getBytes());
							return;
						}
					}
				}
			}
			// Add jobs to workqueue engine
			//
			else if(!Utils.isNullStr(req.getParameter("queue"))) {
				
				if(!wqPrm){
					req.setAttribute(Names.ERROR_ATT, "Access denied!");
				    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
					return;				
				}
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
					StringBuffer buffer = new StringBuffer();
					if (Utils.isNullVector(v_result)){
						buffer.append("No jobs has been added to the workqueue!");
					}
					else{
						buffer.append("The following jobs has  been added to the <a href='workqueue.jsp'>workqueue</a>.");
						for (int i=0;i<v_result.size();i++){
							Vector v = (Vector)v_result.get(i);
							buffer.append("<br/>" + String.valueOf(i+1) + ". job ID: " + (String)v.get(0));
						}
					}
					req.setAttribute(Names.SUCCESS_ATT, buffer.toString());
				} catch (GDEMException ge){
					result = ge.getMessage();
					req.setAttribute(Names.ERROR_ATT, result);
				}
			    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
			}
		}
		else if(sandboxtype.equals("SCRIPT")){
//			execute only 1 script from textarea

			String q_id = req.getParameter("ID");
			String xqScript = req.getParameter(XQ_SCRIPT_PARAM);
			String scriptType = req.getParameter(XQ_SCRIPT_TYPE_ATT);

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
					String resultContentType = getContentType(query);
					String xqOutputType=(query!=null && query.get("content_type")!=null) ?
						(String)query.get("content_type"):null;

					xq = new XQScript(xqScript, pars, xqOutputType);
					xq.setScriptType(scriptType);
					xq.setSrcFileUrl(dataURL);
					OutputStream output =null;
					try {
						//System.out.println("siin2");
						if(!resultContentType.startsWith(HTML_CONTENT_TYPE)){
							res.setContentType(resultContentType);
							res.setCharacterEncoding(HTML_CHARACTER_ENCODING);
							output = res.getOutputStream();
							xq.getResult(output);
							return;
						}
						else{
							result=xq.getResult();
							req.setAttribute(XQ_RESULT_ATT, result);
							req.getRequestDispatcher(Names.SANDBOX_RESULT_JSP).forward(req,res);
							return;
						}
					} catch (GDEMException ge){
						result = ge.getMessage();
						if(output==null){
							req.setAttribute(Names.ERROR_ATT, result);
						    req.getRequestDispatcher(Names.SANDBOX_RESULT_JSP).forward(req,res);				
							return;
						}
						else{
							output.write(result.getBytes());
							return;
						}
					}
				}
				// Add job to workqueue engine
				//
				if(!Utils.isNullStr(req.getParameter("queue"))) {

					if(!wqPrm){
						req.setAttribute(Names.ERROR_ATT, "Access denied!");
					    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
						return;				
					}
					XQueryService xqE = new XQueryService();
					xqE.setTicket(getTicket(req));
					xqE.setTrustedMode(false);
					try {
						result = xqE.analyze(dataURL, xqScript, scriptType);
						req.setAttribute(Names.SUCCESS_ATT, "Job (id: " + result + ") successfully added to the <a href='workqueue.jsp'>workqueue</a>.");
						//writeHTMLMessage(res, "Job (id: " + result + ") successfully added to the <a href='workqueue.jsp'>workqueue</a>.");
					} catch (GDEMException ge){
						result = ge.getMessage();
						req.setAttribute(Names.ERROR_ATT, result);
					}
				    req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
				}
			}
			else{
				req.setAttribute(Names.ERROR_ATT, "The script cannot be empty!");
				req.getRequestDispatcher(Names.SANDBOX_JSP).forward(req,res);				
				//writeHTMLMessage(res, "The script cannot be empty!");
			}
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