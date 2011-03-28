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
 * Several settings and properties for GDEM
 */
public class Properties {

    public static String appHome = null;

    public static String tmpFolder = "/tmp";

    //public static String urlPrefix="http://conversions.eionet.eu.int/";

    public static String xslFolder = "/xsl/";

    public static String odsFolder = "/opendoc/ods";

    public static String queriesFolder = "/queries/";

    public static String schemaFolder = "/schema/";

    public static String xmlfileFolder = "xmlfile";

    //full path to xmlfileFolder. Calclated at context init
    public static String xmlfileFolderPath = "/xmlfile/";

    public static final int CONV_SERVICE = 1; //Conversion service weight
    public static final int QA_SERVICE = 2; //QA service weight
    public static int services_installed = 3; //by default the both services are installed

    //public static String xformsFolder="/xforms/";

    //Database settings from the properties file
    public static String dbUrl = null;
    public static String dbDriver = null;
    public static String dbUser = null;
    public static String dbPwd = null;

    //Edit UI
    public static String uiFolder = null;

    //period for checking new jobs in the workqueue in milliseconds, default 20sec
    public static long wqCheckInterval = 20000L;

    //NB Saxon is the default value, not hard-coded!
    public static String engineClass = "eionet.gdem.qa.engines.SaxonImpl";

    //DCM settings from the properties file
    public static String convFile = null;
    public static String metaXSLFolder = null;
    public static String ddURL = null;
    public static String gdemURL = null;

    //DCM settings from the properties file of incoming services from DD
    public static String invServUrl = null;
    public static String invServName = null;

    //Content registry xml-rpc
    public static String crServUrl = null;
    public static String crServName = null;

    //ldap url
    public static String ldapUrl = null;
    public static String ldapContext = null;
    public static String ldapUserDir = null;
    public static String ldapAttrUid = null;

    //DCM settings from the properties file of incoming services from CDR
    //CDR doesn't use Service names
    public static String cdrServUrl = null;

    // TODO add initialization from property file
    public static int openOfficePort = 8100;
    public static String xgawkCommand = null;
    //timeout for running external QA program in command line in milliseconds, default 120sec
    public static long qaTimeout = 120000L;

    private static ResourceBundle props;
    private static ResourceBundle ldapProps;
    public static final Logger logger = Logger.getLogger(Properties.class);

    public static String dateFormatPattern="dd MMM yyyy";
    public static String timeFormatPattern="dd MMM yyyy hh:mm:ss";

    private static ResourceBundle applicationResources;

    public static int wqMaxJobs = 20;

     static {


        if (props == null) {
            props = ResourceBundle.getBundle("gdem");
            try {
                queriesFolder = props.getString("queries.folder");

                //xformsFolder=props.getString("xforms.folder");

                xslFolder=checkPath(props.getString("xsl.folder"));
                tmpFolder=props.getString("tmp.folder");
                odsFolder=checkPath(props.getString("ods.folder"));

                xmlfileFolder = props.getString("xmlfile.folder");

                //DB connection settings
                dbDriver = props.getString("db.driver");
                dbUrl = props.getString("db.url");
                dbUser = props.getString("db.user");
                dbPwd = props.getString("db.pwd");

                engineClass = props.getString("xq.engine.implementator");
                //DCM settings
                ddURL = props.getString("dd.url");
                gdemURL = props.getString("gdem.url");

                //settings for incoming services from DD
                invServUrl = props.getString("dd.rpc.url");
                invServName = props.getString("dd.rpcservice.name");

                //settings for incoming services from CDR
                cdrServUrl = props.getString("cdr.url");

                //settings for incoming services from Content Registry
                crServUrl = props.getString("cr.rpc.url");
                crServName = props.getString("cr.rpcservice.name");

                //period in seconds
                String frequency = props.getString("wq.check.interval");
                Float f = new Float(frequency);
                wqCheckInterval = (long) (f.floatValue() * 1000);

                //maximum number of jobs executed at the same time
                String maxJobs = props.getString("wq.max.jobs");
                try{
                    if (maxJobs!=null && maxJobs.length()>0) {wqMaxJobs =Integer.parseInt(maxJobs);}
                }
                catch(Exception e){}

                dateFormatPattern=props.getString("date.format.pattern");
                timeFormatPattern=props.getString("time.format.pattern");

                try {
                    services_installed = Integer.parseInt(props.getString("gdem.services"));
                } catch (Exception e) { //ignore, use default
                }

                String ooServicePortStr = props.getString("openoffice.service.port");
                try {
                    if (ooServicePortStr != null && ooServicePortStr.length() > 0) {
                        openOfficePort = Integer.parseInt(ooServicePortStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //wqCheckInterval= (Long.getLong(props.getString("wq.check.interval"))).longValue();

                //urlPrefix=props.getString("url.prefix"); //URL where the files can be downloaded
                //maximum number of jobs executed at the same time
                String sQaTimeout = props.getString("external.qa.timeout");
                try{
                    if (sQaTimeout!=null && sQaTimeout.length()>0) {qaTimeout =Long.parseLong(sQaTimeout);}
                }
                catch(Exception e){
                    logger.error("\"external.qa.timeout\" property is not defined or is not numeric in gdem.properties");
                }
                xgawkCommand = props.getString("external.qa.command.xgawk");

            } catch (MissingResourceException mse) {
                mse.printStackTrace();

                //no error handling? go with the default values??
            } catch (Exception e) {
                //System.out.println("error " + e.toString());
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
        if (applicationResources == null)
            applicationResources = ResourceBundle.getBundle("ApplicationResources");

    }

    private static String checkPath(String path){
        if(path.endsWith("/")){
            path=path.substring(0, path.length() -1);
        }
        return path;
    }
    public static String getMessage(String key){
        return applicationResources.getString(key);
    }
    public static String getMessage(String key, Object[] replacement){
        return MessageFormat.format(applicationResources.getString(key), replacement);
    }

}
