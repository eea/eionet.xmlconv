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

package eionet.gdem.web.spring.workqueue;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.jpa.Entities.JobHistoryEntry;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.qa.XQueryService;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

import static eionet.gdem.web.listeners.JobScheduler.getQuartzHeavyScheduler;
import static eionet.gdem.web.listeners.JobScheduler.getQuartzScheduler;

/**
 * Work Queue Manager.
 *
 * @author Enriko Käsper, Tieto Estonia
 */

public class WorkqueueManager {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkqueueManager.class);
    /** Dao for getting job data. */
    private static IXQJobDao jobDao = GDEMServices.getDaoService().getXQJobDao();

    /**
     * Get work-queue job data.
     *
     * @param jobId
     *            Job unique ID in DB.
     * @return WorkqueueJob object.
     * @throws DCMException
     *             Database exception occured.
     */
    public WorkqueueJob getWqJob(String jobId) throws DCMException {
        WorkqueueJob job = null;
        try {
            String[] jobData = jobDao.getXQJobData(jobId);
            job = parseJobData(jobData);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error getting workqueue job", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return job;
    }

    /**
     * Adds a new jobs into the workqueue using script content sent as the method parameter
     *
     * @param user
     *            Logged in user name.
     * @param sourceUrl
     *            Source file URL.
     * @param scriptContent
     *            Script content to be stored in workqueue.
     * @param scriptType
     *            Script title.
     * @return Job ID.
     * @throws DCMException If an error occurs.
     */
    public String addQAScriptToWorkqueue(String user, String sourceUrl, String scriptContent, String scriptType)
    throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "i")) {
                LOGGER.debug("You don't have permissions jobs into workqueue!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        XQueryService xqE = new XQueryService();
        xqE.setTrustedMode(false);
        try {
            String result = xqE.analyze(sourceUrl, scriptContent, scriptType);
            return result;
        } catch (Exception e) {
            LOGGER.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Adds new jobs into the workqueue by the given XML Schema
     *
     * @param user
     *            Loggedin user name.
     * @param sourceUrl
     *            Source URL of XML file.
     * @param schemaUrl
     *            XML Schema URL.
     * @return List of job IDs.
     * @throws DCMException If an error occurs.
     */
    public List<String> addSchemaScriptsToWorkqueue(String user, String sourceUrl, String schemaUrl) throws DCMException {

        List<String> result = new ArrayList<String>();
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "i")) {
                LOGGER.debug("You don't have permissions jobs into workqueue!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        XQueryService xqE = new XQueryService();
        xqE.setTrustedMode(false);
        try {
            Hashtable h = new Hashtable();
            Vector files = new Vector();
            files.add(sourceUrl);
            h.put(schemaUrl, files);
            Vector v_result = xqE.analyzeXMLFiles(h);
            if (v_result != null) {
                for (int i = 0; i < v_result.size(); i++) {
                    Vector v = (Vector) v_result.get(i);
                    result.add((String) v.get(0));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return result;
    }

    /**
     * Gets finished jobs.
     * @return List of finish jobs
     * @throws DCMException If an error occurs.
     */
    public List<WorkqueueJob> getFinishedJobs() throws DCMException {
        List<WorkqueueJob> jobs = new ArrayList<WorkqueueJob>();
        try {
            String[][] jobsData = jobDao.getXQFinishedJobs();
            if (jobsData != null && jobsData.length > 0) {
                for (String[] jobData : jobsData) {
                    if (jobData != null) {
                        jobs.add(parseJobData(jobData));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error getting finished workqueue jobs", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return jobs;

    }

    /**
     * Parses Job data
     * @param jobData Job data
     * @return Job
     * @throws ParseException If an error occurs.
     */
    private WorkqueueJob parseJobData(String[] jobData) throws ParseException {
        WorkqueueJob job = null;
        if (jobData != null && jobData.length > 4) {
            job = new WorkqueueJob();
            job.setUrl((jobData[0] == null) ? "" : jobData[0]);
            job.setScriptFile((jobData[1] == null) ? "" : jobData[1]);
            job.setResultFile((jobData[2] == null) ? "" : jobData[2]);
            job.setStatus((jobData[3] == null) ? 0 : new Integer(jobData[3]));
            job.setSrcFile((jobData[4] == null) ? "" : jobData[4]);
            job.setScriptId((jobData[5] == null) ? "" : jobData[5]);
            job.setJobId((jobData[6] == null) ? "" : jobData[6]);
            job.setJobTimestamp(Utils.parseDate(jobData[7], "yyyy-MM-dd HH:mm:ss"));
        }
        return job;
    }

    /**
     * Remove the job from the queue and delete temporary files.
     *
     * @param job Work queue job
     * @throws DCMException If an error occurs.
     */
    public void endXQJob(WorkqueueJob job) throws DCMException {
        // remove the job from the queue / DB when the status won't change= FATAL or READY
        try {
            jobDao.endXQJob(job.getJobId());
            LOGGER.info("Delete expired job: " + job.getJobId());
        } catch (SQLException sqle) {
            throw new DCMException("Error getting XQJob data from DB: " + sqle.toString());
        }
        // delete files only, if debug is not enabled
        if (!LOGGER.isDebugEnabled()) {
            // delete the result from filesystem
            String resultFile = job.getResultFile();
            try {
                Utils.deleteFile(resultFile);
            } catch (Exception e) {
                LOGGER.error("Could not delete job result file: " + resultFile + "." + e.getMessage());
            }
            // delete XQuery file, if it is stored in tmp folder
            String xqFile = job.getScriptFile();
            try {
                // Important!!!: delete only, when the file is stored in tmp folder
                if (xqFile.startsWith(Properties.tmpFolder)) {
                    Utils.deleteFile(xqFile);
                }
            } catch (Exception e) {
                LOGGER.error("Could not delete job result file: " + xqFile + "." + e.getMessage());
            }
        }
    }

    /**
     * Reset active jobs on startup.
     */
    public static void resetActiveJobs() {
        try {
            jobDao.changeJobStatusByStatus(Constants.XQ_DOWNLOADING_SRC, Constants.XQ_RECEIVED);
            List<JobHistoryEntry> entriesDownloading = getJobHistoryRepository().findAllByStatus(Constants.XQ_DOWNLOADING_SRC);
            for(JobHistoryEntry entry: entriesDownloading){
                getJobHistoryRepository().save(new JobHistoryEntry(entry.getJobName(), Constants.XQ_RECEIVED, Utils.getGMTCurrentTimestamp(), entry.getUrl(), entry.getXqFile(), entry.getResultFile(), entry.getXqType()));
                LOGGER.info("Job with id #" + entry.getJobName() + " has been inserted in table JOB_HISTORY ");
            }
            jobDao.changeJobStatusByStatus(Constants.XQ_PROCESSING, Constants.XQ_RECEIVED);
            List<JobHistoryEntry> entriesProcessing = getJobHistoryRepository().findAllByStatus(Constants.XQ_PROCESSING);
            for(JobHistoryEntry entry: entriesProcessing){
                getJobHistoryRepository().save(new JobHistoryEntry(entry.getJobName(), Constants.XQ_RECEIVED, Utils.getGMTCurrentTimestamp(), entry.getUrl(), entry.getXqFile(), entry.getResultFile(), entry.getXqType()));
                LOGGER.info("Job with id #" + entry.getJobName() + " has been inserted in table JOB_HISTORY ");
            }
        } catch (Exception e) {
            LOGGER.error("Error reseting active jobs: " + e.toString());
        }
    }

    /**
     * Restart jobs by id.
     */
    public static void restartJobs(String[] jobIds) throws XMLConvException {
        LOGGER.info("Request to restart jobs " + Utils.stringArray2String(jobIds, "," ) );
        XQueryService xQueryService = new XQueryService();
        List<String> jobsToRestart = new ArrayList<>();
        try{
            if (jobIds.length > 0) {
                for (String jobId : jobIds) {
                    String[] jobData = GDEMServices.getDaoService().getXQJobDao().getXQJobData(jobId);
                    if (jobData == null || jobData.length < 3) {
                        continue;
                    }
                    // check if job is running
                    JobKey qJob = new JobKey(jobId, "XQueryJob");
                    if ( "2".equals(jobData[3]) ) {
                        if ( getQuartzScheduler().checkExists(qJob)) {
                            try {
                                if (getQuartzScheduler().checkExists(qJob))
                                    // try to interrupt running job
                                    getQuartzScheduler().interrupt(qJob);
                                else if (getQuartzHeavyScheduler().checkExists(qJob))
                                    // try to interrupt running job
                                    getQuartzHeavyScheduler().interrupt(qJob);
                            }catch (UnableToInterruptJobException e) {
                                LOGGER.info("Job with ID: " + jobId + " is running and cannot be interrupted and thus cannot be restarted");
                                continue;
                            }
                        }
                        else {
                            continue;
                        }
                    }
                    jobsToRestart.add(jobId);
                }
                //
                jobIds = new String[ jobsToRestart.size() ];
                jobsToRestart.toArray(jobIds );
                // Change the jobs' status
                GDEMServices.getDaoService().getXQJobDao().changeXQJobsStatuses(jobIds, Constants.XQ_RECEIVED);
                LOGGER.info("Jobs restarted: " + Utils.stringArray2String(jobIds, "," ));
                for (String jobId : jobIds) {
                    // and reschedule each job
                    xQueryService.rescheduleJob(jobId);
                    getJobHistoryRepository().save(new JobHistoryEntry(jobId, Constants.XQ_RECEIVED, Utils.getGMTCurrentTimestamp(), null, null, null, null));
                    LOGGER.info("Job with id #" + jobId + " has been inserted in table JOB_HISTORY ");
                }
            }
        }
        catch (Exception e) {
            throw new XMLConvException(e.getMessage());
        }
    }

    /**
     * Delete jobs by id.
     */
    public static void deleteJobs(String[] jobIds) throws XMLConvException {
        LOGGER.info("Request to deleteJobs jobs " + Utils.stringArray2String(jobIds, "," ) );
        try {
            List<String> jobsToDelete = new ArrayList<>();

            if (jobIds.length > 0) {
                try {
                    for (String jobId : jobIds) {
                        String[] jobData = GDEMServices.getDaoService().getXQJobDao().getXQJobData(jobId);
                        if (jobData == null || jobData.length < 3) {
                            continue;
                        }

                        JobKey qJob = new JobKey(jobId, "XQueryJob");
                        if ( "2".equals(jobData[3]) ) {
                            try {

                                if (getQuartzScheduler().checkExists(qJob))
                                // try to interrupt running job
                                    getQuartzScheduler().interrupt(qJob);
                                else if (getQuartzHeavyScheduler().checkExists(qJob))
                                    // try to interrupt running job
                                    getQuartzHeavyScheduler().interrupt(qJob);
                            }catch (UnableToInterruptJobException e) {

                                GDEMServices.getDaoService().getXQJobDao().markDeleted(jobId);

                                LOGGER.info("Job with ID: " + jobId + " is running and cannot be interrupted and thus cannot be deleted");
                                continue;
                            }
                        }

                        jobsToDelete.add(jobId);
                        // delete also result files from file system tmp folder
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
                }
                catch (Exception e) {
                    LOGGER.error("Could not delete job result files!" + e.getMessage());
                }
                jobIds = new String[ jobsToDelete.size() ];
                jobsToDelete.toArray(jobIds );
                GDEMServices.getDaoService().getXQJobDao().endXQJobs(jobIds);
                LOGGER.info("Jobs deleted: " + Utils.stringArray2String(jobIds, "," ));
            }

        } catch (Exception e) {
            throw new XMLConvException(e.getMessage());
        }
    }

    private static JobHistoryRepository getJobHistoryRepository() {
        return (JobHistoryRepository) SpringApplicationContext.getBean("jobHistoryRepository");
    }
}
