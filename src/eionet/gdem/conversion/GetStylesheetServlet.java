/*
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
 * The Original Code is Web Dashboards Service
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Kolundzija Dusko (ED)
 */


package eionet.gdem.conversion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.utils.Utils;

public class GetStylesheetServlet extends HttpServlet {
	

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String metaXSLFolder = Properties.metaXSLFolder;
		String tableDefURL = Properties.ddURL;
		String id = req.getParameter("id");
		String convId = req.getParameter("conv");

		ConversionDto conv = Conversion.getConversionById(convId);

		//hardcoded for test
		String format = metaXSLFolder + File.separatorChar+ conv.getStylesheet();
		String url = tableDefURL + "/GetTableDef?id=" + id;

		if (Utils.isNullStr(id) && Utils.isNullStr(convId)) {
			String err_message = "Some of the following parameters are missing: 'id' or 'conv'!";
			handleError(req, res, new GDEMException(err_message), Names.ERROR_JSP);
			return;
		}

		try {
			//do the conversion      
			convertXML(res, url, format);
		} catch (GDEMException ge) {
			handleError(req, res, ge, Names.ERROR_JSP);
			return;
			//throw new ServletException("Conversion failed " + ge.toString());
		}

	}


	private void convertXML(HttpServletResponse res, String url, String format) throws GDEMException, IOException {
		ConversionService cnv = new ConversionService();

		Hashtable result = null;

		result = cnv.makeDynamicXSL(url, format, res);
		String contentType = (String) result.get("content-type");
		byte[] content = (byte[]) result.get("content");
		res.setContentType(contentType);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(content);
		int bufLen = 0;
		byte[] buf = new byte[1024];

		while ((bufLen = byteIn.read(buf)) != -1)
			res.getOutputStream().write(buf, 0, bufLen);
		byteIn.close();
		/*}*/
	}


	/**
	 * handle error and direct to the correct JSP
	 */
	protected void handleError(HttpServletRequest req, HttpServletResponse res, Exception err, String jspName) throws ServletException, IOException {
		//System.out.println(errMsg);
		HttpSession sess = req.getSession(true);
		//GDEMException err= new GDEMException(errMsg);
		sess.setAttribute("gdem.exception", err);
		if (Utils.isNullStr(jspName)) jspName = Names.ERROR_JSP;

		//req.getRequestDispatcher(jspName).forward(req,res);
		res.sendRedirect(res.encodeRedirectURL(req.getContextPath() + "/" + jspName));
		return;
	}


	/**
	 * doPost()
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}

}