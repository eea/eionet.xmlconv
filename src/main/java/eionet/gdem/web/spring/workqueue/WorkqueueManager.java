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

import eionet.gdem.Properties;
import eionet.gdem.*;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.jpa.Entities.*;
import eionet.gdem.jpa.repositories.JobHistoryRepository;
import eionet.gdem.jpa.service.JobExecutorService;
import eionet.gdem.jpa.service.JobService;
import eionet.gdem.qa.QueryService;
import eionet.gdem.rabbitMQ.service.RabbitMQMessageFactory;
import eionet.gdem.rabbitMQ.service.WorkerAndJobStatusHandlerService;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.JobRequestHandlerService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;


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
     * @param usedToken
     *     true for mirrored requests
     * @return Job ID.
     * @throws DCMException If an error occurs.
     */
    public String addQAScriptToWorkqueue(String user, String sourceUrl, String scriptContent, String scriptType, Boolean usedToken)
            throws DCMException {

        if(!usedToken) {
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
        }
        QueryService xqE = new QueryService();
        xqE.setTrustedMode(false);
        try {
            String result = getJobRequestHandlerServiceBean().analyze(sourceUrl, scriptContent, scriptType);
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
     * @param usedToken
     *     true for mirrored requests
     * @return List of job IDs.
     * @throws DCMException If an error occurs.
     */
    public List<String> addSchemaScriptsToWorkqueue(String user, String sourceUrl, String schemaUrl, Boolean usedToken) throws DCMException {

        List<String> result = new ArrayList<String>();
        if(!usedToken) {
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
        }
        QueryService xqE = new QueryService();
        xqE.setTrustedMode(false);
        try {
            HashMap h = new HashMap();
            List<String> files = new ArrayList<>();
            files.add(sourceUrl);
            h.put(schemaUrl, files);
            HashMap<String, String> resultMap = getJobRequestHandlerServiceBean().analyzeMultipleXMLFiles(h, false);
            if (resultMap != null) {
                for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                    result.add(entry.getKey());
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
                JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(entry.getJobName(), Constants.XQ_RECEIVED, new Timestamp(new Date().getTime()), entry.getUrl(), entry.getXqFile(), entry.getResultFile(), entry.getXqType());
                jobHistoryEntry.setDuplicateIdentifier(entry.getDuplicateIdentifier());
                getJobHistoryRepository().save(jobHistoryEntry);
                LOGGER.info("Job with id #" + entry.getJobName() + " has been inserted in table JOB_HISTORY ");
            }
            jobDao.changeJobStatusByStatus(Constants.XQ_PROCESSING, Constants.XQ_RECEIVED);
            List<JobHistoryEntry> entriesProcessing = getJobHistoryRepository().findAllByStatus(Constants.XQ_PROCESSING);
            for(JobHistoryEntry entry: entriesProcessing){
                JobHistoryEntry jobHistoryEntry = new JobHistoryEntry(entry.getJobName(), Constants.XQ_RECEIVED, new Timestamp(new Date().getTime()), entry.getUrl(), entry.getXqFile(), entry.getResultFile(), entry.getXqType());
                jobHistoryEntry.setDuplicateIdentifier(entry.getDuplicateIdentifier());
                getJobHistoryRepository().save(jobHistoryEntry);
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
        QueryService queryService = new QueryService();
        List<String> jobsToRestart = new ArrayList<>();
        try{
            if (jobIds.length > 0) {
                for (String jobId : jobIds) {
                    Integer jobIdInt = Integer.parseInt(jobId);
                    String[] jobData = GDEMServices.getDaoService().getXQJobDao().getXQJobData(jobId);
                    if (jobData == null || jobData.length < 3) {
                        continue;
                    }
                    JobEntry jobEntry = getJobServiceBean().findById(jobIdInt);
                    String status = jobData[3];
                    if(status.equals(String.valueOf(Constants.XQ_PROCESSING))){
                        //make job_executor status failed
                        if (jobEntry.getJobExecutorName()!=null) {
                            JobExecutor jobExecutor = getJobExecutorServiceBean().findByName(jobEntry.getJobExecutorName());
                            jobExecutor.setJobId(jobIdInt).setStatus(SchedulingConstants.WORKER_FAILED).setName(jobEntry.getJobExecutorName());
                            JobExecutorHistory jobExecutorHistory = new JobExecutorHistory(jobEntry.getJobExecutorName(), jobExecutor.getContainerId(), SchedulingConstants.WORKER_FAILED, jobIdInt, new Timestamp(new Date().getTime()), jobExecutor.getHeartBeatQueue());
                            getWorkerAndJobStatusHandlerService().saveOrUpdateJobExecutor(jobExecutor, jobExecutorHistory);

                            InternalSchedulingStatus internalStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_QUEUED);
                            jobEntry.setnStatus(Constants.XQ_PROCESSING).setIntSchedulingStatus(internalStatus).setJobExecutorName(null).setTimestamp(new Timestamp(new Date().getTime()));
                            getWorkerAndJobStatusHandlerService().updateJobAndJobHistoryEntries(jobEntry);
                        }
                    }
                    else{
                        //if the status is not processing, the job should be sent to the queue
                        getRabbitMQMessageFactory().createScriptAndSendMessageToRabbitMQ(jobId);
                    }
                    LOGGER.info("### Job with id=" + jobId + " has been re-sent to the queue.");

                    jobsToRestart.add(jobId);
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
    public static void deleteJobs(String[] jobIds, Boolean cancelled) throws XMLConvException {
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

                        String status = jobData[3];
                        if(status.equals(String.valueOf(Constants.XQ_PROCESSING))){
                            //make job_executor status failed
                            Integer jobIdInt = Integer.parseInt(jobId);
                            if (jobIdInt!=null) {
                                JobEntry jobEntry = getJobServiceBean().findById(jobIdInt);
                                InternalSchedulingStatus internalStatus = new InternalSchedulingStatus(SchedulingConstants.INTERNAL_STATUS_CANCELLED);
                                jobEntry.setnStatus(Constants.DELETED).setIntSchedulingStatus(internalStatus).setTimestamp(new Timestamp(new Date().getTime()));
                                getWorkerAndJobStatusHandlerService().handleCancelledJob(jobEntry, SchedulingConstants.WORKER_FAILED);
                            }
                        }
                        else{
                            //user has already pressed delete for the job And the job has been handled by the If clause above.
                            // A Second delete action, will reach this else statement and delete the job Directly from the tables which may lead to unwanted
                            // behavior in terms of the workers previously executing the job, and their termination, as well as the job removal from rabbitmq queues.
                            if (status.equals(String.valueOf(Constants.DELETED))) {
                                LOGGER.info("Job with id " + jobId + " has already status deleted=" + Constants.DELETED);
                                continue;
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
                            GDEMServices.getDaoService().getXQJobDao().endXQJob(jobId);
                        }
                        LOGGER.info("Deleted job " + jobId);

                    }
                }
                catch (Exception e) {
                    LOGGER.error("Could not delete job result files!" + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new XMLConvException(e.getMessage());
        }
    }

    public List<WorkqueueJob> getRunningJobs() throws DCMException {
        List<WorkqueueJob> jobs = new ArrayList<>();
        try {
            String[][] jobsData = jobDao.getRunningJobs();
            if (jobsData != null && jobsData.length > 0) {
                for (String[] jobData : jobsData) {
                    if (jobData != null) {
                        jobs.add(parseData(jobData));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error getting running workqueue jobs", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return jobs;
    }

    private WorkqueueJob parseData(String[] jobData) {
        WorkqueueJob job = null;
        if (jobData != null) {
            job = new WorkqueueJob();
            job.setJobId((jobData[0] == null) ? "" : jobData[0]);
            job.setUrl((jobData[1] == null) ? "" : jobData[1]);
            job.setDuration((jobData[2] == null) ? 0 : new Long(jobData[2]));
        }
        return job;
    }

    private static JobHistoryRepository getJobHistoryRepository() {
        return (JobHistoryRepository) SpringApplicationContext.getBean("jobHistoryRepository");
    }

    private static JobRequestHandlerService getJobRequestHandlerServiceBean() {
        return (JobRequestHandlerService) SpringApplicationContext.getBean("jobRequestHandlerService");
    }

    private static JobService getJobServiceBean() {
        return (JobService) SpringApplicationContext.getBean("jobService");
    }

    private static JobExecutorService getJobExecutorServiceBean() {
        return (JobExecutorService) SpringApplicationContext.getBean("jobExecutorService");
    }

    private static RabbitMQMessageFactory getRabbitMQMessageFactory() {
        return (RabbitMQMessageFactory) SpringApplicationContext.getBean("rabbitMQMessageFactory");
    }

    private static WorkerAndJobStatusHandlerService getWorkerAndJobStatusHandlerService() {
        return (WorkerAndJobStatusHandlerService) SpringApplicationContext.getBean("workerAndJobStatusHandlerService");
    }
}
