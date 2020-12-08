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
import java.util.ResourceBundle;

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

    /** FME host. */
    public static String fmeHost = null;
    /** FME port. */
    public static String fmePort = null;
    /** FME user login. */
    public static String fmeUser = null;
    /** FME user password. */
    public static String fmePassword = null;
    /** FME token expiration. */
    public static String fmeTokenExpiration = null;
    /** FME token timeunit. */
    public static String fmeTokenTimeunit = null;
    /** FME timeout. */
    public static int fmeTimeout = 0;
    public static int fmeRetryHours = 4;
    public static String fmePollingUrl = null;
    public static String fmeResultFolderUrl = null;
    public static String fmeResultFolder = null;
    public static String fmeDeleteFolderUrl = null;

    /** Hostname. */
    public static String hostname = null;
    /** Is Rancher Boolean. */
    public static int isRancher = 0;
    /** url for rancher metadata queries. */
    public static String rancherMetadataUrl;

    /** Implementation class for QA queries. Saxon is the default value, not hard-coded. */
    public static String engineClass = "eionet.gdem.qa.engines.SaxonImpl";
    /** XGawk program executable command. */
    public static String xgawkCommand = null;
    /** timeout for running external QA program in command line in milliseconds, default 120sec. */
    public static long qaTimeout = 120000L;
    /** Maximum number of jobs executed in workqueue simultaneously. */
    public static int wqMaxJobs = 20;
    /** period for checking new jobs in the workqueue in seconds, default 20sec. */
    public static int wqCheckInterval = 20;
    /** interval of deleting finished jobs in SECONDS, default 2hours=7200sec. */
    public static int wqCleanInterval = 7200;
    /** maximum age of finished workqueue job stored in the queue in HOURS, default 24 hours. */
    public static int wqJobMaxAge = 24;
    /** Maximum size (MB) of XML file sent to manual QA for XML Schema validation. */
    public static int qaValidationXmlUpperLimit = 200;
    /** period for updating DD dataset tables data in seconds, default 1hour=3600sec */
    public static int ddTablesUpdateInterval = 3600;

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


    public static int heavyJobThreshhold;

    public static Long longRunningJobThreshold;

    public static final String SSO_LOGIN_URL;

    public static final String XQUERY_HTTP_ENDPOINTS;

    public static final Long BASEX_XQUERY_TIME_LIMIT;

    public static Long maxSchemaExecutionTime;

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

    public static final String rancherApiAccessKey;

    public static final String rancherApiSecretKey;

    public static String rabbitMQHost;
    public static Integer rabbitMQPort;
    public static String rabbitMQUsername;
    public static String rabbitMQPassword;
    public static String WORKERS_JOBS_QUEUE;
    public static String WORKERS_JOBS_RESULTS_QUEUE;
    public static String MAIN_XMLCONV_JOBS_EXCHANGE;
    public static String MAIN_WORKERS_EXCHANGE;
    public static String JOBS_ROUTING_KEY;
    public static String JOBS_RESULTS_ROUTING_KEY;

    public static boolean enableQuartz;

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

        // QA Service properties
        engineClass = getStringProperty("xq.engine.implementator");
        // period in milliseconds
        wqCheckInterval = getIntProperty("wq.check.interval");
        // period in seconds
        wqCleanInterval = getIntProperty("wq.clean.job.interval");
        // period in hours
        wqJobMaxAge = getIntProperty("wq.job.max.age");
        // maximum number of jobs executed at the same time
        wqMaxJobs = getIntProperty("wq.max.jobs");
        interruptingJobsInterval = getIntProperty("wq.job.interrupt.interval");
        // default value of the maximum size of XML file sent to ad-hoc QA
        qaValidationXmlUpperLimit = getIntProperty("qa.validation.xml.upper_limit");
        // external QA program timeout
        qaTimeout = getIntProperty("external.qa.timeout");
        // exteranal QA program
        xgawkCommand = getStringProperty("external.qa.command.xgawk");
        // period in seconds
        ddTablesUpdateInterval = getIntProperty("dd.tables.update.job.interval");

        dateFormatPattern = getStringProperty("date.format.pattern");
        timeFormatPattern = getStringProperty("time.format.pattern");
        services_installed = getIntProperty("gdem.services");

        hostname = getStringProperty("config.hostname") == null ? "hostname_not_set" : getStringProperty("config.hostname");
        isRancher = getIntProperty("config.isRancher");
        rancherMetadataUrl = getStringProperty("rancher.metadata.url");

        fmeHost = getStringProperty("fme.host");
        fmePort = getStringProperty("fme.port");
        fmeUser = getStringProperty("fme.user");
        fmePassword = getStringProperty("fme.password");
        fmeTokenExpiration = getStringProperty("fme.token.expiration");
        fmeTokenTimeunit = getStringProperty("fme.token.timeunit");
        fmeTimeout = getIntProperty("fme.timeout");
        fmeRetryHours = getIntProperty("fme.retry.hours");
        heavyJobThreshhold = getIntProperty ("config.heavy.threshold");
        fmePollingUrl = getStringProperty("fme.polling.url");
        fmeResultFolderUrl = getStringProperty("fme.result.folder.url");
        fmeResultFolder = getStringProperty("fme.result.folder");
        fmeDeleteFolderUrl = getStringProperty("fme.delete.folder.url");
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

        SSO_LOGIN_URL = getStringProperty(CASFilter.LOGIN_INIT_PARAM);

        XQUERY_HTTP_ENDPOINTS = getStringProperty("env.xquery.http.endpoints");

        BASEX_XQUERY_TIME_LIMIT = getLongProperty("env.basex.xquery.timeLimit");

        maxSchemaExecutionTime = getLongProperty("env.schema.maxExecutionTime");

        jwtAudienceProperty = getStringProperty("jwt.audience");
        jwtIssuerProperty = getStringProperty("jwt.issuer");
        jwtHeaderProperty = getStringProperty("jwt.header");
        jwtHeaderSchemaProperty = getStringProperty("jwt.header.schema");
        jwtSecretKey = getStringProperty("jwt.secret");

        crHost = getStringProperty("config.cr.host");

        mockCrUrl = getStringProperty("config.cr.mockCrUrl");
       enableXqueryCrCallsInterception =Boolean.parseBoolean(getStringProperty("config.enableXqueryCrCallsInterception"));

       rancherApiAccessKey = getStringProperty("env.rancher.api.accessKey");
       rancherApiSecretKey = getStringProperty("env.rancher.api.secretKey");

        rabbitMQHost = getStringProperty("env.rabbitmq.host");
        rabbitMQPort = getIntProperty("env.rabbitmq.port");
        rabbitMQUsername = getStringProperty("env.rabbitmq.username");
        rabbitMQPassword = getStringProperty("env.rabbitmq.password");
        WORKERS_JOBS_QUEUE = getStringProperty("env.rabbitmq.workers.jobs.queue");
        WORKERS_JOBS_RESULTS_QUEUE = getStringProperty("env.rabbitmq.workers.jobs.results.queue");
        MAIN_XMLCONV_JOBS_EXCHANGE = getStringProperty("env.rabbitmq.main.xmlconv.jobs.exchange");
        MAIN_WORKERS_EXCHANGE = getStringProperty("env.rabbitmq.main.workers.exchange");
        JOBS_ROUTING_KEY = getStringProperty("env.rabbitmq.jobs.routingkey");
        JOBS_RESULTS_ROUTING_KEY = getStringProperty("env.rabbitmq.jobs.results.routingkey");

        enableQuartz = Boolean.parseBoolean(getStringProperty("env.enable.quartz"));
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
