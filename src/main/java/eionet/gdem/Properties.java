/**
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
 * The Original Code is "GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem;

import edu.yale.its.tp.cas.client.filter.CASFilter;
import eionet.propertyplaceholderresolver.CircularReferenceException;
import eionet.propertyplaceholderresolver.ConfigurationPropertyResolver;
import eionet.propertyplaceholderresolver.UnresolvedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Several settings and properties for XMLCONV application.
 *
 */
public class Properties {

    private static ConfigurationPropertyResolver configurationService;
    /**
     * Logger class.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(Properties.class);

    /** Application classes (WEB-INF/classes) path in file system. */
    public static String appHome = null;

    /** App host **/
    public static String appHost = null;
    /** Folder for temporary files. */
    public static String tmpFolder = null;
    /** Tempfile directory for QASandbox */
    public static String tmpfileDir = null;
    /** Folder for XSLs. */
    public static String xslFolder = null;
    /** Folder for OpenDocument helper files. */
    public static String odsFolder = null;
    /** Folder for QA scripts. */
    public static String queriesFolder = null;
    /** Folder for XML Schema files. */
    public static String schemaFolder = null;
    /** Folder for XML files. */
    public static String xmlfileFolder = null;
    /** Parent folder for other user folders. */
    public static String appRootFolder = null;

    /** Catalog path **/
    public static String catalogPath = null;

    /** Conversion service installation. */
    public static final int CONV_SERVICE = 1;
    /** QA service installation. */
    public static final int QA_SERVICE = 2;
    /** Number indicating the type of services are installed. Default is 3 (both Conv and QA). */
    public static int services_installed = 3;

    /** DB URL. */
    public static String dbUrl = null;
    /** DB driver class name. */
    public static String dbDriver = null;
    /** DB connection user name. */
    public static String dbUser = null;
    /** DB connection password. */
    public static String dbPwd = null;

    /** Cache Configuration */
    public static final String CACHE_TEMP_DIR;
    public static final long CACHE_HTTP_SIZE;
    public static final int CACHE_HTTP_EXPIRY;
    public static final int HTTP_CACHE_ENTRIES;
    public static final long HTTP_CACHE_OBJECTSIZE;
    public static final int HTTP_SOCKET_TIMEOUT;
    public static final int HTTP_CONNECT_TIMEOUT;
    public static final int HTTP_MANAGER_TOTAL;
    public static final int HTTP_MANAGER_ROUTE;

    /** File transfering load balancer with extended timeout */
    public static final String HTTP_TRANSFER_LOADBALANCER;

    /** conversion.xml file location, listing all available generated conversions. */
    public static String convFile = null;
    /** XSL folder for generated conversions. */
    public static String metaXSLFolder = null;
    /** Data Dictionary URL, used when generating XSLs. */
    public static String ddURL = null;
    /** XMLCONV app URL. */
    public static String gdemURL = null;
    /** Data Dictionary xml-rpc URL */
    public static String invServUrl = null;
    /** Data Dictionary xml-rpc service name */
    public static String invServName = null;
    /** Content Registry SPARQL endpoint URL. */
    public static String crSparqlEndpoint = null;

    /** Hostname. */
    public static String hostname = null;
    /** Is Rancher Boolean. */
    public static int isRancher = 0;
    /** url for rancher metadata queries. */
    public static String rancherMetadataUrl;

    /** XGawk program executable command. */
    public static String xgawkCommand = null;
    /** timeout for running external QA program in command line in milliseconds, default 120sec. */
    public static long qaTimeout = 120000L;
    /** maximum age of finished workqueue job stored in the queue in HOURS, default 24 hours. */
    public static int wqJobMaxAge = 24;
    /** Maximum size (MB) of XML file sent to manual QA for XML Schema validation. */
    public static int qaValidationXmlUpperLimit = 200;

    /** Date pattern used for displaying date values on UI. */
    public static String dateFormatPattern = "dd MMM yyyy";
    /** Time pattern used for displaying time values on UI. */
    public static String timeFormatPattern = "dd MMM yyyy hh:mm:ss";
    /** Context path to be used in classes */
    public static String contextPath;

