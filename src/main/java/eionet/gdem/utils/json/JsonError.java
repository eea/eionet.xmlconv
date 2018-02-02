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
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */

package eionet.gdem.utils.json;

import org.apache.http.HttpStatus;

/**
 * Object for storing Json error message.
 *
 * @author Enriko Käsper
 */
public class JsonError {
    
    /** Http response code */
    Integer code;
    /** Response message */
    String message;

    /**
     * Constructor
     * @param code Code
     * @param message Message
     */
    public JsonError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Constructor
     * @param message message
     */
    public JsonError(String message) {
        this.code = getDefaultErrorCode();
        this.message = message;
    }
    /**
     * Initialize with default values
     */
    public JsonError() {
        this.code = getDefaultErrorCode();
        this.message = "Unknown error.";
    }
    /**
     * @return response message
     */
    public String getMessage(){
        return this.message;
    }
    /**
     * @return response code
     */
    public Integer getCode(){
        return this.code;
    }
    /**
     * @return default response code
     */
    private Integer getDefaultErrorCode(){
        return HttpStatus.SC_BAD_REQUEST;
    }

}
