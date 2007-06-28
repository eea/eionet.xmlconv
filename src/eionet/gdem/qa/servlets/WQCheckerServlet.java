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

import javax.servlet.ServletConfig;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletException;
import java.util.Timer;
import eionet.gdem.qa.WQChecker;
//import eionet.gdem.utils.Utils;
import eionet.gdem.Properties;

/**
* Servlet started automatically when the servlet engine starts
* Runs the scheduled Workqueue checker - checks if new jobs received
*/

public class WQCheckerServlet extends HttpServlet {

  public void init(ServletConfig config) throws ServletException {

    try {
			(new Timer(true)).scheduleAtFixedRate( new WQChecker(), 0, Properties.wqCheckInterval );
    } catch (Exception e) {
      //better error handling here!!
      throw new ServletException(e.getMessage(), e);
    }
  }

}