    /* UNS properties */
    public static String PROP_UNS_XMLRPC_SERVER_URL = null;
    public static String PROP_UNS_CHANNEL_NAME = null;
    public static String PROP_UNS_SUBSCRIPTIONS_URL = null;
    public static String PROP_UNS_USERNAME = null;
    public static String PROP_UNS_PASSWORD = null;
    public static String PROP_UNS_DISABLED = null;
    public static String PROP_UNS_SUBSCRIBE_FUNC = null;
    public static String PROP_UNS_SEND_NOTIFICATION_FUNC = null;
    public static String PROP_UNS_LONG_RUNNING_JOBS_PREDICATE = null;
    public static String PROP_UNS_EVENTTYPE_PREDICATE = null;
    public static String PROP_UNS_EVENTS_NAMESPACE = null;
    public static final String LONG_RUNNING_JOBS_EVENT = "Found long running jobs";
    public static String PROP_UNS_URL = null;
    public static String PROP_UNS_REST_SEND_NOTIFICATION = null;
    public static String PROP_UNS_REST_USERNAME = null;
    public static String PROP_UNS_REST_PASSWORD = null;
    public static String ALERTS_PREDICATE = null;
    public static String ALERTS_EVENTTYPE_PREDICATE = null;
    public static final String ALERTS_EVENT = "Alerts";
    public static String ALERTS_CHANNEL_NAME;

    public static Long longRunningJobThreshold;

    public static final String SSO_LOGIN_URL;

    public static final String XQUERY_HTTP_ENDPOINTS;

    public static final Long BASEX_XQUERY_TIME_LIMIT;

    public static Long maxSchemaExecutionTime;
    public static Long maxSchemaExecutionTimeLimit;

    public static String jwtAudienceProperty;
    public static String jwtIssuerProperty;
    public static String jwtHeaderProperty;
    public static String jwtHeaderSchemaProperty;
    public static String jwtSecretKey;

    /** interval of checking whether running jobs duration has exceeded schema's maxExecutionTime (in seconds) */
    public static int interruptingJobsInterval;

    public static final String crHost;

    public static final String mockCrUrl;

    public static final boolean enableXqueryCrCallsInterception ;

    public static final String rancherApiUrl;
    public static final String rancherApiAccessKey;
    public static final String rancherApiSecretKey;
    public static final String rancherJobExecutorImageUuid;
    public static final String rancherJobExecutorStackId;
    public static final Integer rancherJobExecutorServiceScale;
    public static final Long rancherJobExecServiceMemory;
    public static final Long rancherJobExecServiceMemoryReservation;
    public static final String rancherLightJobExecServiceId;
    public static final String rancherHeavyJobExecServiceId;
    public static final String rancherSyncFmeJobExecServiceId;
    public static final String rancherAsyncFmeJobExecServiceId;
    public static final boolean enableJobExecRancherScheduledTask;
    public static final Integer maxLightJobExecutorContainersAllowed;
    public static final Integer maxHeavyJobExecutorContainersAllowed;
    public static final Integer maxSyncFmeJobExecutorContainersAllowed;
    public static final Integer maxAsyncFmeJobExecutorContainersAllowed;

