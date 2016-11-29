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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import eionet.acl.AppUser;

import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;

/**
 * Main Servlet.
 * @author Unknown
 * @author George Sofianos
 */
public class Main extends HttpServlet implements Names {

    protected HashMap apps;
    protected HashMap appClients; // holds ServiceClients (RPC clients )

    protected final String GDEM_readPermission = "r";
    protected final String GDEM_SSAclName = "/stylesheets";
    protected String authUser;
    protected String unauthUser;
    protected HttpSession session;
    protected String index_jsp = INDEX_JSP;

    private boolean userChanged = false;

    protected HashMap acls;

    /**
     * Returns the current Http session (old controller servlet)
     * @param req Servlet request
     */
    protected HttpSession getSession(HttpServletRequest req) {
        session = (HttpSession) req.getAttribute(Names.SESS_ATT);
        return session;
    }

    /**
     * Handles Get method
     * @param req Servlet request
     * @param res Servlet response
     * @throws ServletException Servlet Exception
     * @throws IOException IO Exception
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        req.setCharacterEncoding(setEncoding());

        String action = req.getParameter("ACTION");
        action = (action == null ? "" : action);
        HttpSession sess = req.getSession();

        index_jsp = getWelcomeFile();

        if (action.equals(LOGOUT_ACTION)) {
            doLogout(req);
            userChanged = true;
        }

        // if login is going on, no user needed
        if (action.equals(LOGIN_ACTION)) {
            try {
                doLogin(req, res);
            } catch (Exception e) {
                // l ("exception in login");
                handleError(req, res, "Error: " + e.toString(), LOGIN_ACTION);
                return;
            }
        }
        // HttpSession needed as request attribtue as well
        req.setAttribute(SESS_ATT, sess);

        // redirect to correct JSP
        dispatch(req, res, action);
    }

    /**
     * Post method
     * @param req Servlet request
     * @param res Servlet response
     * @throws ServletException Servlet Exception
     * @throws IOException IO Exception
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    /**
     * Login to Stylesheet Repository
     * @param req Servlet request
     * @param res Servlet response
     * @throws IOException IO Exception
     * @throws ServletException Servlet Exception
     */
    protected void doLogin(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String u = req.getParameter("j_username");
        String p = req.getParameter("j_passwd");

        // TODO check this, since it hides a field.
        // here we set session as a request attribute
        HttpSession session = req.getSession();

        try {
            // DirectoryService.sessionLogin(u, p);
            AppUser aclUser = new AppUser();

            if (!Utils.isNullStr(u))
                aclUser.authenticate(u, p);
            else
                throw new ServletException("Authentication failed: a username must be provided");

            // if (!SecurityUtil.hasPerm(u,GDEM_SSAclName, "v"))//GDEM_readPermission))
            // throw new ServletException("Not allowed to use the Styelsheet Repository");

            session.setAttribute(USER_ATT, aclUser);
            session.setAttribute(TICKET_ATT, Utils.getEncodedAuthentication(u, p));

        } catch (Exception dire) {

            session.setAttribute(USER_ATT, null);
            // session.setAttribute(Names.APPLICATIONS_ATT, null);
            req.setAttribute(SESS_ATT, null);

            // handleError(req, res,"Authentication failed " + dire.toString(), Names.ERROR_ACTION);
            throw new ServletException("Authentication failed " + dire.toString());
            // l("=================== 1");
            // return;
        }

    }

