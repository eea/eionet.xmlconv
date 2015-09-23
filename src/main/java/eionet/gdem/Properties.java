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

import eionet.gdem.configuration.ConfigurationException;
import eionet.gdem.configuration.ConfigurationFactory;
import eionet.gdem.configuration.ConfigurationService;
import eionet.gdem.configuration.RuntimeConfigurationService;
import eionet.gdem.configuration.SystemPropertyProviderImpl;
import eionet.gdem.configuration.UnResolvedPropertyException;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Several settings and properties for XMLCONV application.
 */
public class Properties {

    private static ConfigurationFactory configurationFactory;
    private static ConfigurationService configurationService;
    /**
     * Logger class.
     */
    public static final Logger LOGGER = Logger.getLogger(Properties.class);

    /**
     * Application classes (WEB-INF/classes) path in file system.
     */
    public static String appHome = null;
    /**
     * Folder for temporary files.
     */
    public static String tmpFolder = null;
    /**
     * Folder for XSLs.
     */
    public static String xslFolder = null;
    /**
     * Folder for OpenDocument helper files.
     */
    public static String odsFolder = null;
    /**
     * Folder for QA scripts.
     */
    public static String queriesFolder = null;
    /**
     * Folder for XML Schema files.
     */
    public static String schemaFolder = null;
    /**
     * Folder for XML files.
     */
    public static String xmlfileFolder = null;
    /**
     * Parent folder for other user folders.
     */
    public static String appRootFolder = null;

    /**
     * Conversion service installation.
     */
    public static final int CONV_SERVICE = 1;
    /**
     * QA service installation.
     */
    public static final int QA_SERVICE = 2;
    /**
     * Number indicating the type of services are installed. Default is 3 (both
     * Conv and QA).
     */
    public static int services_installed = 3;

    /**
     * DB URL.
     */
    public static String dbUrl = null;
    /**
     * DB driver class name.
     */
    public static String dbDriver = null;
    /**
     * DB connection user name.
     */
    public static String dbUser = null;
    /**
     * DB connection password.
     */
    public static String dbPwd = null;

    /**
     * conversion.xml file location, listing all available generated
     * conversions.
     */
    public static String convFile = null;
    /**
     * XSL folder for generated conversions.
     */
    public static String metaXSLFolder = null;
    /**
     * Data Dictionary URL, used when generating XSLs.
     */
    public static String ddURL = null;
    /**
     * XMLCONV app URL.
     */
    public static String gdemURL = null;
    /**
     * Data Dictionary xml-rpc URL
     */
    public static String invServUrl = null;
    /**
     * Data Dictionary xml-rpc service name
     */
    public static String invServName = null;
    /**
     * Content Registry SPARQL endpoint URL.
     */
    public static String crSparqlEndpoint = null;

    /**
     * LDAP url.
     */
    public static String ldapUrl = null;
    /**
     * LDAP context.
     */
    public static String ldapContext = null;
    /**
     * LDAP user directory.
     */
    public static String ldapUserDir = null;
    /**
     * LDAP UID attribute.
     */
    public static String ldapAttrUid = null;

    /**
     * FME host.
     */
    public static String fmeHost = null;
    /**
     * FME port.
     */
    public static String fmePort = null;
    /**
     * FME user login.
     */
    public static String fmeUser = null;
    /**
     * FME user password.
     */
    public static String fmePassword = null;
    /**
     * FME token expiration.
     */
    public static String fmeTokenExpiration = null;
    /**
     * FME token timeunit.
     */
    public static String fmeTokenTimeunit = null;

