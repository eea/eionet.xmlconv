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
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */

package eionet.gdem.qa;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dcm.business.WorkqueueManager;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.exceptions.DCMException;

/**
 * QA Service workqueue cleaner job. Deletes all jobs with status = (READY, FATAL_ERR) and finished more than 24 hours ago
 * (parameter in gdem.proiperties).
 *
 * @author Enriko Käsper
 */
public class WQCleanerJob implements Job {

    /** */
    private static final Log LOGGER = LogFactory.getLog(WQCleanerJob.class);
    /** Dao for getting job data. */
    private WorkqueueManager jobsManager = new WorkqueueManager();

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext paramJobExecutionContext) throws JobExecutionException {

        LOGGER.debug("RUN WQCleanerJob.");
        try {
            List<WorkqueueJob> jobs = jobsManager.getFinishedJobs();

            if (jobs != null) {
                for (WorkqueueJob job : jobs) {
                    if (canDeleteJob(job)) {
                        jobsManager.endXQJob(job);
                    }
                }
            }
        } catch (DCMException e) {
            LOGGER.error("Error when running work-queue clearner job: ", e);
        }
    }

    /**
     * Check the job's age and return true if it is possible to delete it.
     *
     * @param job
     *            Workqueue job object
     * @return true if job can be deleted.
     */
    public static boolean canDeleteJob(WorkqueueJob job) {
        boolean canDelete = false;
        if (job != null && job.getJobTimestamp() != null && job.getStatus() >= Constants.XQ_READY) {
            Calendar now = Calendar.getInstance();
            int maxAge = Properties.wqJobMaxAge == 0 ? -1 : -Properties.wqJobMaxAge;
            now.add(Calendar.HOUR, maxAge);
            Calendar jobCal = Calendar.getInstance();
            jobCal.setTime(job.getJobTimestamp());
            if (now.after(jobCal)) {
                canDelete = true;
            }
        }
        return canDelete;
    }
}
