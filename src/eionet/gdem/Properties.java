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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Several settings and properties for XMLCONV application.
 */
public class Properties {

    /** Application classes (WEB-INF/classes) path in file system. */
    public static String appHome = null;
    /** Folder for temporary files. */
    public static String tmpFolder = "/tmp";
    /** Folder for XSLs. */
    public static String xslFolder = "/xsl/";
    /** Folder for OpenDocument helper files. */
    public static String odsFolder = "/opendoc/ods";
    /** Folder for QA scripts. */
    public static String queriesFolder = "/queries/";
    /** Folder for XML Schema files. */
    public static String schemaFolder = "/schema/";
    /** Folder for XML files. */
    public static String xmlfileFolder = "xmlfile";
    /** Full path to xmlfileFolder. Calclated at context init. */
    public static String xmlfileFolderPath = "/xmlfile/";

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

    /** Edit UI folder. */
    public static String uiFolder = null;

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

    /** LDAP url. */
    public static String ldapUrl = null;
    /** LDAP context. */
    public static String ldapContext = null;
    /** LDAP user directory. */
    public static String ldapUserDir = null;
    /** LDAP UID attribute. */
    public static String ldapAttrUid = null;

    /** DCM settings from the properties file of incoming services from CDR. CDR doesn't use Service names. */
    public static String cdrServUrl = null;

    /** OpenOffice port. */
    public static int openOfficePort = 8100;
    /** Implementation class for QA queries. Saxon is the default value, not hard-coded. */
    public static String engineClass = "eionet.gdem.qa.engines.SaxonImpl";
    /** XGawk program executable command. */
    public static String xgawkCommand = null;
    /** timeout for running external QA program in command line in milliseconds, default 120sec. */
    public static long qaTimeout = 120000L;
    /** Maximum number of jobs executed in workqueue simultaniosly. */
    public static int wqMaxJobs = 20;
    /** period for checking new jobs in the workqueue in milliseconds, default 20sec. */
    public static long wqCheckInterval = 20000L;
    /** Maximum size (MB) of XML file sent to manual QA for XML Schema validation. */
    public static int qaValidationXmlUpperLimit = 200;

    /** Resource bundle for gdem properties. */
    private static ResourceBundle props;
    /** Resource bundle for LDAP properties. */
    private static ResourceBundle ldapProps;
    /** Resource bundle for Messages. */
    private static ResourceBundle applicationResources;
    /** Logger class. */
    public static final Logger logger = Logger.getLogger(Properties.class);

    /** Date pattern used for displaying date values on UI. */
    public static String dateFormatPattern = "dd MMM yyyy";
    /** Time pattern used for displaying time values on UI. */
    public static String timeFormatPattern = "dd MMM yyyy hh:mm:ss";

    static {

        if (props == null) {
            props = ResourceBundle.getBundle("gdem");
            try {
                queriesFolder = props.getString("queries.folder");

                // xformsFolder=props.getString("xforms.folder");

                xslFolder = checkPath(props.getString("xsl.folder"));
                tmpFolder = props.getString("tmp.folder");
                odsFolder = checkPath(props.getString("ods.folder"));

                xmlfileFolder = props.getString("xmlfile.folder");

                // DB connection settings
                dbDriver = props.getString("db.driver");
                dbUrl = props.getString("db.url");
                dbUser = props.getString("db.user");
                dbPwd = props.getString("db.pwd");

                engineClass = props.getString("xq.engine.implementator");
                // DCM settings
                ddURL = props.getString("dd.url");
                gdemURL = props.getString("gdem.url");

                // settings for incoming services from DD
                invServUrl = props.getString("dd.rpc.url");
                invServName = props.getString("dd.rpcservice.name");

                // settings for incoming services from CDR
                cdrServUrl = props.getString("cdr.url");

                // settings for incoming services from Content Registry
                crSparqlEndpoint = props.getString("cr.sparql.endpoint");

                // period in seconds
                String frequency = props.getString("wq.check.interval");
                Float f = new Float(frequency);
                wqCheckInterval = (long) (f.floatValue() * 1000);

                // maximum number of jobs executed at the same time
                String maxJobs = props.getString("wq.max.jobs");
                try {
                    if (maxJobs != null && maxJobs.length() > 0) {
                        wqMaxJobs = Integer.parseInt(maxJobs);
                    }
                } catch (Exception e) {
                    logger.error("\"wq.max.jobs\" property is not defined or is not numeric in gdem.properties");
                }

                dateFormatPattern = props.getString("date.format.pattern");
                timeFormatPattern = props.getString("time.format.pattern");

                try {
                    services_installed = Integer.parseInt(props.getString("gdem.services"));
                } catch (Exception e) {
                    logger.error("\"gdem.services\" property is not defined or is not numeric in gdem.properties");
                }

                String ooServicePortStr = props.getString("openoffice.service.port");
                try {
                    if (ooServicePortStr != null && ooServicePortStr.length() > 0) {
                        openOfficePort = Integer.parseInt(ooServicePortStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // maximum number of jobs executed at the same time
                String sQaTimeout = props.getString("external.qa.timeout");
                try {
                    if (sQaTimeout != null && sQaTimeout.length() > 0) {
                        qaTimeout = Long.parseLong(sQaTimeout);
                    }
                } catch (Exception e) {
                    logger.error("\"external.qa.timeout\" property is not defined or is not numeric in gdem.properties");
                }
                try {
                    qaValidationXmlUpperLimit = Integer.parseInt(props.getString("qa.validation.xml.upper_limit"));
                } catch (Exception e) {
                    logger.error("\"qa.validation.xml.upper_limit\" property is not defined or is not numeric in gdem.properties");
                }

                xgawkCommand = props.getString("external.qa.command.xgawk");

            } catch (MissingResourceException mse) {
                logger.error("Missing property in gdem.properties");
                mse.printStackTrace();
                // no error handling? go with the default values??
            } catch (Exception e) {
                logger.error("Error when reading properties from gdem.properties");
                e.printStackTrace();
            }
        }

        if (ldapProps == null) {
            ldapProps = ResourceBundle.getBundle("eionetdir");
            try {
                ldapUrl = ldapProps.getString("ldap.url");
                ldapContext = ldapProps.getString("ldap.context");
                ldapUserDir = ldapProps.getString("ldap.user.dir");
                ldapAttrUid = ldapProps.getString("ldap.attr.uid");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (applicationResources == null) {
            applicationResources = ResourceBundle.getBundle("ApplicationResources");
        }

    }

    private static String checkPath(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static String getMessage(String key) {
        return applicationResources.getString(key);
    }

    public static String getMessage(String key, Object[] replacement) {
        return MessageFormat.format(applicationResources.getString(key), replacement);
    }

}