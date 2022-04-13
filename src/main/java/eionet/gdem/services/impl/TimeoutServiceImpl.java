package eionet.gdem.services.impl;

import eionet.gdem.Properties;
import eionet.gdem.models.TimeoutEntity;
import eionet.gdem.services.TimeoutService;
import org.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeoutServiceImpl implements TimeoutService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutServiceImpl.class);

    @Autowired
    public TimeoutServiceImpl() {
    }

    @Override
    public List<TimeoutEntity> getAllTimeoutProperties(){
        List<TimeoutEntity> timeoutEntities = new ArrayList<>();
        TimeoutEntity jobsOnDemandLimitBeforeTimeout = new TimeoutEntity("jobsOnDemandLimitBeforeTimeout", String.valueOf(Properties.jobsOnDemandLimitBeforeTimeout) + " (in ms)", "Time limit in case of xmlrpc/rest runScript for a long running job. If this limit is exceeded we stop asking the job status");
        timeoutEntities.add(jobsOnDemandLimitBeforeTimeout);
        TimeoutEntity jobsOnDemandUITimeout = new TimeoutEntity("jobsOnDemandUITimeout", String.valueOf(Properties.jobsOnDemandUITimeout) + " (in ms)", "Time limit in case of GUI Qa Sandbox runScript for a long running job. If this limit is exceeded we stop asking the job status");
        timeoutEntities.add(jobsOnDemandUITimeout);
        TimeoutEntity timeoutToWaitForEmptyFileForOnDemandJobs = new TimeoutEntity("timeoutToWaitForEmptyFileForOnDemandJobs", String.valueOf(Properties.timeoutToWaitForEmptyFileForOnDemandJobs) + " (in ms)", "Timeout for on demand jobs to get not empty html file");
        timeoutEntities.add(timeoutToWaitForEmptyFileForOnDemandJobs);
        TimeoutEntity longRunningJobThreshold = new TimeoutEntity("longRunningJobThreshold", String.valueOf(Properties.longRunningJobThreshold) + " (in ms)", "Threshold for long running jobs");
        timeoutEntities.add(longRunningJobThreshold);
        TimeoutEntity maxSchemaExecutionTime = new TimeoutEntity("maxSchemaExecutionTime", String.valueOf(Properties.maxSchemaExecutionTime) + " (in ms)", "Max execution time of qa scripts");
        timeoutEntities.add(maxSchemaExecutionTime);
        TimeoutEntity maxSchemaExecutionTimeLimit = new TimeoutEntity("maxSchemaExecutionTimeLimit", String.valueOf(Properties.maxSchemaExecutionTimeLimit) + " (in ms)", "Limit for max execution time of qa scripts in ms");
        timeoutEntities.add(maxSchemaExecutionTimeLimit);
        TimeoutEntity wqJobMaxAge = new TimeoutEntity("wqJobMaxAge", String.valueOf(Properties.wqJobMaxAge) + " (in hours)", "The number of hours a finished or cancelled job will remain in the workqueue");
        timeoutEntities.add(wqJobMaxAge);
        TimeoutEntity interruptingJobsInterval = new TimeoutEntity("interruptingJobsInterval", String.valueOf(Properties.interruptingJobsInterval) + " (in ms)", "Interval of checking whether running jobs duration has exceeded schema's maxExecutionTime");
        timeoutEntities.add(interruptingJobsInterval);
        TimeoutEntity consumerTimeout = new TimeoutEntity("consumerTimeout", "90000000 (in ms)", "Rabbitmq consumer_timeout which dictates how much time the workers have, to give any job result (and in rabbitmq concept, acknowledge the message they took)");
        timeoutEntities.add(consumerTimeout);
        TimeoutEntity qaTimeout = new TimeoutEntity("qaTimeout", String.valueOf(Properties.qaTimeout) + " (in ms)", "External QA engine (shell program) timeout");
        timeoutEntities.add(qaTimeout);
        TimeoutEntity proxyTimeout = new TimeoutEntity("proxyTimeout", " (in ms)", "Proxy Server(rancher) timeouts for /runQaScript");
        timeoutEntities.add(proxyTimeout);


        //call fme endpoint and retrieve jsonArray
        try {
            String url = Properties.jobExecutorRequestsUrl + Properties.jobExecutorTimeoutRetrieveEndpoint;
            LOGGER.error("Calling " + url + " url to retrieve properties from Job Executor");
            HttpGet request = new HttpGet(url);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = null;

            response = httpClient.execute(request);

            String jsonString = EntityUtils.toString(response.getEntity());
            JSONObject jobExecutorPropertiesJson = new JSONObject(jsonString);

            TimeoutEntity fmeTimeout = new TimeoutEntity("fmeTimeout (jobExecutor property)", jobExecutorPropertiesJson.get("fmeTimeout") + " (in ms)", "Timeout the system will be waiting to retry a request to fme");
            timeoutEntities.add(fmeTimeout);
            TimeoutEntity fmeSocketTimeout = new TimeoutEntity("fmeSocketTimeout (jobExecutor property)", jobExecutorPropertiesJson.get("fmeSocketTimeout") + " (in ms)", "Timeout the system will be waiting for a FME response");
            timeoutEntities.add(fmeSocketTimeout);
            TimeoutEntity fmeRetryHours = new TimeoutEntity("fmeRetryHours (jobExecutor property)", jobExecutorPropertiesJson.get("fmeRetryHours") + " (in hours)", "The number of hours that we will retry polling for fme job status");
            timeoutEntities.add(fmeRetryHours);

            TimeoutEntity httpConnectTimeout = new TimeoutEntity("httpConnectTimeout (jobExecutor property)", jobExecutorPropertiesJson.get("httpConnectTimeout") + " (in ms)", "The time to establish the connection with the remote host");
            timeoutEntities.add(httpConnectTimeout);
            TimeoutEntity responseTimeoutMs = new TimeoutEntity("responseTimeoutMs (jobExecutor property)", jobExecutorPropertiesJson.get("responseTimeoutMs") + " (in ms)", "Milliseconds that the thread will sleep for before sending a response message to converters.");
            timeoutEntities.add(responseTimeoutMs);

        } catch (Exception e) {
            LOGGER.error("Could not retrieve timeout properties from Job Executor. Exception message: " + e.getMessage());
        }


        return timeoutEntities;
    }
}
