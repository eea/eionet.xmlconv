package eionet.gdem.jpa.service;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import eionet.gdem.jpa.Entities.JobEntry;
import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.jpa.errors.DatabaseException;
import eionet.gdem.jpa.repositories.JobRepository;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.StatusUtils;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.hosts.IHostDao;
import eionet.gdem.web.spring.schemas.ISchemaDao;
import eionet.gdem.web.spring.workqueue.EntriesForPageObject;
import eionet.gdem.web.spring.workqueue.JobMetadata;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Service("jobService")
public class JobServiceImpl implements JobService {

    JobRepository jobRepository;

    @PersistenceContext
    private EntityManager entityManager;

    QueryHistoryService queryHistoryService;

    private PropertiesService propertiesService;

    private static final String MAX_MS_FOR_PROCESSING_DUPLICATE_SCHEMA_VALIDATION = "maxMsForProcessingDuplicateSchemaValidation";

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    public JobServiceImpl(@Qualifier("jobRepository") JobRepository jobRepository, @Qualifier("queryHistoryServiceImpl") QueryHistoryService queryHistoryService,
                          PropertiesService propertiesService) {
        this.jobRepository = jobRepository;
        this.queryHistoryService = queryHistoryService;
        this.propertiesService = propertiesService;
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
    public JobEntry saveOrUpdate(JobEntry jobEntry) throws DatabaseException {
        try {
            return jobRepository.save(jobEntry);
        } catch (Exception e) {
            LOGGER.error("Database exception when trying to save or update " + jobEntry.getId());
            throw new DatabaseException(e);
        }
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
    public EntriesForPageObject getPagedAndSortedEntries(Integer page, Integer itemsPerPage, String sortBy, Boolean sortDesc, String searchParam, String keyword, String[] searchedStatuses) {
        EntriesForPageObject entriesForPageObject = new EntriesForPageObject();
        Pageable pageRequest = null;
        //paging is zero based
        if(page > 0){
            page--;
        }
        Integer totalNumberOfEntries = 0;
        if(Utils.isNullStr(searchParam) || (!searchParam.equals("statusName") && Utils.isNullStr(keyword) ) || (searchParam.equals("statusName") && searchedStatuses.length == 0)){
            totalNumberOfEntries = getNumberOfTotalJobs();
        }
        else{
            totalNumberOfEntries = getTotalNumberOfSearchedEntries(searchParam, keyword, searchedStatuses);
        }

        if(itemsPerPage < 0){
            //show all results
            itemsPerPage = totalNumberOfEntries;
        }

        String jobEntrySortParameter = getSortParameter(sortBy);
        if(sortDesc){
            pageRequest = new PageRequest(page, itemsPerPage, new Sort(Sort.Direction.DESC, jobEntrySortParameter));
        }
        else{
            pageRequest = new PageRequest(page, itemsPerPage, new Sort(Sort.Direction.ASC, jobEntrySortParameter));
        }
        Page<JobEntry> pagedPage = null;
        if(Utils.isNullStr(searchParam) || (!searchParam.equals("statusName") && Utils.isNullStr(keyword) ) || (searchParam.equals("statusName") && searchedStatuses.length == 0)){
            pagedPage = jobRepository.findAll(pageRequest);
        }
        else{
            pagedPage = getPagedEntriesWithKeyword(pageRequest, searchParam, keyword, searchedStatuses, sortBy, sortDesc);
        }
        entriesForPageObject.setTotalNumberOfJobEntries(totalNumberOfEntries);

        if(pagedPage != null && !Utils.isNullList(pagedPage.getContent())){
            entriesForPageObject.setJobEntriesForPage(pagedPage.getContent());
        }
        else{
            entriesForPageObject.setJobEntriesForPage(new ArrayList<JobEntry>());
        }
        return entriesForPageObject;
    }

    private Integer getTotalNumberOfSearchedEntries(String searchParam, String keyword, String[] searchedStatuses){
        Long totalNumberOfEntries = 0L;
        LOGGER.info("Searching in T_XQJOBS table for total number of entries for keyword " + keyword + " and parameter " + searchParam);
        if (searchParam.equals("jobId")){
            try{
                int number = Integer.parseInt(keyword);
                totalNumberOfEntries = jobRepository.countById(number);
            }
            catch (NumberFormatException ex){
                LOGGER.error("Could not transform keyword " + keyword + " to integer. Exception message: " + ex.getMessage());
            }
        } else if (searchParam.equals("url")){
            totalNumberOfEntries = jobRepository.countByUrlContainingIgnoreCase(keyword);
        } else if (searchParam.equals("script_file")){
            totalNumberOfEntries = jobRepository.countByFileContainingIgnoreCase(keyword);
        } else if (searchParam.equals("result_file")){
            Set<Integer> statuses;
            if (StringUtils.containsIgnoreCase("*** Not ready ***", keyword)){
                statuses = new HashSet<Integer>(Arrays.asList(Constants.XQ_RECEIVED, Constants.XQ_DOWNLOADING_SRC, Constants.XQ_PROCESSING,
                        Constants.XQ_INTERRUPTED, Constants.CANCELLED_BY_USER, Constants.DELETED));
                totalNumberOfEntries = jobRepository.countByNStatusIn(statuses);
            }
            else if(StringUtils.containsIgnoreCase("Job Result", keyword)){
                statuses = new HashSet<Integer>(Arrays.asList(Constants.XQ_READY, Constants.XQ_FATAL_ERR, Constants.XQ_LIGHT_ERR));
                totalNumberOfEntries = jobRepository.countByNStatusIn(statuses);
            }
        }
        else if (searchParam.equals("statusName")){
            //use searchedStatuses
            Set<Integer> statusIds = StatusUtils.getStatusIdsBasedOnStatusNames(searchedStatuses);
            totalNumberOfEntries = jobRepository.countByNStatusIn(statusIds);
        } else if (searchParam.equals("instance")){
            totalNumberOfEntries = jobRepository.countByInstanceContainingIgnoreCase(keyword);
        }  else if (searchParam.equals("jobType")){
            totalNumberOfEntries = jobRepository.countByJobTypeContainingIgnoreCase(keyword);
        } else if (searchParam.equals("jobExecutorName")){
            totalNumberOfEntries = jobRepository.countByJobExecutorNameContainingIgnoreCase(keyword);
        } else if (searchParam.equals("timestamp")){
            String likeKeyword = "%" + keyword + "%";
            totalNumberOfEntries = jobRepository.countByTimestampContaining(likeKeyword);
        }
        LOGGER.info("For keyword " + keyword + " and parameter " + searchParam + " there are " + totalNumberOfEntries + " entries in the database.");
        return Math.toIntExact(totalNumberOfEntries);
    }

    private Page<JobEntry> getPagedEntriesWithKeyword(Pageable pageRequest, String searchParam, String keyword, String[] searchedStatuses, String sortBy, Boolean sortDesc){
        Page<JobEntry> pagedPage = null;
        LOGGER.info("Searching in T_XQJOBS table for keyword " + keyword + " for parameter " + searchParam);
        if (searchParam.equals("jobId")){
            try{
                int number = Integer.parseInt(keyword);
                pagedPage = jobRepository.findById(number, pageRequest);
            }
            catch (NumberFormatException ex){
                LOGGER.error("Could not transform keyword " + keyword + " to integer. Exception message: " + ex.getMessage());
            }
        } else if (searchParam.equals("url")){
            pagedPage = jobRepository.findByUrlContainingIgnoreCase(keyword, pageRequest);
        } else if (searchParam.equals("script_file")){
            pagedPage = jobRepository.findByFileContainingIgnoreCase(keyword, pageRequest);
        } else if (searchParam.equals("result_file")){
            Set<Integer> statuses;
            if (StringUtils.containsIgnoreCase("*** Not ready ***", keyword)){
                statuses = new HashSet<Integer>(Arrays.asList(Constants.XQ_RECEIVED, Constants.XQ_DOWNLOADING_SRC, Constants.XQ_PROCESSING,
                        Constants.XQ_INTERRUPTED, Constants.CANCELLED_BY_USER, Constants.DELETED));
                pagedPage = jobRepository.findByNStatusIn(statuses, pageRequest);
            }
            else if(StringUtils.containsIgnoreCase("Job Result", keyword)){
                statuses = new HashSet<Integer>(Arrays.asList(Constants.XQ_READY, Constants.XQ_FATAL_ERR, Constants.XQ_LIGHT_ERR));
                pagedPage = jobRepository.findByNStatusIn(statuses, pageRequest);
            }
        }
        else if (searchParam.equals("statusName")){
            //use searchedStatuses
            Set<Integer> statusIds = StatusUtils.getStatusIdsBasedOnStatusNames(searchedStatuses);
            pagedPage = jobRepository.findByNStatusIn(statusIds, pageRequest);
        } else if (searchParam.equals("instance")){
            pagedPage = jobRepository.findByInstanceContainingIgnoreCase(keyword, pageRequest);
        }  else if (searchParam.equals("jobType")){
            pagedPage = jobRepository.findByJobTypeContainingIgnoreCase(keyword, pageRequest);
        } else if (searchParam.equals("jobExecutorName")){
            pagedPage = jobRepository.findByJobExecutorNameContainingIgnoreCase(keyword, pageRequest);
        }
        else if (searchParam.equals("timestamp")){
            String likeKeyword = "%" + keyword + "%";
            List<JobEntry> entriesOfTimestampContaining = getJobEntriesBasedOnTimestampContaining(likeKeyword, pageRequest.getPageSize(), pageRequest.getOffset(),
                    sortBy, sortDesc);
            pagedPage = new PageImpl<>(entriesOfTimestampContaining);
        }
        return pagedPage;
    }

    private List<JobEntry> getJobEntriesBasedOnTimestampContaining(String keyword, Integer limit, Integer offset, String sortParameter, Boolean sortDesc){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JobEntry> criteriaQuery = criteriaBuilder.createQuery(JobEntry.class);
        Root<JobEntry> itemRoot = criteriaQuery.from(JobEntry.class);
        //add sorting parameters
        if(sortParameter.equals("url") || sortParameter.equals("timestamp") || sortParameter.equals("instance") || sortParameter.equals("jobType") || sortParameter.equals("jobExecutorName")){
            if(sortDesc){
                criteriaQuery.orderBy(criteriaBuilder.desc(itemRoot.get(sortParameter)));
            }
            else{
                criteriaQuery.orderBy(criteriaBuilder.asc(itemRoot.get(sortParameter)));
            }
        } else if(sortParameter.equals("jobId")){
            if(sortDesc){
                criteriaQuery.orderBy(criteriaBuilder.desc(itemRoot.get("id")));
            }
            else{
                criteriaQuery.orderBy(criteriaBuilder.asc(itemRoot.get("id")));
            }
        }else if(sortParameter.equals("script_file")){
            if(sortDesc){
                criteriaQuery.orderBy(criteriaBuilder.desc(itemRoot.get("file")));
            }
            else{
                criteriaQuery.orderBy(criteriaBuilder.asc(itemRoot.get("file")));
            }
        }else if(sortParameter.equals("statusName")){
            if(sortDesc){
                criteriaQuery.orderBy(criteriaBuilder.desc(itemRoot.get("nStatus")));
            }
            else{
                criteriaQuery.orderBy(criteriaBuilder.asc(itemRoot.get("nStatus")));
            }
        }else if(sortParameter.equals("durationInProgress")){
            if(sortDesc){
                criteriaQuery.orderBy(criteriaBuilder.desc(itemRoot.get("duration")));
            }
            else{
                criteriaQuery.orderBy(criteriaBuilder.asc(itemRoot.get("duration")));
            }
        }
        else{
            LOGGER.error("Received invalid sort parameter for query in T_XQJOBS based on timestamp. Sort parameter is: " + sortParameter);
        }

        Predicate predicateForTimestamp = criteriaBuilder.like(itemRoot.get("timestamp").as(String.class), keyword);
        criteriaQuery.where(predicateForTimestamp);
        List<JobEntry> items = entityManager.createQuery(criteriaQuery).setFirstResult(offset).setMaxResults(limit).getResultList();
        return items;
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
            job.setFileName(entry.getFile());
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
                job.setResult_file("/tmp/" + entry.getResultFile().substring(entry.getResultFile().lastIndexOf(File.separatorChar) + 1));
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
            job.setConverters_graylog_url(Properties.CONVERTERS_GRAYLOG + job.getJobId() + "&from=" + job.getFrom_date() + ".000Z&to=" + job.getTo_date() + ".000Z");
            job.setJob_executor_graylog_url(Properties.JOB_EXECUTOR_GRAYLOG + job.getJobId() + "&from=" + job.getFrom_date() + ".000Z&to=" + job.getTo_date() + ".000Z");

            jobsList.add(job);

        }
        return jobsList;
    }

    @Override
    public void deleteJobById(Integer jobId){
        jobRepository.delete(jobId);
        LOGGER.info("Deleting job with id " + jobId);
    }


    @Override
    public void changeJobStatusAndTimestampByStatus(Integer currentStatus, Integer newStatus) {
        jobRepository.changeJobStatusAndTimestampByStatus(currentStatus, new Timestamp(new Date().getTime()) ,newStatus);
        LOGGER.info("Changed statuses to " + newStatus + " for all entries with old status " + currentStatus);
    }

    @Override
    public void updateJobDurationsByIds(Map<String, Long> jobHashmap){
        for (Map.Entry<String, Long> entry : jobHashmap.entrySet()) {
            String jobId = entry.getKey();
            Long duration = entry.getValue();
            jobRepository.updateDurationByJobId(duration, Integer.valueOf(jobId));

        }
    }


    @Override
    public JobEntry findByDuplicateIdentifierAndStatus(String duplicateIdentifier, Set<Integer> statuses){
        JobEntry jobEntry = jobRepository.findByDuplicateIdentifierAndStatus(duplicateIdentifier, statuses);
        return jobEntry;
    }

    @Override
    public String findDuplicateNotCompletedJob(String fileUrl, String scriptId){
        String duplicateIdentifier = getDuplicateIdentifier(fileUrl, scriptId);
        if(Utils.isNullStr(duplicateIdentifier)){
            return null;
        }
        JobEntry jobEntry = findByDuplicateIdentifierAndStatus(duplicateIdentifier, new HashSet<Integer>(Arrays.asList(Constants.XQ_RECEIVED, Constants.XQ_DOWNLOADING_SRC, Constants.XQ_PROCESSING)));
        if(jobEntry != null){
            //found duplicate entry
            LOGGER.info("Found job with id " + jobEntry.getId() + " that has duplicate identifier " + duplicateIdentifier + " and status " + jobEntry.getnStatus());
            if(scriptId.equals("-1")){
                //check processing time
                Long currentMs = new Date().getTime();
                Long entryTimestampMs = jobEntry.getTimestamp().getTime();
                Long durationOfJob = currentMs - entryTimestampMs;
                Long maxDuration = Properties.maxMsForProcessingDuplicateSchemaValidation;
                try {
                    Long maxDurationDbValue = (Long) propertiesService.getValue(MAX_MS_FOR_PROCESSING_DUPLICATE_SCHEMA_VALIDATION);
                    if (maxDurationDbValue != null) {
                        maxDuration = maxDurationDbValue;
                    }
                } catch (DatabaseException e) {
                    LOGGER.error("Error when retrieving value for maxMsForProcessingDuplicateSchemaValidation in PROPERTIES table. Exception message: " + e.getMessage());
                }
                if(durationOfJob >= maxDuration){
                    //this job should not be considered as duplicate because it has taken too long and has probably failed.
                    return null;
                }

            }
            return jobEntry.getId().toString();
        }
        else{
            LOGGER.info("No duplicate processing job found for fileUrl " + fileUrl + " and script " + scriptId);
        }
        return null;
    }

    protected String getHashFromCdrBdrForFile(String fileUrl) throws IOException, SQLException {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String hash = null;
            String requestUrl = fileUrl;
            String lastChar = fileUrl.substring(fileUrl.length() - 1);
            if (!lastChar.equals("/")) {
                requestUrl += "/";
            }
            requestUrl += "file_metadata";
            HttpGet httpget = new HttpGet(requestUrl);
            URL url = new URL(requestUrl);
            String authorization = getAuthorizationString(url.getHost());
            if (!Utils.isNullStr(authorization)) {
                httpget.addHeader(HttpHeaders.AUTHORIZATION, authorization);
            }
            CloseableHttpResponse response = httpClient.execute(httpget);
            LOGGER.info("When retrieving hash from cdr/bdr for file " + fileUrl + " got response code " + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                JSONObject object = new JSONObject((responseString));
                hash = (String) object.get("hash");
            }
            return hash;
        }catch(Exception e){
            LOGGER.error("Could not retrieve hash from cdr/bdr for file " + fileUrl + ". Exception message is: " + e.getMessage());
            return null;
        }

    }

