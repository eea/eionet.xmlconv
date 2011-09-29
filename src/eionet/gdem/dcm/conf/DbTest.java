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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;

public class DbTest {

    /** */
    private static final Log LOGGER = LogFactory.getLog(DbTest.class);

    public void tstDbParams(String url, String user, String psw) throws Exception {

        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;

        try {
            // Class.forName(Properties.dbDriver);
            Class.forName(Properties.dbDriver);

            con = DriverManager.getConnection(url, user, psw);
            stmt = con.createStatement();
            String sql = "SELECT 1";
            rset = stmt.executeQuery(sql);

        } catch (Exception e) {
            LOGGER.debug("Testing database connection failed!", e);
            e.printStackTrace();
            throw new DCMException(BusinessConstants.EXCEPTION_PARAM_DB_TEST_FAILED);
        } finally {
            // Close connection
            if (rset != null) {
                rset.close();
            }
            if (stmt != null) {
                stmt.close();
                if (!con.getAutoCommit()) {
                    con.commit();
                }
            }
            if (con != null) {
                con.close();
                con = null;
            }

        }
    }

}
