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

package eionet.gdem.deprecated;

import eionet.acl.AppUser;
import eionet.gdem.Constants;
import eionet.gdem.dcm.business.WorkqueueManager;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Handler of storing methods for the GDEM.
 * @author Unknown
 * @author George Sofianos
 */
public class SaveHandler {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveHandler.class);

    private static final WorkqueueManager workqueueManager = new WorkqueueManager();
    /**
     * Handles work queue
     * @param req Servlet request
     * @param action Action
     */
    static void handleWorkqueue(HttpServletRequest req, String action) {
        AppUser user = SecurityUtil.getUser(req, Constants.USER_ATT);
        String user_name = null;
        if (user != null) {
            user_name = user.getUserName();
        }


        if (action.equals(Constants.WQ_DEL_ACTION)) {
            try {
                if (!SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_WQ_PATH, "d")) {
                    req.setAttribute(Constants.ERROR_ATT, "You don't have permissions to delete jobs!");
                    return;
                }
            } catch (Exception e) {
                req.setAttribute(Constants.ERROR_ATT, "Cannot read permissions: " + e.toString());
                return;
            }

            StringBuffer err_buf = new StringBuffer();
            // String del_id = (String)req.getParameter("ID");
            String[] jobs = req.getParameterValues("jobID");

            try {
                workqueueManager.deleteJobs(jobs);
            } catch (Exception e) {
                LOGGER.error("Could not delete jobs!" + e.getMessage());
                err_buf.append("Cannot delete job: " + e.toString());
            }

            if (err_buf.length() > 0) {
                req.setAttribute(Constants.ERROR_ATT, err_buf.toString());
            }
        } else if (action.equals(Constants.WQ_RESTART_ACTION)) {
            try {
                if (!SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_WQ_PATH, "u")) {
                    req.setAttribute(Constants.ERROR_ATT, "You don't have permissions to restart the jobs!");
                    return;
                }
            } catch (Exception e) {
                req.setAttribute(Constants.ERROR_ATT, "Cannot read permissions: " + e.toString());
                return;
            }

            StringBuffer err_buf = new StringBuffer();
            String[] jobs = req.getParameterValues("jobID");

            try {
                workqueueManager.restartJobs(jobs);

            } catch (Exception e) {
                LOGGER.error("Could not restart jobs!" + e.getMessage());
                err_buf.append("Cannot restart jobs: " + e);
            }
            if (err_buf.length() > 0) {
                req.setAttribute(Constants.ERROR_ATT, err_buf.toString());
            }
        }
    }
}
