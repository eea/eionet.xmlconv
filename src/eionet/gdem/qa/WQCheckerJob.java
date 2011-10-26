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
 *                  Enriko Käsper
 */

package eionet.gdem.qa;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IXQJobDao;

/**
 * Periodical check of received jobs for the XQEngine The interval is specified in gdem.properties.
 */
public class WQCheckerJob implements Job {

    /** */
    private static final Log LOGGER = LogFactory.getLog(WQCheckerJob.class);
    /** Dao for getting job data. */
    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();

    /* (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {

        LOGGER.debug("RUN WQChecker.");

        // get new received jobs from the DB
        String[] newJobs = null;
        try {
            int activeJobs = xqJobDao.countActiveJobs();
            if (activeJobs >= Properties.wqMaxJobs) {
                LOGGER.debug("The number of active jobs is greater or equal than max jobs allowed to run in parallel: active jobs:"
                        + activeJobs + "; max jobs: " + Properties.wqMaxJobs);
            } else {
                newJobs = xqJobDao.getJobsLimit(Constants.XQ_RECEIVED, Properties.wqMaxJobs - activeJobs);
            }

        } catch (SQLException sqe) {
            LOGGER.fatal("*** SQL error getting jobs from DB: " + sqe.toString());
        } catch (Exception e) {
            LOGGER.error("*** error when getting received jobs:  " + e.toString());
        }
        XQueryTask xq;
        if (newJobs != null) {
            for (int i = 0; i < newJobs.length; i++) {
                LOGGER.debug("*** waiting job: " + newJobs[i]);

                xq = new XQueryTask(newJobs[i]);
                WQExecutor.getInstance().execute(xq);
            }
        }
    }

}
