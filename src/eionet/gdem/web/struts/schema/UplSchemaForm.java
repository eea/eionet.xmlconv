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
import org.apache.struts.upload.FormFile;

import eionet.gdem.dto.Schema;

public class UplSchemaForm extends ActionForm {

    //T_SCHEMA table
    private String schemaId;
    private String description;
    private String schemaUrl;
    private boolean doValidation=false;
    private String schemaLang;

    //T_UPL_SCHEMA
    private String uplSchemaId;
    private FormFile schemaFile;
    private String schemaFileName;



    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        return null;
    }


    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        schemaFile = null;
        description = null;
        schemaUrl = null;
        schemaLang = Schema.getDefaultSchemaLang();
        doValidation=false;
    }


    public FormFile getSchemaFile() {
        return schemaFile;
    }


    public void setSchemaFile(FormFile schema) {
        this.schemaFile = schema;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getSchemaUrl() {
        return schemaUrl;
    }


    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }


    public String getSchemaId() {
        return schemaId;
    }


    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }


    public String getUplSchemaId() {
        return uplSchemaId;
    }


    public void setUplSchemaId(String uplSchemaId) {
        this.uplSchemaId = uplSchemaId;
    }


    public String getSchemaFileName() {
        return schemaFileName;
    }


    public void setSchemaFileName(String schemaFileName) {
        this.schemaFileName = schemaFileName;
    }


    public boolean isDoValidation() {
        return doValidation;
    }


    public void setDoValidation(boolean doValidation) {
        this.doValidation = doValidation;
    }


    public String getSchemaLang() {
        return schemaLang;
    }


    public void setSchemaLang(String schemaLang) {
        this.schemaLang = schemaLang;
    }

    public String[] getSchemaLanguages(){
        return Schema.getSchemaLanguages();
    }

    public String getDefaultSchemaLang(){
        return Schema.getDefaultSchemaLang();
    }
}
