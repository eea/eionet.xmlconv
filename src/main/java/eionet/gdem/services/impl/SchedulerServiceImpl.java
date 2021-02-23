package eionet.gdem.services.impl;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.remote.RemoteService;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.qa.IQueryDao;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.SchedulerService;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

import static eionet.gdem.web.listeners.JobScheduler.getQuartzHeavyScheduler;
import static eionet.gdem.web.listeners.JobScheduler.getQuartzScheduler;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Service("schedulerService")
public class SchedulerServiceImpl extends RemoteService implements SchedulerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    private static final long heavyJobThreshhold = Properties.heavyJobThreshhold;
    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();

    @Autowired
    public SchedulerServiceImpl() {
    }

    /**
     *  Schedule a job with quartz
     * @param JobID the id of the job
     */
    @Override
    public void scheduleJob (String JobID, long sizeInBytes, String scriptType ) throws SchedulerException {
        // ** Schedule the job with quartz to execute as soon as possibly.
        // only the job_id is needed for the job to be executed
        // Define an anonymous job
        JobDetail job1 = newJob(eionet.gdem.qa.XQueryJob.class)
                .withIdentity(JobID, "XQueryJob")
                .usingJobData("jobId", JobID )
                .requestRecovery()
                .build();

        // Define a Trigger that will fire "now", and not repeat
        Trigger trigger = newTrigger()
                .startNow()
                .build();

        // Schedule the job
        // Heavy jobs go into a separate scheduler
        if (sizeInBytes > heavyJobThreshhold && ! scriptType.equals( XQScript.SCRIPT_LANG_FME) ) {
            Scheduler quartzScheduler = getQuartzHeavyScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }
        else {
            Scheduler quartzScheduler = getQuartzScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }
    }

    /**
     *  Reschedule a job with quartz
     * @param JobID the id of the job
     */
    @Override
    public void rescheduleJob(String JobID) throws SchedulerException, SQLException, XMLConvException {

        String[] jobData = xqJobDao.getXQJobData(JobID);
        String url = jobData[0];
        if(url.indexOf(Constants.GETSOURCE_URL)>0 && url.indexOf(Constants.SOURCE_URL_PARAM)>0) {
            int idx = url.indexOf(Constants.SOURCE_URL_PARAM);
            url = url.substring(idx + Constants.SOURCE_URL_PARAM.length() + 1);
        }

        String scriptType = jobData[8];

        long sourceSize = HttpFileManager.getSourceURLSize(getTicket(), url, isTrustedMode());

        JobDetail job1 = newJob(eionet.gdem.qa.XQueryJob.class)
                .withIdentity(JobID, "XQueryJob")
                .usingJobData("jobId", JobID )
                .requestRecovery()
                .build();

        // Define a Trigger that will fire "now", and not repeat
        Trigger trigger = newTrigger()
                .startNow()
                .build();

        // Reschedule the job
        // Heavy jobs go into a separate scheduler
        if (sourceSize > heavyJobThreshhold && ! scriptType.equals( XQScript.SCRIPT_LANG_FME ) ) {
            Scheduler quartzScheduler = getQuartzHeavyScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }
        else {
            Scheduler quartzScheduler = getQuartzScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }

    }
}