    /**
     * Handle error and direct to the correct JSP
     * @param req Servlet request
     * @param res Servlet response
     * @param errMsg Error message
     * @param action Action
     * @throws ServletException Servlet Exception
     * @throws IOException IO Exception
     */
    protected void handleError(HttpServletRequest req, HttpServletResponse res, String errMsg, String action)
            throws ServletException, IOException {

        // req.setAttribute(ERROR_ATT, errMsg);
        String jspName = ERROR_JSP; // default
        if (action.equals(LOGIN_ACTION)) {
            req.setAttribute(ERROR_ATT, errMsg);
            jspName = LOGIN_JSP;
            req.getRequestDispatcher(jspName).forward(req, res);
        } else {
            HttpSession sess = req.getSession(true);
            Exception err = new Exception(errMsg);
            sess.setAttribute("gdem.exception", err);
            if (Utils.isNullStr(jspName))
                jspName = Names.ERROR_JSP;

            // req.getRequestDispatcher(jspName).forward(req,res);
            res.sendRedirect(res.encodeRedirectURL(req.getContextPath() + "/" + jspName));
        }
    }

    /**
     * Dispatch method
     * @param req Servlet request
     * @param res Servlet response
     * @param action Action
     * @throws ServletException Servlet Exception
     * @throws IOException IO Exception
     */
    private void dispatch(HttpServletRequest req, HttpServletResponse res, String action) throws ServletException, IOException {

        String jspName = "do/qaScripts";

        if (action.equals(LOGOUT_ACTION))
            jspName = index_jsp;
        else if (action.equals(LOGIN_ACTION)) {
            // login has succeeded and we close login window
            res.setContentType("text/html");
            PrintWriter out = res.getWriter();
            out.print("<html><script>window.opener.location.reload(true);window.close()</script></html>");
            out.close();
        } else if (action.equals(SHOW_TESTCONVERSION_ACTION))
            jspName = TEST_CONVERSION_JSP;
        else if (action.equals(SHOW_LISTCONVERSION_ACTION))
            jspName = LIST_CONVERSION_JSP;
        else if (action.equals(EXECUTE_TESTCONVERSION_ACTION))
            jspName = TEST_CONVERSION_SERVLET;
        else if (action.equals(WQ_DEL_ACTION) || action.equals(WQ_RESTART_ACTION)) {
            SaveHandler.handleWorkqueue(req, action);
            jspName = LIST_WORKQUEUE_JSP;
        }

        res.sendRedirect(jspName);
    }

    /**
     * Logout Method
     * @param req Servlet request
     */
    private void doLogout(HttpServletRequest req) {

        // groups=null;
        // permissions=null;

        if (appClients != null)
            appClients.clear();

        req.getSession().removeAttribute(USER_ATT);
        req.getSession().removeAttribute(TICKET_ATT);
        req.removeAttribute(SESS_ATT);

    }

    /**
     * Guard method
     * @param sess Http session
     * @return True if user ?
     */
    private boolean guard(HttpSession sess) {
        if (sess.getAttribute(USER_ATT) == null)
            return false;
        else
            return true;
    }

    /**
     * Gets welcome file
     * @return welcome file
     */
    private String getWelcomeFile() {

        String welcomefile = null;
        String[] files = (String[]) getServletConfig().getServletContext().getAttribute("org.apache.catalina.WELCOME_FILES");

        if (files != null) {
            if (files.length > 0) {
                welcomefile = files[0];
            }
        }
        if (welcomefile == null) {
            if (serviceInstalled(Properties.CONV_SERVICE)) {
                welcomefile = INDEX_JSP;
            }
            if (serviceInstalled(Properties.QA_SERVICE)) {
                welcomefile = QUERIESINDEX_JSP;
            } else {
                welcomefile = INDEX_JSP;
            }

        }
        return welcomefile;
    }

    /**
     * Checks if service is installed
     * @param service Service
     * @return True if service is installed
     */
    private boolean serviceInstalled(int service) {

        int services_installed = Properties.services_installed;

        // we divide displayWhen with the type's weight
        // and if the result is an odd number, we return true
        // if not, we return false
        int div = services_installed / service;

        if (div % 2 != 0)
            return true;
        else
            return false;
    }

    /**
     * Gets encoding
     * @return Encoding
     */
    protected String setEncoding() {
        return "UTF-8";
    }

}
