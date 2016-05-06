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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.acl.AppUser;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;

/**
 * Handler of storing methods for the GDEM.
 * @author Unknown
 * @author George Sofianos
 */
public class SaveHandler {

    /** */
    private static final Log LOGGER = LogFactory.getLog(SaveHandler.class);

    /**
     * Handles work queue
     * @param req Servlet request
     * @param action Action
     */
    static void handleWorkqueue(HttpServletRequest req, String action) {
        AppUser user = SecurityUtil.getUser(req, Names.USER_ATT);
        String user_name = null;
        if (user != null) {
            user_name = user.getUserName();
        }

        if (action.equals(Names.WQ_DEL_ACTION)) {
            try {
                if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_WQ_PATH, "d")) {
                    req.setAttribute(Names.ERROR_ATT, "You don't have permissions to delete jobs!");
                    return;
                }
            } catch (Exception e) {
                req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
                return;
            }

            StringBuffer err_buf = new StringBuffer();
            // String del_id = (String)req.getParameter("ID");
            String[] jobs = req.getParameterValues("jobID");

            try {
                if (jobs.length > 0) {
                    // delete also result files from file system tmp folder
                    try {
                        for (int i = 0; i < jobs.length; i++) {
                            String[] jobData = GDEMServices.getDaoService().getXQJobDao().getXQJobData(jobs[i]);
                            if (jobData == null || jobData.length < 3) {
                                continue;
                            }
                            String resultFile = jobData[2];
                            try {
                                Utils.deleteFile(resultFile);
                            } catch (Exception e) {
                                LOGGER.error("Could not delete job result file: " + resultFile + "." + e.getMessage());
                            }
                            // delete xquery files, if they are stored in tmp folder
                            String xqFile = jobData[1];
                            try {
                                // Important!!!: delete only, when the file is stored in tmp folder
                                if (xqFile.startsWith(Properties.tmpFolder)) {
                                    Utils.deleteFile(xqFile);
                                }
                            } catch (Exception e) {
                                LOGGER.error("Could not delete XQuery script file: " + xqFile + "." + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("Could not delete job result files!" + e.getMessage());
                    }
                    GDEMServices.getDaoService().getXQJobDao().endXQJobs(jobs);
                }

            } catch (Exception e) {
                err_buf.append("Cannot delete job: " + e.toString() + jobs);
            }
            if (err_buf.length() > 0) {
                req.setAttribute(Names.ERROR_ATT, err_buf.toString());
            }
        } else if (action.equals(Names.WQ_RESTART_ACTION)) {
            try {
                if (!SecurityUtil.hasPerm(user_name, "/" + Names.ACL_WQ_PATH, "u")) {
                    req.setAttribute(Names.ERROR_ATT, "You don't have permissions to restart the jobs!");
                    return;
                }
            } catch (Exception e) {
                req.setAttribute(Names.ERROR_ATT, "Cannot read permissions: " + e.toString());
                return;
            }

            StringBuffer err_buf = new StringBuffer();
            String[] jobs = req.getParameterValues("jobID");

            try {
                if (jobs.length > 0) {
                    GDEMServices.getDaoService().getXQJobDao().changeXQJobsStatuses(jobs, Constants.XQ_RECEIVED);
                }

            } catch (Exception e) {
                err_buf.append("Cannot restart jobs: " + e.toString() + jobs);
            }
            if (err_buf.length() > 0) {
                req.setAttribute(Names.ERROR_ATT, err_buf.toString());
            }
        }
    }
}
