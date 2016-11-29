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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log device implementation.
 * @author Enriko Käsper, Tieto Estonia LogDevice
 */

public class LogDevice implements ILogDevice {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogDevice.class);
    @Override
    public void log(String c) {
        LOGGER.debug(c);
    }

}
