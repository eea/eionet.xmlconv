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

import java.util.Enumeration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.MissingResourceException;

//import com.tee.uit.security.AccessControlListIF;
//import com.tee.uit.security.AccessController;
import com.tee.uit.security.*;
//import com.tee.uit.security.SignOnException;

import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.SecurityUtil;


/**
* Main Servlet
*/
public class Main extends HttpServlet implements Names {


  protected HashMap apps;
  protected HashMap appClients; //holds ServiceClients (RPC clients )

  protected final String GDEM_readPermission = "r";
  protected final String GDEM_SSAclName = "/stylesheets";
  protected String authUser;
  protected String unauthUser;
  protected HttpSession session;
  protected String index_jsp = INDEX_JSP;

  private boolean userChanged=false;

  protected HashMap acls;

  /**
  * returns the current Http session
  */
  protected HttpSession getSession(HttpServletRequest req ) {
    session = (HttpSession)req.getAttribute(Names.SESS_ATT);
    return session;
  }

  public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {  

    String action=req.getParameter("ACTION");
    action = (action == null ? "" : action);
    HttpSession sess = req.getSession();

    index_jsp = getWelcomeFile();

    if (action.equals(LOGOUT_ACTION)) {
      doLogout(req);
      userChanged=true;
    }
  
    //if login is going on, no user needed
    if (action.equals(LOGIN_ACTION)) {
      try {
        doLogin(req, res);
      } catch (Exception e ) {
        //l ("exception in login");      
       handleError(req,res, "Error: " + e.toString(), LOGIN_ACTION );
       return;
      }
    }

    //check if session exist, if not redirect to login page
    /*if ( !guard(sess)) {
        if (isAllowed(null)) {
          //l("doLogin");        
          doLogin(req,res);
          //l("Login ok");                  
          action=SHOW_SCHEMAS_ACTION;
        }
        else {
          handleError(req,res, "No session", LOGIN_ACTION );
          return;
        }
    } 
    */
    //HttpSession needed as request attribtue as well
    req.setAttribute(SESS_ATT, sess);


     //redirect to correct JSP
    dispatch(req,res,action);
 }
  