    /**
     * OpenOffice port.
     */
    public static int openOfficePort = 8100;
    /**
     * Implementation class for QA queries. Saxon is the default value, not
     * hard-coded.
     */
    public static String engineClass = "eionet.gdem.qa.engines.SaxonImpl";
    /**
     * XGawk program executable command.
     */
    public static String xgawkCommand = null;
    /**
     * timeout for running external QA program in command line in milliseconds,
     * default 120sec.
     */
    public static long qaTimeout = 120000L;
    /**
     * Maximum number of jobs executed in workqueue simultaneously.
     */
    public static int wqMaxJobs = 20;
    /**
     * period for checking new jobs in the workqueue in seconds, default 20sec.
     */
    public static int wqCheckInterval = 20;
    /**
     * interval of deleting finished jobs in SECONDS, default 2hours=7200sec.
     */
    public static int wqCleanInterval = 7200;
    /**
     * maximum age of finished workqueue job stored in the queue in HOURS,
     * default 24 hours.
     */
    public static int wqJobMaxAge = 24;
    /**
     * Maximum size (MB) of XML file sent to manual QA for XML Schema
     * validation.
     */
    public static int qaValidationXmlUpperLimit = 200;
    /**
     * period for updating DD dataset tables data in seconds, default
     * 1hour=3600sec
     */
    public static int ddTablesUpdateInterval = 3600;

    /**
     * Date pattern used for displaying date values on UI.
     */
    public static String dateFormatPattern = "dd MMM yyyy";
    /**
     * Time pattern used for displaying time values on UI.
     */
    public static String timeFormatPattern = "dd MMM yyyy hh:mm:ss";

    private static void initConifigurationService() {

        // initialize here due to xmlconv static method calls,
        // we make sure that the ConfigurationService runs first
        Set<String> resourcesToHandle = new HashSet<String>();
        resourcesToHandle.add("gdem.properties");
        resourcesToHandle.add("uit.properties");
        resourcesToHandle.add("CatalogManager.properties");
        resourcesToHandle.add("fme.properties");
        resourcesToHandle.add("ApplicationResources.properties");
        try {
            configurationFactory = new ConfigurationFactory(resourcesToHandle, "docker_config_xmlconv");
            configurationService = new RuntimeConfigurationService(configurationFactory.getResources(), new SystemPropertyProviderImpl());
            configurationService.cacheAll();
        } catch (ConfigurationException e) {
            LOGGER.error(e.getMessage());
        }
    }

    static {
        initConifigurationService();
        // filesystem properties
        queriesFolder = getStringProperty("queries.folder");
        xslFolder = checkPath(getStringProperty("xsl.folder"));
        tmpFolder = getStringProperty("tmp.folder");
        xmlfileFolder = getStringProperty("xmlfile.folder");
        schemaFolder = getStringProperty("schema.folder");
        appRootFolder = getStringProperty("root.folder");

        // DB connection settings
        dbDriver = getStringProperty("db.driver");
        dbUrl = getStringProperty("db.url");
        dbUser = getStringProperty("db.user");
        dbPwd = getStringProperty("db.pwd");

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
        openOfficePort = getIntProperty("openoffice.service.port");

        ldapUrl = getStringProperty("ldap.url");
        ldapContext = getStringProperty("ldap.context");
        ldapUserDir = getStringProperty("ldap.user.dir");
        ldapAttrUid = getStringProperty("ldap.attr.uid");

        fmeHost = getStringProperty("fme.host");
        fmePort = getStringProperty("fme.port");
        fmeUser = getStringProperty("fme.user");
        fmePassword = getStringProperty("fme.password");
        fmeTokenExpiration = getStringProperty("fme.token.expiration");
        fmeTokenTimeunit = getStringProperty("fme.token.timeunit");

    }

    public static String getStringProperty(String key) {
        try {
            String value = configurationService.get(key);
            return value;
        } catch (UnResolvedPropertyException ue) {
            return null;
        }
    }

    private static int getIntProperty(String key) {
        try {
            String value = configurationService.get(key);
            return Integer.valueOf(value);
        } catch (UnResolvedPropertyException ue) {
            return 0;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    private static String checkPath(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * Load message property from resource bundle.
     *
     * @param key Resource bundle key.
     * @return String value.
     */
    public static String getMessage(String key) {
        if (configurationService == null) {
            initConifigurationService();
        }
        try {
            return configurationService.get(key);
        } catch (UnResolvedPropertyException e) {
            return null;
        }
    }

    /**
     * Load message property with parameters from resource bundle.
     *
     * @param key Resource bundle key.
     * @param replacement Replacement array.
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

}