    public static String rabbitMQHost;
    public static Integer rabbitMQPort;
    public static String rabbitMQUsername;
    public static String rabbitMQPassword;
    public static String cdrRabbitMQHost;
    public static Integer cdrRabbitMQPort;
    public static String cdrRabbitMQUsername;
    public static String cdrRabbitMQPassword;
    public static String WORKERS_JOBS_QUEUE;
    public static String WORKERS_JOBS_RESULTS_QUEUE;
    public static String WORKERS_STATUS_QUEUE;
    public static String WORKER_HEART_BEAT_RESPONSE_QUEUE;
    public static String XMLCONV_HEART_BEAT_REQUEST_EXCHANGE;
    public static String WORKERS_DEAD_LETTER_QUEUE;
    public static String WORKERS_DEAD_LETTER_EXCHANGE;
    public static String MAIN_XMLCONV_JOBS_EXCHANGE;
    public static String MAIN_WORKERS_EXCHANGE;
    public static String JOBS_ROUTING_KEY;
    public static String JOBS_RESULTS_ROUTING_KEY;
    public static String WORKER_STATUS_ROUTING_KEY;
    public static String WORKER_HEART_BEAT_RESPONSE_ROUTING_KEY;
    public static String WORKERS_DEAD_LETTER_ROUTING_KEY;
    public static String HEAVY_WORKERS_JOBS_QUEUE;
    public static String SYNC_FME_JOBS_QUEUE;
    public static String ASYNC_FME_JOBS_QUEUE;
    public static String  MAIN_XMLCONV_HEAVY_JOBS_EXCHANGE;
    public static String XMLCONV_SYNC_FME_JOBS_EXCHANGE;
    public static String XMLCONV_ASYNC_FME_JOBS_EXCHANGE;
    public static String HEAVY_JOBS_ROUTING_KEY;
    public static String SYNC_FME_JOBS_ROUTING_KEY;
    public static String ASYNC_FME_JOBS_ROUTING_KEY;
    public static String CONVERTERS_GRAYLOG;
    public static String JOB_EXECUTOR_GRAYLOG;
    public static String FME_JOB_URL;
    public static String XMLCONV_HEALTH_QUEUE;
    public static String XMLCONV_HEALTH_EXCHANGE;
    public static String XMLCONV_HEALTH_ROUTING_KEY;
    public static String CDR_DEAD_LETTER_QUEUE;
    public static String CDR_DEAD_LETTER_EXCHANGE;
    public static String CDR_DEAD_LETTER_ROUTING_KEY;
    public static String CDR_REQUEST_QUEUE;

    public static Integer CDR_REQUEST_QUEUE_TTL;
    public static String CDR_REQUEST_EXCHANGE;
    public static String CDR_REQUEST_ROUTING_KEY;
    public static String CDR_RESULTS_QUEUE;
    public static String CDR_RESULTS_EXCHANGE;
    public static String CDR_RESULTS_ROUTING_KEY;

    public static final Long jobsOnDemandLimitBeforeTimeout;
    public static final Long jobsOnDemandUITimeout;

    public static Long maxHeavyRetries;

    public static Long timeoutToWaitForEmptyFileForOnDemandJobs;
    public static Long maxMsToWaitForEmptyFileForOnDemandJobs;

    public static Long maxMsForProcessingDuplicateSchemaValidation;

    public static String fmeUrl;
    public static String fmeUser;
    public static String fmePassword;
    public static String fmeToken;

    public static String rancherContainerMetadataUrl;

    public static String jobExecutorRequestsUrl;
    public static String jobExecutorTimeoutRetrieveEndpoint;

