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

package eionet.gdem.qa;

import java.sql.SQLException;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IXQJobDao;

/**
 * Periodical check of received jobs for the XQEngine The interval is specified in gdem.properties
 */

public class WQChecker extends TimerTask implements Constants {

    /** */
    private static final Log LOGGER = LogFactory.getLog(WQChecker.class);

    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();

    public WQChecker() {
    }

    /**
     * override of Thread run() method, checks for new jobs in DB
     */
    @Override
    public void run() {
        // get new received jobs from the DB
        String[] newJobs = null;
        try {
            int activeJobs = xqJobDao.countActiveJobs();
            if (activeJobs >= Properties.wqMaxJobs) {
                LOGGER.debug("The number of active jobs is greater or equal than max jobs allowed to run in parallel: active jobs:"
                        + activeJobs + "; max jobs: " + Properties.wqMaxJobs);
            } else {
                newJobs = xqJobDao.getJobsLimit(XQ_RECEIVED, Properties.wqMaxJobs - activeJobs);
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
                xq.start();
            }
        }
    }

}
