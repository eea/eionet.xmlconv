package eionet.gdem.jpa.service;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.schemas.ISchemaDao;
import eionet.gdem.web.spring.workqueue.JobMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public Integer getNumberOfTotalJobs() {
        return Math.toIntExact(jobRepository.count());
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

    private String getSortParameter(String sortParameter){
        String jobEntrySortParameter = null;
        if (sortParameter.equals("jobId")){
            jobEntrySortParameter = "id";
        } else if (sortParameter.equals("url")){
            jobEntrySortParameter = "url";
        } else if (sortParameter.equals("script_file")){
            jobEntrySortParameter = "file";
        } else if (sortParameter.equals("result_file")){
            jobEntrySortParameter = "resultFile";
        } else if (sortParameter.equals("statusName")){
            jobEntrySortParameter = "nStatus";
        } else if (sortParameter.equals("timestamp")){
            jobEntrySortParameter = "timestamp";
        } else if (sortParameter.equals("instance")){
            jobEntrySortParameter = "instance";
        } else if (sortParameter.equals("durationInProgress")){
            jobEntrySortParameter = "duration";
        } else if (sortParameter.equals("jobType")){
            jobEntrySortParameter = "jobType";
        } else if (sortParameter.equals("jobExecutorName")){
            jobEntrySortParameter = "jobExecutorName";
        }
        return jobEntrySortParameter;
    }

    @Override
    public List<JobEntry> getPagedAndSortedEntries(Integer page, Integer itemsPerPage, String sortBy, Boolean sortDesc, String keyword) {
        Pageable pageRequest = null;
        //paging is zero based
        if(page > 0){
            page--;
        }
        String jobEntrySortParameter = getSortParameter(sortBy);
        if(sortDesc){
            pageRequest = new PageRequest(page, itemsPerPage, new Sort(Sort.Direction.DESC, jobEntrySortParameter));
        }
        else{
            pageRequest = new PageRequest(page, itemsPerPage, new Sort(Sort.Direction.ASC, jobEntrySortParameter));
        }
        Page<JobEntry> pagedPage = null;
        if(Utils.isNullStr(keyword)){
            pagedPage = jobRepository.findAll(pageRequest);
        }
        else{
            pagedPage = null;
        }

        if(pagedPage != null && !Utils.isNullList(pagedPage.getContent())){
            return pagedPage.getContent();
        }
        return new ArrayList<JobEntry>();
    }

    @Override
    public List<JobEntry> findProcessingJobs() {
        return jobRepository.findProcessingJobs();
    }

    @Override
    public List<JobMetadata> getJobsMetadata(List<JobEntry> jobEntries){
        List<JobMetadata> jobsList = new ArrayList<>();

        ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();

        for( JobEntry entry: jobEntries){
            JobMetadata job = new JobMetadata();
            job.setJobId(entry.getId().toString());
            job.setFileName(entry.getSrcFile());
            job.setScript_file(entry.getFile().substring(entry.getFile().lastIndexOf(File.separatorChar) + 1));
            job.setStatus(entry.getnStatus());
            job.setTimestamp(entry.getTimestamp().toString());
            job.setScriptId(entry.getQueryId().toString());
            job.setInstance(entry.getInstance());
            job.setJobType(entry.getJobType());
            job.setJobExecutorName(entry.getJobExecutorName());
            job.setScript_type(entry.getScriptType());
            Integer status = job.getStatus();
            if (status == Constants.XQ_RECEIVED || status == Constants.XQ_DOWNLOADING_SRC || status == Constants.XQ_PROCESSING ||
                    status == Constants.XQ_INTERRUPTED || status == Constants.CANCELLED_BY_USER || status == Constants.DELETED) {
                job.setResult_file(null);
            }
            else{
                job.setResult_file(entry.getResultFile());
            }
            if(entry.getFmeJobId() != null){
                job.setFme_job_url(Properties.FME_JOB_URL + entry.getFmeJobId().toString());
                job.setFme_job_id(entry.getFmeJobId().toString());
            }

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

            String url = entry.getUrl();
            if (url.indexOf(Constants.GETSOURCE_URL) > 0 && url.indexOf(Constants.SOURCE_URL_PARAM) > 0) {
                int idx = url.indexOf(Constants.SOURCE_URL_PARAM);
                url = url.substring(idx + Constants.SOURCE_URL_PARAM.length() + 1);
            }
            job.setUrl(url);
            String urlName = (url.length() > Constants.URL_TEXT_LEN ? url.substring(0, Constants.URL_TEXT_LEN) + "..." : url);
            job.setUrl_name(urlName);

            //Set duration of job id status is in PROCESSING
            if (entry.getDuration() != null) {
                Long duration = Long.parseLong(entry.getDuration().toString());
                job.setDurationInProgress(Utils.createFormatForMs(duration));
            }

            String schemaId = null;
            if(job.getScriptId().equals("-1")) {
                //schema validation
                try {
                    schemaId = schemaDao.getSchemaID(job.getFileName());
                } catch (SQLException e) {
                    LOGGER.error("Error when retrieving schema id for schema " + job.getFileName() + " Exception message: " + e.getMessage());
                }
                if (schemaId != null) {
                    job.setScriptId(schemaId);
                }
            }

            /*set up script page url*/
            if(job.getScript_file() != null){
                if(job.getScript_file().startsWith("gdem")){
                    job.setScript_url(Properties.gdemURL + "/tmp/" + job.getScript_file());
                }
                else if(job.getScript_file().endsWith(".xsd")){
                    job.setScript_url(Properties.gdemURL + "/schemas/" + schemaId);
                }
                else{
                    job.setScript_url(Properties.gdemURL + "/scripts/" + job.getScriptId());
                }
            }

            job.setFrom_date(entry.getTimestamp().toLocalDateTime().minusDays(1).toString());
            job.setTo_date(entry.getTimestamp().toLocalDateTime().plusDays(1).toString());
            job.setJob_executor_graylog_url(Properties.CONVERTERS_GRAYLOG + job.getJobId() + "&from=" + job.getFrom_date() + ".000Z&to=" + job.getTo_date() + ".000Z");
            job.setConverters_graylog_url(Properties.JOB_EXECUTOR_GRAYLOG + job.getJobId() + "&from=" + job.getFrom_date() + ".000Z&to=" + job.getTo_date() + ".000Z");

            jobsList.add(job);

        }
        return jobsList;
    }

}













