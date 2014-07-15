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

package eionet.gdem.web.struts.stylesheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;

public class StylesheetForm extends ActionForm {

    private String schema;
    private String outputtype;
    private String description;
    private FormFile xslfile;
    private String schemaId;
    private String xsl;
    private String stylesheetId;
    private String xslContent;
    private String xslFileName;
    private String modified;
    private String checksum;
    private String dependsOn;
    /** List of related XML Schemas. */
    private List<Schema> schemas;
    /** List of new related XML Schemas. */
    private List<String> newSchemas;
    /** If any related schema lang=EXCEL then show depends on selection on the form. */
    private boolean showDependsOnInfo;
    /** List of stylesheets available for dependsOn attribute. */
    private List<Stylesheet> existingStylesheets;

    @Override
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        return null;
    }

    @Override
    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        schema = null;
        outputtype = null;
        description = null;
        xslfile = null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOutputtype() {
        return outputtype;
    }

    public void setOutputtype(String outputtype) {
        this.outputtype = outputtype;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public FormFile getXslfile() {
        return xslfile;
    }

    public void setXslfile(FormFile xslfile) {
        this.xslfile = xslfile;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getXsl() {
        return xsl;
    }

    public void setXsl(String xsl) {
        this.xsl = xsl;
    }

    public String getStylesheetId() {
        return stylesheetId;
    }

    public void setStylesheetId(String stylesheetId) {
        this.stylesheetId = stylesheetId;
    }

    public String getXslContent() {
        return xslContent;
    }

    public void setXslContent(String xslContent) {
        this.xslContent = xslContent;
    }

    public String getXslFileName() {
        return xslFileName;
    }

    public void setXslFileName(String xslFileName) {
        this.xslFileName = xslFileName;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * @return the dependsOn
     */
    public String getDependsOn() {
        return dependsOn;
    }

    /**
     * @param dependsOn
     *            the dependsOn to set
     */
    public void setDependsOn(String dependsOn) {
        this.dependsOn = dependsOn;
    }

    /**
     * @return the schemas
     */
    public List<Schema> getSchemas() {
        return schemas;
    }

    /**
     * @param schemas the schemas to set
     */
    public void setSchemas(List<Schema> schemas) {
        this.schemas = schemas;
    }

    /**
     * @return the showDependsOnInfo
     */
    public boolean isShowDependsOnInfo() {
        return showDependsOnInfo;
    }

    /**
     * @param showDependsOnInfo the showDependsOnInfo to set
     */
    public void setShowDependsOnInfo(boolean showDependsOnInfo) {
        this.showDependsOnInfo = showDependsOnInfo;
    }

    /**
     * @return the existingStylesheets
     */
    public List<Stylesheet> getExistingStylesheets() {
        return existingStylesheets;
    }

    /**
     * @param existingStylesheets the existingStylesheets to set
     */
    public void setExistingStylesheets(List<Stylesheet> existingStylesheets) {
        this.existingStylesheets = existingStylesheets;
    }

    /**
     * @return the newSchemas
     */
    public List<String> getNewSchemas() {
        if (newSchemas == null) {
            newSchemas = new ArrayList<String>();
            newSchemas.add("");
        }
        return newSchemas;
    }

    /**
     * @param newSchemas the newSchemas to set
     */
    public void setNewSchemas(String newSchemas[]) {
        this.newSchemas = Arrays.asList(newSchemas);
    }
}
