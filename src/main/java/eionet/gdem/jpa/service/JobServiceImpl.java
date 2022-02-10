package eionet.gdem.jpa.service;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.schemas.ISchemaDao;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import eionet.gdem.web.spring.workqueue.JobMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("jobService")
public class JobServiceImpl implements JobService {

    JobRepository jobRepository;

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    public JobServiceImpl(@Qualifier("jobRepository") JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void changeNStatus(Integer jobId, Integer status) throws DatabaseException {
        try {
            jobRepository.updateJobNStatus(status, Properties.getHostname(), new Timestamp(new Date().getTime()), jobId);
            LOGGER.info("### Job with id=" + jobId + " has changed status to " + status + ".");
        } catch (Exception e) {
            LOGGER.error("Database exception when changing status of job with id " + jobId + ", " + e.toString());
            throw new DatabaseException(e);
        }
    }

    @Override
    public JobEntry findById(Integer id) throws DatabaseException {
        JobEntry jobEntry = null;
        try {
            jobEntry = jobRepository.findById(id);
        } catch (Exception e) {
            LOGGER.info("Database exception during retrieval of job with id " + id);
            throw new DatabaseException(e);
        }
        return jobEntry;
    }

    @Override
    public List<JobEntry> findByIntSchedulingStatus(InternalSchedulingStatus intSchedulingStatus) {
        return jobRepository.findByIntSchedulingStatus(intSchedulingStatus);
    }

    @Override
    public List<JobEntry> findByIntSchedulingStatusAndIsHeavy(InternalSchedulingStatus intSchedulingStatus, boolean isHeavy) {
        return jobRepository.findByIntSchedulingStatusAndIsHeavy(intSchedulingStatus, isHeavy);
    }

    @Override
    public List<JobEntry> findProcessingJobs() {
        return jobRepository.findProcessingJobs();
    }

    @Override
    public JobEntry saveOrUpdate(JobEntry jobEntry) {
        return jobRepository.save(jobEntry);
    }

    @Override
    public Integer getRetryCounter(Integer jobId) throws DatabaseException {
        Integer result;
        try {
            result = jobRepository.getRetryCounter(jobId);
        } catch (Exception e) {
            LOGGER.error("Database exception while retrieving retryCounter for job with id " + jobId);
            throw new DatabaseException(e);
        }
        return result;
    }

    @Override
    public List<JobMetadata> retrieveAllJobsWithMetadata() throws SQLException {
        List<JobMetadata> jobsList = new ArrayList<>();

        String[][] list = null;
        try {
            IXQJobDao jobDao = GDEMServices.getDaoService().getXQJobDao();
            list = jobDao.getJobData();
        } catch (Exception e) {
            LOGGER.error("Could not retrieve jobs from T_XQJOBS table. Exception message: " + e.getMessage());
            throw e;
        }
        String tmpFolder = Constants.TMP_FOLDER;
        String queriesFolder = Constants.QUERIES_FOLDER;

        IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
        ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
        for (int i = 0; i < list.length; i++) {
            JobMetadata job = new JobMetadata();
            String jobId = list[i][0];
            String url = list[i][1];
            String xqLongFileName = list[i][2];
            String xqFile = list[i][2].substring(list[i][2].lastIndexOf(File.separatorChar) + 1);
            String resultFile = list[i][3].substring(list[i][3].lastIndexOf(File.separatorChar) + 1);
            int status = Integer.parseInt(list[i][4]);
            String timeStamp = list[i][5];
            String xqStringID = list[i][6];
            String instance = list[i][7];
            String durationMs = list[i][8];
            String jobType = list[i][9];
            String jobExecutorName = list[i][10];

            job.setJobId(jobId);
            job.setFileName(xqLongFileName);
            job.setScript_file(xqFile);
            job.setStatus(status);
            job.setTimestamp(timeStamp);
            job.setScriptId(xqStringID);
            job.setInstance(instance);
            job.setJobType(jobType);
            job.setJobExecutorName(jobExecutorName);
            int xqID = 0;
            String scriptType = "";
            try {
                xqID = Integer.parseInt(xqStringID);
                java.util.HashMap query = queryDao.getQueryInfo(xqStringID);
                if (query != null) {
                    scriptType = (String) query.get("script_type");
                }
            } catch (NumberFormatException n) {
                xqID = 0;
            } catch (Exception e) {
                LOGGER.error("Error when retrieving script information for script " + xqStringID + " Exception message: " + e.getMessage());
            }
            job.setScriptType(scriptType);

            String xqFileURL = "";
            String xqText = "Show script";
            if (xqID == Constants.JOB_VALIDATION) {
                xqText = "Show XML Schema";
                xqFileURL = xqLongFileName;
            } else if (xqID == Constants.JOB_FROMSTRING) {
                xqFileURL = tmpFolder + xqFile;
            } else {
                xqFileURL = queriesFolder + xqFile;
            }

            if (status == Constants.XQ_RECEIVED || status == Constants.XQ_DOWNLOADING_SRC || status == Constants.XQ_PROCESSING ||
                    status == Constants.XQ_INTERRUPTED || status == Constants.CANCELLED_BY_USER || status == Constants.DELETED) {
                resultFile = null;
            }
            else{
                resultFile = Properties.gdemURL + "/tmp/" + resultFile;
            }
            job.setResult_file(resultFile);

            String statusName = "-- Unknown --";

            if (status == Constants.XQ_RECEIVED)
                statusName = "JOB RECEIVED";
            if (status == Constants.XQ_DOWNLOADING_SRC)
                statusName = "DOWNLOADING SOURCE";
            if (status == Constants.XQ_PROCESSING)
                statusName = "PROCESSING";
            if (status == Constants.XQ_READY)
                statusName = "READY";
            if (status == Constants.XQ_FATAL_ERR)
                statusName = "FATAL ERROR";
            if (status == Constants.XQ_LIGHT_ERR)
                statusName = "RECOVERABLE ERROR";
            if (status == Constants.XQ_INTERRUPTED)
                statusName = "INTERRUPTED";
            if (status == Constants.CANCELLED_BY_USER)
                statusName = "CANCELLED BY USER";
            if (status == Constants.DELETED)
                statusName = "DELETED";

            job.setStatusName(statusName);
            if (url.indexOf(Constants.GETSOURCE_URL) > 0 && url.indexOf(Constants.SOURCE_URL_PARAM) > 0) {
                int idx = url.indexOf(Constants.SOURCE_URL_PARAM);
                url = url.substring(idx + Constants.SOURCE_URL_PARAM.length() + 1);
            }
            job.setUrl(url);
            //TODO remove this ?
            //String urlName = (url.length() > Constants.URL_TEXT_LEN ? url.substring(0, Constants.URL_TEXT_LEN) + "..." : url);
            job.setUrlName(url);

            //Set duration of job id status is in PROCESSING
            if (durationMs != null) {
                Long duration = Long.parseLong(durationMs);
                job.setDurationInProgress(Utils.createFormatForMs(duration));
            }

            String schemaId = null;
            if(xqStringID.equals("-1")){
                //schema validation
                try {
                    schemaId = schemaDao.getSchemaID(job.getFileName());
                } catch (SQLException e) {
                    LOGGER.error("Error when retrieving schema id for schema " + job.getFileName() + " Exception message: " + e.getMessage());
                }
                if(schemaId != null) {
                    job.setScriptId(schemaId);
                }
            }
            /*set up script page url*/
            if(xqFile != null){
                if(xqFile.startsWith("gdem")){
                    job.setScript_url(Properties.gdemURL + "/tmp/" + xqFile);
                }
                else if(xqFile.endsWith(".xsd")){
                    job.setScript_url(Properties.gdemURL + "/schemas/" + schemaId);
                }
                else{
                    job.setScript_url(Properties.gdemURL + "/scripts/" + job.getScriptId());
                }
            }
            jobsList.add(job);
        }
        return jobsList;
    }

}













