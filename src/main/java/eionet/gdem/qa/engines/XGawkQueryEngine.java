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
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.qa.engines;

import java.util.Iterator;
import java.util.Map;

import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, Tieto Estonia XGawkQueryEngine
 */

public class XGawkQueryEngine extends ExternalQueryEngine {

    @Override
    protected String getShellCommand(String dataFile, String scriptFile, Map<String, String> params) {
        return Properties.xgawkCommand + getVariables(params) + " -f " + scriptFile + " " + dataFile;
    }

    /**
     * Gets variables
     * @param params Parameters
     * @return Variables
     */
    protected String getVariables(Map<String, String> params) {

        String ret = "";
        if (!Utils.isNullHashMap(params)) {
            StringBuffer buf = new StringBuffer();
            Iterator<String> it = params.keySet().iterator();

            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                buf.append(" -v ");
                buf.append(key);
                buf.append("=\"");
                buf.append(value);
                buf.append("\"");
            }
            ret = buf.toString();
        }

        return ret;
    }
}
