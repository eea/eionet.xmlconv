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
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem.services;

import eionet.gdem.services.db.dao.DCMDaoFactory;

/**
 * Container class for GDEM Services e.g. DBService
 * @author Unknown
 * @author George Sofianos
 */
public final class GDEMServices {

    /**
     * Utility class constructor
     */
    private GDEMServices() {
        // do nothing
    }

    private static boolean testConnection = false;

    public static DCMDaoFactory getDaoService() {
        return DCMDaoFactory.getDaoFactory(DCMDaoFactory.MYSQL_DB);
    }

    public static boolean isTestConnection() {
        return testConnection;
    }

    public static void setTestConnection(boolean testConnection) {
        GDEMServices.testConnection = testConnection;
    }
}
