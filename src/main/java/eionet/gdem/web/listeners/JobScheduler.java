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

package eionet.gdem.web.listeners;

import eionet.gdem.Properties;
import eionet.gdem.dcm.business.WorkqueueManager;
import eionet.gdem.logging.Markers;
import eionet.gdem.qa.WQCheckerJob;
import eionet.gdem.qa.WQCleanerJob;
import eionet.gdem.qa.WQExecutor;
import eionet.gdem.web.job.DDTablesCacheUpdater;
import org.apache.commons.lang3.tuple.Pair;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.text.ParseException;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * ContextListener for initialising scheduled jobs with quartz.
 *
 * @author Enriko Käsper, TripleDev
 * @author George Sofianos
 */
@SuppressWarnings("unchecked")
public class JobScheduler implements ServletContextListener {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);

    /** */
    private static Scheduler quartzScheduler = null;

    public static Scheduler getQuartzScheduler() throws SchedulerException {
        if (quartzScheduler == null) {
            init();
        }
        return quartzScheduler;
    }

    private static Pair<Integer, JobDetail>[] intervalJobs;

    /**
     * Initialize Job Scheduler
     * @throws SchedulerException If an error occurs.
     */
    private static void init() throws SchedulerException {

        SchedulerFactory schedFact = new StdSchedulerFactory();
        quartzScheduler = schedFact.getScheduler();
        quartzScheduler.start();
    }

    /**
     * Schedules cron job.
     * @param cronExpression Cron expression
     * @param jobDetails Job details
     * @throws SchedulerException If an error occurs.
     * @throws ParseException If an error occurs.
     */
    public static synchronized void scheduleCronJob(String cronExpression, JobDetail jobDetails) throws SchedulerException,
    ParseException {

        Trigger trigger =
            newTrigger().withIdentity(jobDetails.getKey().getName(), jobDetails.getKey().getGroup())
            .withSchedule(cronSchedule(cronExpression)).forJob(jobDetails.getKey()).build();

        if (quartzScheduler == null) {
            init();
        }

        quartzScheduler.scheduleJob(jobDetails, trigger);
    }

    /**
     * Schedules interval job.
     * @param repeatInterval Repeat interval
     * @param jobDetails Job Details
     * @throws SchedulerException If an error occurs.
     * @throws ParseException If an error occurs.
     */
    public static synchronized void scheduleIntervalJob(int repeatInterval, JobDetail jobDetails) throws SchedulerException,
    ParseException {

        SimpleTrigger trigger =
            newTrigger().withIdentity(jobDetails.getKey().getName(), jobDetails.getKey().getGroup()).startNow()
            .withSchedule(simpleSchedule().withIntervalInSeconds(repeatInterval).repeatForever()).build();

        if (quartzScheduler == null) {
            init();
        }

        quartzScheduler.scheduleJob(jobDetails, trigger);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (quartzScheduler != null) {
            try {
                quartzScheduler.shutdown(false);
                Thread.sleep(1000);
            } catch (SchedulerException e) {
                LOGGER.error("Failed proper shutdown of " + quartzScheduler.getClass().getSimpleName(), e);
            } catch (InterruptedException e) {
                LOGGER.error("Failed proper shutdown of " + quartzScheduler.getClass().getSimpleName(), e);
            }
        }
        WQExecutor.getInstance().shutdown();
    }

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent) {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        intervalJobs
                = new Pair[]{
                    Pair.of(new Integer(Properties.wqCheckInterval),
                            newJob(WQCheckerJob.class).withIdentity(WQCheckerJob.class.getSimpleName(),
                            WQCheckerJob.class.getName()).build()),
                    Pair.of(new Integer(Properties.wqCleanInterval),
                            newJob(WQCleanerJob.class).withIdentity(WQCleanerJob.class.getSimpleName(),
                            WQCleanerJob.class.getName()).build()),
                    Pair.of(new Integer(Properties.ddTablesUpdateInterval),
                            newJob(DDTablesCacheUpdater.class).withIdentity(DDTablesCacheUpdater.class.getSimpleName(),
                            DDTablesCacheUpdater.class.getName()).build())};
        // schedule interval jobs
        for (Pair<Integer, JobDetail> job : intervalJobs) {

            try {
                scheduleIntervalJob(job.getLeft(), job.getRight());
                LOGGER.debug(job.getRight().getKey().getName() + " scheduled, interval=" + job.getLeft());
            } catch (Exception e) {
                LOGGER.error(Markers.fatal, "Error when scheduling " + job.getRight().getKey().getName(), e);
            }
        }
        WorkqueueManager.resetActiveJobs();
    }
}