    static {
        configurationService = (ConfigurationPropertyResolver) SpringApplicationContext.getBean("configurationPropertyResolver");
        // filesystem properties
        queriesFolder = getStringProperty("queries.folder");
        xslFolder = getStringProperty("xsl.folder");
        tmpFolder = getStringProperty("tmp.folder");
        tmpfileDir = getStringProperty("tmpfile.dir");

        xmlfileFolder = getStringProperty("xmlfile.folder");
        schemaFolder = getStringProperty("schema.folder");
        appRootFolder = getStringProperty("root.folder");
        catalogPath = getStringProperty("config.catalog");
        appHost = getStringProperty("app.host");
        appHome = getStringProperty("app.home");

        // DB connection settings
        dbDriver = getStringProperty("db.driver");
        dbUrl = getStringProperty("db.url");
        dbUser = getStringProperty("db.user");
        dbPwd = getStringProperty("db.pwd");

        CACHE_TEMP_DIR = getStringProperty("cache.temp.dir");
        CACHE_HTTP_SIZE = getLongProperty("cache.http.size");
        CACHE_HTTP_EXPIRY = getIntProperty("cache.http.expiryinterval");
        HTTP_CACHE_ENTRIES = getIntProperty("http.cache.entries");
        HTTP_CACHE_OBJECTSIZE = getLongProperty("http.cache.objectsize");
        HTTP_SOCKET_TIMEOUT = getIntProperty("http.socket.timeout");
        HTTP_CONNECT_TIMEOUT = getIntProperty("http.connect.timeout");
        HTTP_MANAGER_TOTAL = getIntProperty("http.manager.total");
        HTTP_MANAGER_ROUTE = getIntProperty("http.manager.route");
        HTTP_TRANSFER_LOADBALANCER = getStringProperty("http.transfer.loadbalancer");

        // DCM settings
        ddURL = getStringProperty("dd.url");
        gdemURL = getStringProperty("gdem.url");

        // Remote application URLS
        // settings for incoming services from DD
        invServUrl = getStringProperty("dd.rpc.url");
        invServName = getStringProperty("dd.rpcservice.name");
        // settings for incoming services from Content Registry
        crSparqlEndpoint = getStringProperty("cr.sparql.endpoint");

        // period in hours
        wqJobMaxAge = getIntProperty("wq.job.max.age");
        interruptingJobsInterval = getIntProperty("wq.job.interrupt.interval");
        // default value of the maximum size of XML file sent to ad-hoc QA
        qaValidationXmlUpperLimit = getIntProperty("qa.validation.xml.upper_limit");
        // external QA program timeout
        qaTimeout = getIntProperty("external.qa.timeout");
        // exteranal QA program
        xgawkCommand = getStringProperty("external.qa.command.xgawk");

        dateFormatPattern = getStringProperty("date.format.pattern");
        timeFormatPattern = getStringProperty("time.format.pattern");
        services_installed = getIntProperty("gdem.services");

        hostname = getStringProperty("config.hostname") == null ? "hostname_not_set" : getStringProperty("config.hostname");
        isRancher = getIntProperty("config.isRancher");
        rancherMetadataUrl = getStringProperty("rancher.metadata.url");

        longRunningJobThreshold = getLongProperty("env.long.running.jobs.threshold");

        PROP_UNS_XMLRPC_SERVER_URL = getStringProperty("env.uns.xml.rpc.server.url");
        PROP_UNS_CHANNEL_NAME = getStringProperty("env.uns.channel.name");
        PROP_UNS_SUBSCRIPTIONS_URL = getStringProperty("env.uns.subscriptions.url");
        PROP_UNS_USERNAME = getStringProperty("env.uns.username");
        PROP_UNS_PASSWORD = getStringProperty("env.uns.password");
        PROP_UNS_DISABLED = getStringProperty("env.uns.isDisabled");
        PROP_UNS_SUBSCRIBE_FUNC = getStringProperty("env.uns.make.subsription.function");
        PROP_UNS_SEND_NOTIFICATION_FUNC = getStringProperty("env.uns.send.notification.function");
        PROP_UNS_LONG_RUNNING_JOBS_PREDICATE = getStringProperty("env.uns.long.running.jobs.predicate");
        PROP_UNS_EVENTTYPE_PREDICATE = getStringProperty("env.uns.eventtype.predicate");
        PROP_UNS_EVENTS_NAMESPACE = getStringProperty("env.uns.events-namespace");
        PROP_UNS_URL = getStringProperty("env.uns.url");
        PROP_UNS_REST_SEND_NOTIFICATION = getStringProperty("uns.sendNotification.method");
        PROP_UNS_REST_USERNAME = getStringProperty("uns.rest.username");
        PROP_UNS_REST_PASSWORD = getStringProperty("uns.rest.password");
        ALERTS_PREDICATE = getStringProperty("env.uns.alerts.predicate");
        ALERTS_EVENTTYPE_PREDICATE = getStringProperty("env.uns.alerts.eventtype.predicate");
        ALERTS_CHANNEL_NAME = getStringProperty("env.uns.alerts.channel.name");

        SSO_LOGIN_URL = getStringProperty(CASFilter.LOGIN_INIT_PARAM);

        XQUERY_HTTP_ENDPOINTS = getStringProperty("env.xquery.http.endpoints");

        BASEX_XQUERY_TIME_LIMIT = getLongProperty("env.basex.xquery.timeLimit");

        maxSchemaExecutionTime = getLongProperty("env.schema.maxExecutionTime");
        maxSchemaExecutionTimeLimit = getLongProperty("env.schema.maxExecutionTimeLimit");

        jwtAudienceProperty = getStringProperty("jwt.audience");
        jwtIssuerProperty = getStringProperty("jwt.issuer");
        jwtHeaderProperty = getStringProperty("jwt.header");
        jwtHeaderSchemaProperty = getStringProperty("jwt.header.schema");
        jwtSecretKey = getStringProperty("jwt.secret");

        crHost = getStringProperty("config.cr.host");

        mockCrUrl = getStringProperty("config.cr.mockCrUrl");
        enableXqueryCrCallsInterception =Boolean.parseBoolean(getStringProperty("config.enableXqueryCrCallsInterception"));

        rancherApiUrl = getStringProperty("env.rancher.api.url");
        rancherApiAccessKey = getStringProperty("env.rancher.api.accessKey");
        rancherApiSecretKey = getStringProperty("env.rancher.api.secretKey");
        rancherJobExecutorImageUuid = getStringProperty("env.rancher.api.jobExecutor.imageUuid");
        rancherJobExecutorStackId = getStringProperty("env.rancher.api.jobExecutor.stackId");
        rancherJobExecutorServiceScale = getIntProperty("env.rancher.api.jobExecutor.service.scale");
        rancherJobExecServiceMemory = getLongProperty("env.rancher.api.jobExec.service.memory");
        rancherJobExecServiceMemoryReservation = getLongProperty("env.rancher.api.jobExec.service.memoryReservation");
        rancherLightJobExecServiceId = getStringProperty("env.rancher.api.light.jobExec.service.id");
        rancherHeavyJobExecServiceId = getStringProperty("env.rancher.api.heavy.jobExec.service.id");
        rancherSyncFmeJobExecServiceId = getStringProperty("env.rancher.api.sync.fme.jobExec.service.id");
        rancherAsyncFmeJobExecServiceId = getStringProperty("env.rancher.api.async.fme.jobExec.service.id");
        enableJobExecRancherScheduledTask = Boolean.parseBoolean(getStringProperty("env.enable.jobExecutor.rancher.scheduled.task"));
        maxLightJobExecutorContainersAllowed = getIntProperty("env.max.light.jobExecutor.containers.allowed");
        maxHeavyJobExecutorContainersAllowed = getIntProperty("env.max.heavy.jobExecutor.containers.allowed");
        maxSyncFmeJobExecutorContainersAllowed = getIntProperty("env.max.sync.fme.jobExecutor.containers.allowed");
        maxAsyncFmeJobExecutorContainersAllowed = getIntProperty("env.max.async.fme.jobExecutor.containers.allowed");

        rabbitMQHost = getStringProperty("env.rabbitmq.host");
        rabbitMQPort = getIntProperty("env.rabbitmq.port");
        rabbitMQUsername = getStringProperty("env.rabbitmq.username");
        rabbitMQPassword = getStringProperty("env.rabbitmq.password");
        cdrRabbitMQHost = getStringProperty("env.cdr.rabbitmq.host");
        cdrRabbitMQPort = getIntProperty("env.cdr.rabbitmq.port");
        cdrRabbitMQUsername = getStringProperty("env.cdr.rabbitmq.username");
        cdrRabbitMQPassword = getStringProperty("env.cdr.rabbitmq.password");
        WORKERS_JOBS_QUEUE = getStringProperty("env.rabbitmq.workers.jobs.queue");
        WORKERS_JOBS_RESULTS_QUEUE = getStringProperty("env.rabbitmq.workers.jobs.results.queue");
        WORKERS_STATUS_QUEUE = getStringProperty("env.rabbitmq.workers.status.queue");
        WORKER_HEART_BEAT_RESPONSE_QUEUE = getStringProperty("env.rabbitmq.worker.heartBeat.response.queue");
        XMLCONV_HEART_BEAT_REQUEST_EXCHANGE = getStringProperty("env.rabbitmq.xmlconv.heartBeat.request.exchange");
        MAIN_XMLCONV_JOBS_EXCHANGE = getStringProperty("env.rabbitmq.main.xmlconv.jobs.exchange");
        MAIN_WORKERS_EXCHANGE = getStringProperty("env.rabbitmq.main.workers.exchange");
        JOBS_ROUTING_KEY = getStringProperty("env.rabbitmq.jobs.routingkey");
        JOBS_RESULTS_ROUTING_KEY = getStringProperty("env.rabbitmq.jobs.results.routingkey");
        WORKER_STATUS_ROUTING_KEY = getStringProperty("env.rabbitmq.worker.status.routingkey");
        WORKER_HEART_BEAT_RESPONSE_ROUTING_KEY = getStringProperty("env.rabbitmq.worker.heartBeat.response.routingKey");
        HEAVY_WORKERS_JOBS_QUEUE = getStringProperty("env.rabbitmq.heavy.workers.jobs.queue");
        SYNC_FME_JOBS_QUEUE = getStringProperty("env.rabbitmq.workers.fme.sync.jobs.queue");
        ASYNC_FME_JOBS_QUEUE = getStringProperty("env.rabbitmq.workers.fme.async.jobs.queue");
        MAIN_XMLCONV_HEAVY_JOBS_EXCHANGE = getStringProperty("env.rabbitmq.main.xmlconv.heavy.jobs.exchange");
        XMLCONV_SYNC_FME_JOBS_EXCHANGE = getStringProperty("env.rabbitmq.xmlconv.sync.fme.jobs.exchange");
        XMLCONV_ASYNC_FME_JOBS_EXCHANGE = getStringProperty("env.rabbitmq.xmlconv.async.fme.jobs.exchange");
        HEAVY_JOBS_ROUTING_KEY = getStringProperty("env.rabbitmq.heavy.jobs.routingkey");
        SYNC_FME_JOBS_ROUTING_KEY = getStringProperty("env.rabbitmq.sync.fme.jobs.routingkey");
        ASYNC_FME_JOBS_ROUTING_KEY = getStringProperty("env.rabbitmq.async.fme.jobs.routingkey");

        jobsOnDemandLimitBeforeTimeout = getLongProperty("env.jobs.onDemand.limit.before.time.out");
        jobsOnDemandUITimeout = getLongProperty("env.jobs.onDemand.ui.time.out");

        WORKERS_DEAD_LETTER_QUEUE = getStringProperty("env.rabbitmq.dead.letter.queue");
        WORKERS_DEAD_LETTER_EXCHANGE = getStringProperty("env.rabbitmq.dead.letter.exchange");
        WORKERS_DEAD_LETTER_ROUTING_KEY = getStringProperty("env.rabbitmq.dead.letter.routingKey");

        XMLCONV_HEALTH_QUEUE = getStringProperty("env.rabbitmq.health.queue");
        XMLCONV_HEALTH_EXCHANGE = getStringProperty("env.rabbitmq.health.exchange");
        XMLCONV_HEALTH_ROUTING_KEY = getStringProperty("env.rabbitmq.health.routingKey");

        CDR_REQUEST_QUEUE = getStringProperty("env.rabbitmq.cdr.request.queue");
        CDR_REQUEST_EXCHANGE= getStringProperty("env.rabbitmq.cdr.request.exchange");
        CDR_REQUEST_ROUTING_KEY = getStringProperty("env.rabbitmq.cdr.request.routingKey");
        CDR_REQUEST_QUEUE_TTL = getIntProperty("env.rabbitmq.cdr.request.queue.ttl");

        CDR_RESULTS_QUEUE = getStringProperty("env.rabbitmq.cdr.results.queue");
        CDR_RESULTS_EXCHANGE = getStringProperty("env.rabbitmq.cdr.results.exchange");
        CDR_RESULTS_ROUTING_KEY = getStringProperty("env.rabbitmq.cdr.results.routingKey");

        CDR_DEAD_LETTER_QUEUE = getStringProperty("env.rabbitmq.cdr.dead.letter.queue");
        CDR_DEAD_LETTER_EXCHANGE = getStringProperty("env.rabbitmq.cdr.dead.letter.exchange");
        CDR_DEAD_LETTER_ROUTING_KEY = getStringProperty("env.rabbitmq.cdr.dead.letter.routingKey");

        CONVERTERS_GRAYLOG = getStringProperty("env.converters.graylog");
        JOB_EXECUTOR_GRAYLOG = getStringProperty("env.jobExecutor.graylog");
        FME_JOB_URL = getStringProperty("env.fme.job.url");

        maxHeavyRetries = getLongProperty("env.max.heavy.retries");

        timeoutToWaitForEmptyFileForOnDemandJobs = getLongProperty("env.onDemand.waitForEmptyFile.timeout.ms");
        maxMsToWaitForEmptyFileForOnDemandJobs = getLongProperty("env.onDemand.waitForEmptyFile.max.ms");
        maxMsForProcessingDuplicateSchemaValidation = getLongProperty("schema.validation.duplicates.processing.threshold");

        fmeUrl = getStringProperty("env.fme.url");
        fmeUser = getStringProperty("env.fme.user");
        fmePassword = getStringProperty("env.fme.password");
        fmeToken = getStringProperty("env.fme.token");

        rancherContainerMetadataUrl = getStringProperty("env.rancher.container.metadata.url");

        jobExecutorRequestsUrl = getStringProperty("jobExecutor.requests.url");
        jobExecutorTimeoutRetrieveEndpoint = getStringProperty("jobExecutor.properties.retrieve.endpoint");

    }

