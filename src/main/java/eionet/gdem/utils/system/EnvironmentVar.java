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

package eionet.gdem.utils.system;

/**
 * Environment Variable class.
 * @author Enriko Käsper, Tieto Estonia EnvironmentVar
 */

class EnvironmentVar {
    public String fName = null;
    public String fValue = null;

    /**
     * Constructor
     * @param name Name
     * @param value Value
     */
    public EnvironmentVar(String name, String value) {
        fName = name;
        fValue = value;
    }
}
