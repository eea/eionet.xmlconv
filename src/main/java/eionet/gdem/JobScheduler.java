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

package eionet.gdem;

import eionet.gdem.exceptions.QuartzInitializationException;
import eionet.gdem.logging.Markers;
import eionet.gdem.qa.WQCleanerJob;
import eionet.gdem.web.job.DDTablesCacheUpdater;
import org.apache.commons.lang3.tuple.Pair;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
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

@Component

public class JobScheduler implements InitializingBean {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
    private static  Scheduler QUARTZ_SCHEDULER = null;
    private static  Scheduler QUARTZ_HEAVY_SCHEDULER = null;
    private static final String HEAVY_PROPERTIES_PATH = "quartz-heavy.properties";

    private static  Scheduler QUARTZ_LOCAL_SCHEDULER = null;
    private static final String LOCAL_PROPERTIES_PATH = "local-quartz.properties";
    /** holds the clustered quartz scheduler shared amongst instances*/
 // private final   SchedulerFactory quartzSchedulerFactory;

    private final DataSource quartzDataSource;

    private final java.util.Properties quartzLocalProperties;
    private final java.util.Properties quartzHeavyProperties;


    @Autowired
    public JobScheduler(@Qualifier("quartzDataSource")DataSource quartzDataSource,
                        @Qualifier("quartzLocalProperties") java.util.Properties quartzLocalProperties,
                        @Qualifier("quartzHeavyProperties") java.util.Properties quartzHeavyProperties) throws QuartzInitializationException {
        initQuartzScheduler();
        initLocalQuartzScheduler();
        initQuartzHeavyScheduler();
      //  furtherJobsInitialization();
       this.quartzDataSource = quartzDataSource;
        this.quartzLocalProperties = quartzLocalProperties;
        this.quartzHeavyProperties = quartzHeavyProperties;
    }


   public void init() {
       initQuartzScheduler();
       initLocalQuartzScheduler();
       initQuartzHeavyScheduler();
   }
    private   void initQuartzScheduler()  {
        try {

            SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
            factoryBean.setDataSource(this.quartzDataSource);
            factoryBean.afterPropertiesSet();
            QUARTZ_SCHEDULER = factoryBean.getScheduler();
            QUARTZ_SCHEDULER.start();
        } catch (Exception e) {
          LOGGER.error(e.getMessage());
        }
    }



    private   void initQuartzHeavyScheduler() {
        try {
            SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
            factoryBean.setDataSource(this.quartzDataSource);
            factoryBean.setQuartzProperties(this.quartzHeavyProperties);
            factoryBean.afterPropertiesSet();

            QUARTZ_HEAVY_SCHEDULER = factoryBean.getScheduler();
            QUARTZ_HEAVY_SCHEDULER.start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


  private  synchronized void initLocalQuartzScheduler() {
      try {
          SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
          factoryBean.setDataSource(this.quartzDataSource);
          factoryBean.setQuartzProperties(this.quartzLocalProperties);
          factoryBean.afterPropertiesSet();

          QUARTZ_LOCAL_SCHEDULER = factoryBean.getScheduler();
          QUARTZ_LOCAL_SCHEDULER.start();
      } catch (Exception e) {
          LOGGER.error(e.getMessage());
      }
  }


    public static Scheduler getQuartzScheduler() throws SchedulerException {
        return QUARTZ_SCHEDULER;
    }

    public static Scheduler getQuartzHeavyScheduler() throws SchedulerException {
        return QUARTZ_HEAVY_SCHEDULER;
    }

    private static Pair<Integer, JobDetail>[] intervalJobs;
    /**
     * Schedules interval job locally.
     * @param repeatInterval Repeat interval
     * @param jobDetails Job Details
     * @throws SchedulerException If an error occurs.
     * @throws ParseException If an error occurs.
     */
    public  synchronized void scheduleLocalIntervalJob(int repeatInterval, JobDetail jobDetails) throws SchedulerException,
    ParseException {

        SimpleTrigger trigger =
            newTrigger().withIdentity(jobDetails.getKey().getName(), jobDetails.getKey().getGroup()).startNow()
            .withSchedule(simpleSchedule().withIntervalInSeconds(repeatInterval).repeatForever()).build();

        QUARTZ_LOCAL_SCHEDULER.scheduleJob(jobDetails, trigger);
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

        QUARTZ_SCHEDULER.scheduleJob(jobDetails, trigger);
    }

    /**
     * Schedules interval job.
     * @param repeatInterval Repeat interval
     * @param jobDetails Job Details
     * @throws SchedulerException If an error occurs.
     * @throws ParseException If an error occurs.
     */
    public  synchronized void scheduleIntervalJob(int repeatInterval, JobDetail jobDetails) throws SchedulerException,
    ParseException {

        SimpleTrigger trigger =
            newTrigger().withIdentity(jobDetails.getKey().getName(), jobDetails.getKey().getGroup()).startNow()
            .withSchedule(simpleSchedule().withIntervalInSeconds(repeatInterval).repeatForever()).build();

        QUARTZ_SCHEDULER.scheduleJob(jobDetails, trigger);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
   // @Override
    @PreDestroy
    public void contextDestroyed() {
            try {
                QUARTZ_SCHEDULER.shutdown(false);
                QUARTZ_LOCAL_SCHEDULER.shutdown(false);
                QUARTZ_HEAVY_SCHEDULER.shutdown(false);
                Thread.sleep(1000);
            } catch (SchedulerException e) {
                LOGGER.error("Failed to shutdown the scheduler", e);
            } catch (InterruptedException e) {
                LOGGER.error("Failed to shutdown the scheduler", e);
            }
    }

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent) {@inheritDoc}
     */
    ///@Override

    public void furtherJobsInitialization() {
        intervalJobs
                = new Pair[]{
                      Pair.of(new Integer(7200),
                            newJob(WQCleanerJob.class).withIdentity(WQCleanerJob.class.getSimpleName(),
                            WQCleanerJob.class.getName()).build())};
        // schedule interval jobs
        for (Pair<Integer, JobDetail> job : intervalJobs) {

            try {
                getQuartzHeavyScheduler(); // initialize the heavy scheduler
                scheduleIntervalJob(job.getLeft(), job.getRight());
                LOGGER.debug(job.getRight().getKey().getName() + " scheduled, interval=" + job.getLeft());
            } catch (Exception e) {
                if (!(e instanceof org.quartz.ObjectAlreadyExistsException))  {
                    LOGGER.error(Markers.FATAL, "Error when scheduling " + job.getRight().getKey().getName(), e);
                }
            }
        }
        try {
            // DDTablesCacheUpdater is scheduled locally
            scheduleLocalIntervalJob(3600, newJob(DDTablesCacheUpdater.class).withIdentity(DDTablesCacheUpdater.class.getSimpleName(),
                    DDTablesCacheUpdater.class.getName()).build());
        } catch (Exception e) {
                LOGGER.error(Markers.FATAL, "Error when scheduling DDTablesCacheUpdater", e);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