    /**
     * Gets property value from key
     * @param key Key
     * @return Value
     */
    public static String getStringProperty(String key) {
        try {
            return configurationService.resolveValue(key);
        }
        catch (CircularReferenceException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
        catch (UnresolvedPropertyException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Gets property numeric value from key
     * @param key Key
     * @return Value
     */
    private static int getIntProperty(String key) {
        String value = getStringProperty(key);

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException nfe) {
            LOGGER.error(nfe.getMessage());
            return 0;
        }
    }

    private static boolean getBooleanProperty(String key) {
        String value = getStringProperty(key);
        return Boolean.parseBoolean(value);
    }
    private static long getLongProperty(String key) {
        String value = getStringProperty(key);

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException nfe) {
            LOGGER.error(nfe.getMessage());
            return 0L;
        }
    }

    /**
     * Checks path
     * @param path Path
     * @return Removes trailing slash
     */
    private static String checkPath(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * Load message property from resource bundle.
     *
     * @param key
     *            Resource bundle key.
     * @return String value.
     */
    public static String getMessage(String key) {
        return getStringProperty(key);
    }

    /**
     * Load message property with parameters from resource bundle.
     *
     * @param key
     *            Resource bundle key.
     * @param replacement
     *            Replacement array.
     * @return
     */
    public static String getMessage(String key, Object[] replacement) {

        String message = MessageFormat.format(getMessage(key), replacement);
        if (message != null) {
            return message;
        }
        return null;
    }

    /**
     * @return the xslFolder
     */
    public static String getXslFolder() {
        return xslFolder;
    }

    /**
     * @param xslFolder the xslFolder to set
     */
    public static void setXslFolder(String xslFolder) {
        Properties.xslFolder = xslFolder;
    }

    /**
     * @return the xmlfileFolder
     */
    public static String getXmlfileFolder() {
        return xmlfileFolder;
    }

    /**
     * @param xmlfileFolder the xmlfileFolder to set
     */
    public static void setXmlfileFolder(String xmlfileFolder) {
        Properties.xmlfileFolder = xmlfileFolder;
    }

    /**
     * @return the tmpFolder
     */
    public static String getTmpFolder() {
        return tmpFolder;
    }

    /**
     * @param tmpFolder the tmpFolder to set
     */
    public static void setTmpFolder(String tmpFolder) {
        Properties.tmpFolder = tmpFolder;
    }

    public static String getSchemaFolder() {
        return getStringProperty("schema.folder");
    }

    public static String getHostname() {
        return hostname;
    }

    public static int getIsRancher() {
        return isRancher;
    }

}
