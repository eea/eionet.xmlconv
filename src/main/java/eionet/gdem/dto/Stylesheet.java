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

package eionet.gdem.dto;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eionet.gdem.Properties;

public class Stylesheet implements Serializable {

    /** Unique key of stylehseet. */
    private String convId;
    /** Stylesheet web path and file name eg. stylesheet/file.xsl. */
    private String xsl;
    /** Stylesheet type. */
    private String type;
    /** Textual description of the stylesheet. */
    private String description;
    /** DD conversion ID in case of generated XSLT. */
    private boolean ddConv;
    /** XML Schema URL. */
    @Deprecated
    private String schema;
    /** XSLT file content. */
    private String xslContent;
    /** Stylesheet file name. */
    private String xslFileName;
    /** Generated MD5 hash from file contents. */
    private String checksum;
    /** Depending XSLT. Used in case of sequential conversion.*/
    private String dependsOn;
    /** Formatted last modified time of XSL file as string. */
    private String modified;
    /** Last modified time of XSL file. */
    private Date lastModifiedTime;
    /** List of related XML Schemas. */
    private List<Schema> schemas;
    /** List of XML Schema URLs. */
    private List<String> schemaUrls;
    /** List of XML Schema IDs. */
    private List<String> schemaIds;

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

    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXsl() {
        return xsl;
    }

    public void setXsl(String xsl) {
        this.xsl = xsl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public boolean isDdConv() {
        return ddConv;
    }

    public void setDdConv(boolean ddConv) {
        this.ddConv = ddConv;
    }

    @Deprecated
    public String getSchema() {
        return schema;
    }

    @Deprecated
    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getXslContent() {
        return xslContent;
    }

    public void setXslContent(String content) {
        this.xslContent = content;
    }

    public String getXslFileName() {
        return xslFileName;
    }

    public void setXslFileName(String xslFileName) {
        this.xslFileName = xslFileName;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * @return the lastModifiedTime
     */
    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * @param lastModifiedTime the lastModifiedTime to set
     */
    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
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
     * Adds schema object into the list of related schemas.
     * @param schema XML Schema object.
     */
    public void addSchema(Schema schema) {
        if (schemas == null) {
            schemas = new ArrayList<Schema>();
        }
        schemas.add(schema);
    }

    /**
     * Return full path to XSLT file in file system.
     * @return XSLT file path.
     */
    public String getXslFileFullPath() {
        if(StringUtils.isNotBlank(getXslFileName())){
            return Properties.xslFolder + File.separator + getXslFileName();
        }
        else{
            return null;
        }
    }

    /**
     * @return the schemaUrls
     */
    public List<String> getSchemaUrls() {
        return schemaUrls;
    }

    /**
     * @param schemaUrls the schemaUrls to set
     */
    public void setSchemaUrls(List<String> schemaUrls) {
        this.schemaUrls = schemaUrls;
    }

    /**
     * @return the schemaIds
     */
    public List<String> getSchemaIds() {
        return schemaIds;
    }

    /**
     * @param schemaIds the schemaIds to set
     */
    public void setSchemaIds(List<String> schemaIds) {
        this.schemaIds = schemaIds;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Stylesheet [convId=" + convId + ", xsl=" + xsl + ", type=" + type + ", description=" + description + ", ddConv="
                + ddConv + ", schema=" + schema + ", xslContent=" + xslContent + ", xslFileName=" + xslFileName + ", checksum="
                + checksum + ", dependsOn=" + dependsOn + ", modified=" + modified + ", lastModifiedTime=" + lastModifiedTime
                + ", schemas=" + schemas + "]";
    }

}
