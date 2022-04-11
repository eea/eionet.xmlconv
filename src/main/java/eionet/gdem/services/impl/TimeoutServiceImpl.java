package eionet.gdem.services.impl;

import eionet.gdem.Properties;
import eionet.gdem.models.TimeoutEntity;
import eionet.gdem.services.TimeoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TimeoutServiceImpl implements TimeoutService {

    @Autowired
    public TimeoutServiceImpl() {
    }

    @Override
    public List<TimeoutEntity> getAllTimeoutProperties(){
        List<TimeoutEntity> timeoutEntities = new ArrayList<>();
        TimeoutEntity jobsOnDemandLimitBeforeTimeout = new TimeoutEntity("jobsOnDemandLimitBeforeTimeout", String.valueOf(Properties.jobsOnDemandLimitBeforeTimeout), "Time limit in case of xmlrpc/rest runScript for a long running job. If this limit is exceeded we stop asking the job status");
        timeoutEntities.add(jobsOnDemandLimitBeforeTimeout);
        TimeoutEntity jobsOnDemandUITimeout = new TimeoutEntity("jobsOnDemandUITimeout", String.valueOf(Properties.jobsOnDemandUITimeout), "Time limit in case of GUI Qa Sandbox runScript for a long running job. If this limit is exceeded we stop asking the job status");
        timeoutEntities.add(jobsOnDemandUITimeout);
        TimeoutEntity timeoutToWaitForEmptyFileForOnDemandJobs = new TimeoutEntity("timeoutToWaitForEmptyFileForOnDemandJobs", String.valueOf(Properties.timeoutToWaitForEmptyFileForOnDemandJobs), "Timeout in milliseconds for on demand jobs to get not empty html file");
        timeoutEntities.add(timeoutToWaitForEmptyFileForOnDemandJobs);
        return timeoutEntities;
    }
}
