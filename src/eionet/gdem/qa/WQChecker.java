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

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IXQJobDao;

/**
 * Periodical check of received jobs for the XQEngine The interval is specified in gdem.properties
 */

public class WQChecker extends TimerTask implements Constants {
    private static LoggerIF _logger;

    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();

    public WQChecker() {
        _logger = GDEMServices.getLogger();
        /*
         * try { _db=GDEMServices.getDbModule(); } catch (Exception e) { _db=null; _logger.fatal("Initializing DB Pool failed: " +
         * e.toString() , e);
         * 
         * }
         */
    }

    /**
     * override of Thread run() method, checks for new jobs in DB
     */
    public void run() {
        // get new received jobs from the DB
        String[] newJobs = null;
        try {
            int activeJobs = xqJobDao.countActiveJobs();
            if (activeJobs >= Properties.wqMaxJobs) {
                if (_logger.enable(_logger.DEBUG))
                    _logger.debug("The number of active jobs is greater or equal than max jobs allowed to run in parallel: active jobs:"
                            + activeJobs + "; max jobs: " + Properties.wqMaxJobs);
            } else {
                newJobs = xqJobDao.getJobsLimit(XQ_RECEIVED, Properties.wqMaxJobs - activeJobs);
            }

        } catch (SQLException sqe) {
            _logger.fatal("*** SQL error getting jobs from DB: " + sqe.toString());
        } catch (Exception e) {
            _logger.error("*** error when getting received jobs:  " + e.toString());
        }

        XQueryTask xq;
        if (newJobs != null)
            for (int i = 0; i < newJobs.length; i++) {
                if (_logger.enable(_logger.DEBUG))
                    _logger.info("*** waiting job: " + newJobs[i]);

                xq = new XQueryTask(newJobs[i]);
                xq.start();
            }
    }

}
