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
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.web.spring.scripts;

import javax.servlet.http.HttpServletRequest;


import eionet.gdem.Constants;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sotres qa scripts list in the system cache.
 *
 * @author Enriko Käsper, Tieto Estonia QAScriptListLoader
 */

public class QAScriptListLoader {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(QAScriptListLoader.class);

    public final static String QASCRIPT_LIST_ATTR = "qascript.qascriptList";
    public final static String QASCRIPT_PERMISSIONS_ATTR = "qascript.permissions";

    private static QAScriptListHolder loadQAScriptList(HttpServletRequest httpServletRequest, boolean reload) throws DCMException {

        Object st = httpServletRequest.getSession().getServletContext().getAttribute(QASCRIPT_LIST_ATTR);
        if (st == null || !(st instanceof QAScriptListHolder) || reload) {
            st = new QAScriptListHolder();
            try {
                SchemaManager sm = new SchemaManager();
                st = sm.getSchemasWithQAScripts(null);
            } catch (DCMException e) {
                e.printStackTrace();
                LOGGER.error("Error getting QA scripts list", e);
                throw e;
            }
            httpServletRequest.getSession().getServletContext().setAttribute(QASCRIPT_LIST_ATTR, st);
        }
        Object permissions = httpServletRequest.getSession().getAttribute(QASCRIPT_PERMISSIONS_ATTR);
        if (permissions == null || !(permissions instanceof QAScriptListHolder)) {
            loadPermissions(httpServletRequest);
        }

        return (QAScriptListHolder) st;
    }

    public static void clearList(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().getServletContext().removeAttribute(QASCRIPT_LIST_ATTR);
    }
    public static void reloadList(HttpServletRequest httpServletRequest) throws DCMException {
        loadQAScriptList(httpServletRequest, true);
    }
    public static QAScriptListHolder getList(HttpServletRequest httpServletRequest) throws DCMException {
        return loadQAScriptList(httpServletRequest, false);
    }
    public static void loadPermissions(HttpServletRequest httpServletRequest){
        String user_name = (String) httpServletRequest.getSession().getAttribute("user");
        try {
            httpServletRequest.getSession().setAttribute(QASCRIPT_PERMISSIONS_ATTR, loadQAScriptPermissions(user_name));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error getting QA script permissions", e);
        }
    }

    public static void clearPermissions(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(QASCRIPT_PERMISSIONS_ATTR);
    }
    public  static QAScriptListHolder loadQAScriptPermissions(String userName) throws Exception{
        QAScriptListHolder qa = new QAScriptListHolder();

        boolean ssiPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QUERIES_PATH, "i");
        boolean ssdPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QUERIES_PATH, "d");
        boolean wqiPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_WQ_PATH, "i");
        boolean wquPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QUERIES_PATH, "u");
        boolean qsiPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QASANDBOX_PATH, "i");
        boolean qsuPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QASANDBOX_PATH, "u");

        qa.setSsdPrm(ssdPrm);
        qa.setSsiPrm(ssiPrm);
        qa.setWqiPrm(wqiPrm);
        qa.setWquPrm(wquPrm);
        qa.setQsiPrm(qsiPrm);
        qa.setQsuPrm(qsuPrm);
        return qa;
    }
}