    private String getAuthorizationString(String host) throws SQLException {
        //get credentials for host
        IHostDao hostDao = GDEMServices.getDaoService().getHostDao();
        Vector v = hostDao.getHosts(host);

        if (v != null && v.size() > 0) {
            Hashtable h = (Hashtable) v.get(0);
            String user = (String) h.get("user_name");
            String pwd = (String) h.get("pwd");
            String userpass = user + ":" + pwd;

            //add basic authorization
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
            return basicAuth;
        }
        return null;
    }

    @Override
    public String getDuplicateIdentifier(String fileUrl, String scriptId){
        if(Utils.isNullStr(fileUrl) || Utils.isNullStr(scriptId)){
            return null;
        }
        String hash = null;
        if(!fileUrl.endsWith("/xml")) {
            try {
                hash = getHashFromCdrBdrForFile(fileUrl);
            } catch (IOException e) {
                LOGGER.error("Could not retrieve hash from cdr/bdr for file " + fileUrl + ". Exception message: " + e.getMessage());
                return null;
            } catch (SQLException e) {
                LOGGER.error("Could not retrieve hash from cdr/bdr for file " + fileUrl + ". Exception message: " + e.getMessage());
                return null;
            }
            if (hash == null) {
                return null;
            }
        }

        QueryHistoryEntry queryHistoryEntry = queryHistoryService.findLastEntryByQueryId(Integer.valueOf(scriptId));
        String scriptDateLastChanged = null;
        if(queryHistoryEntry != null && queryHistoryEntry.getDateModified() != null){
            scriptDateLastChanged = queryHistoryEntry.getDateModified().toString();
        }
        String duplicateIdentifier = Utils.constructDuplicateIdentifierForJob(fileUrl, hash, scriptId, scriptDateLastChanged);
        return duplicateIdentifier;
    }

    @Override
    public List<JobEntry> findQueuedJobs() {
        return jobRepository.findQueuedJobs();
    }

}