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
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Istvan Alfeldi (ED)
 */

package eionet.gdem.dcm.conf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DCM Properties class.
 * @author Unknown
 * @author George Sofianos
 */
public class DcmProperties {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(DcmProperties.class);

    /**
     * Set database parameters.
     * @param url Connection url
     * @param user User
     * @param psw Password
     * @throws DCMException If an error occurs.
     */
    public void setDbParams(String url, String user, String psw) throws DCMException {
        String filePath = Properties.appHome + File.separatorChar + "env.properties";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            StringBuffer st = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                // process the line
                line = findSetProp(line, "config.db.jdbcurl", url);
                line = findSetProp(line, "config.db.user", user);
                line = findSetProp(line, "config.db.password", psw);
                st.append(line);
                st.append("\n");
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
            out.write(st.toString());
            out.close();
        } catch (IOException e) {
            LOGGER.error("Saving database parameters failed!", e);
            throw new DCMException(BusinessConstants.EXCEPTION_PARAM_DB_FAILED);
        }
    }

    /**
     * Sets LDAP Parameters
     * @param url Connection url
     * @param context Context
     * @param userDir User Directory
     * @param attrUid UID Attribute
     * @throws DCMException If an error occurs.
     */
    public void setLdapParams(String url, String context, String userDir, String attrUid) throws DCMException {

        String filePath = Properties.appHome + File.separatorChar + "eionetdir.properties";

        try {

            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            StringBuffer st = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                // process the line
                line = findSetProp(line, "ldap.url", url);
                line = findSetProp(line, "ldap.context", context);
                line = findSetProp(line, "ldap.user.dir", userDir);
                line = findSetProp(line, "ldap.attr.uid", attrUid);
                st.append(line);
                st.append("\n");
            }

            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
            out.write(st.toString());
            out.close();
        } catch (IOException e) {
            LOGGER.error("Saving ldap parameters failed!", e);
            throw new DCMException(BusinessConstants.EXCEPTION_PARAM_LDAP_FAILED);
        }
    }

    /**
     * Sets BaseX Parameters
     * @param host Host
     * @param port Port
     * @param user User
     * @param psw Password
     * @throws DCMException If an error occurs.
     */
    public void setBasexParams(String host, String port, String user, String psw) throws DCMException {

        String filePath = Properties.appHome + File.separatorChar + "env.properties";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            StringBuffer st = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                // process the line
                line = findSetProp(line, "basexserver.host", host);
                line = findSetProp(line, "basexserver.port", port);
                line = findSetProp(line, "basexserver.user", user);
                line = findSetProp(line, "basexserver.password", psw);
                st.append(line);
                st.append("\n");
            }

            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
            out.write(st.toString());
            out.close();

            Properties.basexServerPort = port;
            Properties.basexServerPassword = psw;
            Properties.basexServerHost = host;
            Properties.basexServerUser = user;

        } catch (IOException e) {
            LOGGER.error("Saving BaseX server parameters failed!", e);
            throw new DCMException(BusinessConstants.EXCEPTION_PARAM_BASEXSERVER_FAILED);
        }
    }

    /**
     * Sets system parameters
     * @param qaTimeout QA timeout
     * @param cmdXGawk XGawk command
     * @throws DCMException If an error occurs.
     */
    public void setSystemParams(Long qaTimeout, String cmdXGawk) throws DCMException {

        String filePath = Properties.appHome + File.separatorChar + "gdem.properties";

        try {

            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            StringBuffer st = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                // process the line
                line = findSetProp(line, "external.qa.timeout", String.valueOf(qaTimeout));
                line = findSetProp(line, "external.qa.command.xgawk", cmdXGawk);
                st.append(line);
                st.append("\n");
            }

            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
            out.write(st.toString());
            out.close();

            Properties.xgawkCommand = cmdXGawk;
            Properties.qaTimeout = Long.valueOf(qaTimeout);

        } catch (IOException e) {
            LOGGER.error("Saving system parameters failed!", e);
            throw new DCMException(BusinessConstants.EXCEPTION_PARAM_SYSTEM_FAILED);
        }
    }

    /**
     * Sets property value
     * @param line Line
     * @param key Key
     * @param value Value
     * @return Line
     */
    private String findSetProp(String line, String key, String value) {
        if (line.startsWith(key + "=")) {
            line = key + "=" + value;
        }
        return line;
    }

}
