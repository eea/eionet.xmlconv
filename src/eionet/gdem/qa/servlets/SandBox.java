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
  res.sendRedirect("sandbox.html"); //GET redirects to HTML
 }
 public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException    {

    String xqScript = req.getParameter(XQ_SCRIPT_PARAM);

    XQScript xq = null;
    String result = null;
    if (!Utils.isNullStr(xqScript)) {
      xq = new XQScript(xqScript, null);
      try {
        result = xq.getResult();
      } catch (GDEMException ge){
        result = ge.getMessage();
      }

     res.getWriter().write(result);
     
    }
    else
       res.getWriter().write("<html>The script cannot be empty</html>");    
  }

/*private static void _l(String s ){
  System.out.println ("=========================================");
  System.out.println (s);
  System.out.println ("=========================================");  
}*/

}