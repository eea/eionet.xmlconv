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

package eionet.gdem.web.struts.schema;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class SyncUplSchemaForm extends ActionForm {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // T_SCHEMA & T_UPL_SCHEMA table
    private String schemaId;
    private String schemaUrl;
    private String uplSchemaId;
    private String schemaFile;
    private String uplSchemaFileName;

    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        return null;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getSchemaUrl() {
        return schemaUrl;
    }

    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    public String getUplSchemaId() {
        return uplSchemaId;
    }

    public void setUplSchemaId(String uplSchemaId) {
        this.uplSchemaId = uplSchemaId;
    }

    public String getSchemaFile() {
        return schemaFile;
    }

    public void setSchemaFile(String schemaFile) {
        this.schemaFile = schemaFile;
    }

    public String getUplSchemaFileName() {
        return uplSchemaFileName;
    }

    public void setUplSchemaFileName(String uplSchemaFileName) {
        this.uplSchemaFileName = uplSchemaFileName;
    }

    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        schemaId = null;
        schemaUrl = null;
        uplSchemaId = null;
        schemaFile = null;
        uplSchemaFileName = null;
    }
}
