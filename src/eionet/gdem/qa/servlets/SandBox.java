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
import eionet.gdem.qa.XQueryService;
import java.io.IOException;

import javax.servlet.ServletConfig;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletException;
import eionet.gdem.utils.Utils;

import eionet.gdem.qa.XQScript;
import eionet.gdem.GDEMException;

public class SandBox  extends HttpServlet implements Constants { 

 public void doGet(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {
  res.sendRedirect("sandbox.jsp"); //GET redirects to JSP
 }
 public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {

    String xqScript = req.getParameter(XQ_SCRIPT_PARAM);
    String dataURL = req.getParameter(XQ_SOURCE_PARAM_NAME);
    
    String[] pars = new String[1];
    pars[0] = XQ_SOURCE_PARAM_NAME + "=" + dataURL;

    XQScript xq = null;
    String result = null;
    if(!Utils.isNullStr(xqScript) && !Utils.isNullStr(dataURL)) {
      // Run immediately
      //
      if(!Utils.isNullStr(req.getParameter("runnow"))) {
         xq = new XQScript(xqScript, pars);
         try {
            result = xq.getResult();
         } catch (GDEMException ge){
            result = ge.getMessage();
         }
         res.getWriter().write(result);
      }
      // Add job to workqueue engine
      //
      else {
         XQueryService xqE = new XQueryService(); 
         try {
            result = xqE.analyze(dataURL, xqScript);
            res.getWriter().write("<html>Job (id: " + result + ") successfully added to the <a href='workqueue.jsp'>workqueue</a>.");
         } catch (GDEMException ge){
            result = ge.getMessage();
            res.getWriter().write(result);
         }
      }
    }
    else
       res.getWriter().write("<html>The script or data URL cannot be empty!</html>");
  }

/*private static void _l(String s ){
  System.out.println ("=========================================");
  System.out.println (s);
  System.out.println ("=========================================");  
}*/

}