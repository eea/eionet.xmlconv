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

package eionet.gdem.exceptions;

/**
 * Generic Exception.
 * @author Unknown
 */
public class DCMException extends Exception {

    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Constructor
     * @param errorCode Error code
     * @param message Exception message
     */
    public DCMException(String errorCode, String message) {
        super("Error Message:"+message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor
     * @param errorCode Error code
     */
    public DCMException(String errorCode) {
        this.errorCode = errorCode;
    }

}