  /**
  * doPost()
  */
  public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
    doGet(req, res);
  }
    /**
    * Login to Stylesheet Repository
    */
    protected void doLogin(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
      String u = req.getParameter("j_username");
      String p = req.getParameter("j_passwd");

      //here we set session as a request attribute    
      HttpSession session = req.getSession();
    

      try {
        //DirectoryService.sessionLogin(u, p);
        AppUser aclUser = new AppUser();

        if(!Utils.isNullStr(u))
          aclUser.authenticate(u,p);

        if (!SecurityUtil.hasPerm(u,GDEM_SSAclName, "v"))//GDEM_readPermission))
          throw new ServletException("Not allowed to use the Styelsheet Repository");


        session.setAttribute(USER_ATT, aclUser);

         
    } catch (Exception dire ){

      session.setAttribute(USER_ATT, null);    
    //  session.setAttribute(Names.APPLICATIONS_ATT, null);
      req.setAttribute(SESS_ATT, null);

      //handleError(req, res,"Authentication failed " + dire.toString(), Names.ERROR_ACTION);
      throw new ServletException("Authentication failed " + dire.toString());
      //l("=================== 1");      
      //return;
    }
    
 }

  /**
  * handle error and direct to the correct JSP
  */
  protected void handleError(HttpServletRequest req, HttpServletResponse res, String errMsg, String action) throws ServletException, IOException  {

    //req.setAttribute(ERROR_ATT, errMsg);
    String jspName = ERROR_JSP;  //default
    if (action.equals(LOGIN_ACTION)){
      req.setAttribute(ERROR_ATT, errMsg);
      jspName = LOGIN_JSP;
      req.getRequestDispatcher(jspName).forward(req,res);
    }
    else{
      HttpSession sess = req.getSession(true);
      Exception err= new Exception(errMsg);
        sess.setAttribute("gdem.exception", err);
      if (Utils.isNullStr(jspName)) jspName = Names.ERROR_JSP;
      
      //req.getRequestDispatcher(jspName).forward(req,res);
      res.sendRedirect(res.encodeRedirectURL(req.getContextPath() + "/" + jspName));
    }
  }

    private void dispatch(HttpServletRequest req, HttpServletResponse res, String action) throws ServletException, IOException  {

    String jspName=index_jsp;
    
     if ( action.equals( SHOW_SCHEMAS_ACTION ))
        jspName= index_jsp;
      else if ( action.equals( SHOW_STYLESHEETS_ACTION ))
        jspName=STYLESHEETS_JSP;
      else if ( action.equals( SHOW_QUERIES_ACTION ))
        jspName=QUERIES_JSP;
      else  if ( action.equals( SHOW_ADDXSL_ACTION ))
        jspName= ADD_XSL_JSP;
      else  if ( action.equals( XSL_ADD_ACTION ) || action.equals( XSL_DEL_ACTION )){
        SaveHandler.handleStylesheets(req,action);
        jspName=STYLESHEETS_JSP;
      }
      else  if ( action.equals( QUERY_ADD_ACTION ) || action.equals( QUERY_DEL_ACTION )){
        SaveHandler.handleQueries(req,action);
        jspName=QUERIES_JSP;
      }
      else if ( action.equals(QUERY_UPD_ACTION)){
        SaveHandler.handleQueries(req,action);
        jspName=QUERY_JSP;
      }
      else if ( action.equals(XSL_UPD_ACTION)){
        SaveHandler.handleStylesheets(req,action);
        jspName=STYLESHEET_JSP;
      }
      else  if ( action.equals( XSD_UPDVAL_ACTION )){
        SaveHandler.handleSchemas(req,action);
        jspName=QUERIES_JSP;
      }
      else  if ( action.equals( XSD_DEL_ACTION )){
        SaveHandler.handleSchemas(req,action);
        jspName=index_jsp;
      }
      else  if ( action.equals( XSDQ_DEL_ACTION )){
        SaveHandler.handleSchemas(req,action);
        jspName=QUERIESINDEX_JSP;
      }
      else  if ( action.equals( XSD_UPD_ACTION )){
        SaveHandler.handleSchemas(req,action);
        jspName=SCHEMA_JSP;
      }
      else if ( action.equals( LOGOUT_ACTION ))
        jspName=index_jsp;
      else if ( action.equals( LOGIN_ACTION )){
          //login has succeeded and we close login window
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.print("<html><script>window.opener.location.reload(true);window.close()</script></html>");
        out.close();   
      }
      else if ( action.equals( SHOW_TESTCONVERSION_ACTION ))
        jspName=TEST_CONVERSION_JSP;
      else if ( action.equals( SHOW_LISTCONVERSION_ACTION ))
        jspName=LIST_CONVERSION_JSP;
      else if ( action.equals( EXECUTE_TESTCONVERSION_ACTION ))
        jspName=TEST_CONVERSION_SERVLET;
      else if ( action.equals( SHOW_SCHEMA_ACTION ))
        jspName=SCHEMA_JSP;
      else  if ( action.equals( ELEM_ADD_ACTION ) || action.equals( ELEM_DEL_ACTION )){
        SaveHandler.handleRootElems(req,action);
        jspName=SCHEMA_JSP;
      }
      else  if ( action.equals( HOST_ADD_ACTION ) || action.equals( HOST_DEL_ACTION ) || action.equals( HOST_UPD_ACTION )){
        SaveHandler.handleHosts(req,action);
        jspName=HOSTS_JSP;
      }
      else  if ( action.equals( WQ_DEL_ACTION )){
        SaveHandler.handleWorkqueue(req,action);
        jspName=LIST_WORKQUEUE_JSP;
      }

      req.getRequestDispatcher(jspName).forward(req,res);
    }
    private void doLogout(HttpServletRequest req) {
      
      //groups=null;
      //permissions=null;

      if (appClients != null)      
        appClients.clear();
      
      req.getSession().removeAttribute(USER_ATT);
      req.removeAttribute(SESS_ATT);

  }

    private boolean guard(HttpSession sess)  {
      if ( sess.getAttribute(USER_ATT)==null)
          return false;
      else
        return true;
    }
    private String getWelcomeFile(){
    
    String welcomefile = null;
    String[] files=(String[])getServletConfig().getServletContext().getAttribute("org.apache.catalina.WELCOME_FILES");

      if (files!=null){
        if (files.length>0){
          welcomefile = files[0];
        }
      }
      if (welcomefile==null){
        if (serviceInstalled(Properties.CONV_SERVICE)){
          welcomefile=INDEX_JSP;
        }
        if (serviceInstalled(Properties.QA_SERVICE)){
          welcomefile=QUERIESINDEX_JSP;
        }
        else {
          welcomefile=INDEX_JSP;
        }
        
      }
      return welcomefile;
    }
    private boolean serviceInstalled(int service){

    		int services_installed = Properties.services_installed;

        // we divide displayWhen with the type's weight
        // and if the result is an odd number, we return true
        // if not, we return false
        int div = services_installed/service;
        
        if (div % 2 != 0)
            return true;
        else
            return false;
    }

}