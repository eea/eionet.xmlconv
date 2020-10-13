package eionet.gdem.qa;

import eionet.gdem.XMLConvException;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static eionet.gdem.web.listeners.JobScheduler.getQuartzHeavyScheduler;
import static eionet.gdem.web.listeners.JobScheduler.getQuartzScheduler;

/**
 * Interrupts all jobs that their duration is longer than corresponding schema's max execution time.
 *
 */
public class InterruptLongRunningJobsTask implements Job {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(InterruptLongRunningJobsTask.class);
    /** Dao for getting job data. */
    private WorkqueueManager jobsManager = new WorkqueueManager();
    private SchemaManager schemaManager = new SchemaManager();


    @Override
    public void execute(JobExecutionContext paramJobExecutionContext) {
        LOGGER.debug("RUN InterruptLongRunningJobsTask.");
        try {
            List<WorkqueueJob> jobs = jobsManager.getRunningJobs();

            if (jobs != null && jobs.size() > 0) {
                for (WorkqueueJob job : jobs) {
                    if (job.getDuration()==0) {
                        continue;
                    }
                    String schemaUrl = findSchemaFromXml(job.getUrl());
                    if (job.getDuration() > schemaManager.getSchemaMaxExecutionTime(schemaUrl)) {
                        JobKey qJob = new JobKey(job.getJobId(), "XQueryJob");
                        try {
                            if (getScheduler().checkExists(qJob)) {
                                // try to interrupt running job
                                getScheduler().interrupt(qJob);
                            }
                            else if (getHeavyScheduler().checkExists(qJob)) {
                                // try to interrupt running job
                                getHeavyScheduler().interrupt(qJob);
                            }
                        } catch (SchedulerException e) {
                            LOGGER.info("error trying to interrupt job with id: " + job.getJobId());
                            continue;
                        }
                    }
                }
            }
        } catch (DCMException | XMLConvException e) {
            LOGGER.error("Error when running InterruptLongRunningJobsTask: ", e);
        }
    }

    Scheduler getScheduler() throws SchedulerException {
        return getQuartzScheduler();
    }

    Scheduler getHeavyScheduler() throws SchedulerException {
        return getQuartzHeavyScheduler();
    }

    /**
     * Finds schema from XML
     *
     * @param xml XML
     * @return Result
     */
    String findSchemaFromXml(String xml) throws XMLConvException {
        InputAnalyser analyser = new InputAnalyser();
        try {
            analyser.parseXML(xml);
            String schemaOrDTD = analyser.getSchemaOrDTD();
            return schemaOrDTD;
        } catch (Exception e) {
            throw new XMLConvException("Could not extract schema");
        }
    }
}
