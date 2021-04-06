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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import eionet.gdem.Properties;

/**
 * Schema class.
 */
public class Schema implements Serializable {

    /** XML Schema unique numeric ID. */
    private String id;
    /** XML Schema URL. */
    private String schema;
    /** XML Schema textual description. */
    private String description;
    /** List of related XSL stylesheets. */
    private List<Stylesheet> stylesheets;
    /** Is it XML Schema or DTD. */
    boolean isDTD = false;
    /** DTD public id. */
    private String dtdPublicId;
    /** Data Dictionary table identifier if the schema describes dataset table. */
    private String table;
    /** Data Dictionary dataset identifier if the schema describes dataset. */
    private String dataset;
    /** List of Central Data Repository files confirming to given schema. */
    private List<CdrFileDto> cdrfiles;
    /** List of Content Registry files confirming to given schema. */
    private List<CrFileDto> crfiles;
    /** Date when Data Dictionary dataset was released. */
    private Date datasetReleased;
    /** XML Schema validation is part of QA. */
    private boolean doValidation = false;
    /** XML Schema language: XML_SCHEMA or DTD. */
    private String schemaLang;
    /** Uploaded XML Schema file name. */
    private String uplSchemaFileName;
    /** List of related QA scripts. */
    private List<QAScript> qascripts;
    /** Uploaded Schema file object. */
    private UplSchema uplSchema;
    /** XML Schema expire date. */
    private Date expireDate;
    /** Does failed XML Schema validation returns blocker QA. */
    private boolean blocker = false;
    /** Number of related stylesheets. */
    private int countStylesheets;
    /** Number of related QA scripts. */
    private int countQaScripts;
    /** Related stylesheet ID. */
    private String stylesheetSchemaId;
    /** Max execution time of jobs related to this schema*/
    private Long maxExecutionTime;
    private String maxExecutionTimeUI;

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public UplSchema getUplSchema() {
        return uplSchema;
    }

    public void setUplSchema(UplSchema uplSchema) {
        this.uplSchema = uplSchema;
    }

    public List<QAScript> getQascripts() {
        return qascripts;
    }

    public void setQascripts(List<QAScript> qascripts) {
        this.qascripts = qascripts;
    }

    private static String[] schemaLanguages = {"XSD", "DTD", "EXCEL"};
    private static String defaultSchemaLang = "XSD";

    /**
     * Default constructor
     */
    public Schema() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public List<Stylesheet> getStylesheets() {
        return stylesheets;
    }

    public void setStylesheets(List<Stylesheet> stylesheets) {
        this.stylesheets = stylesheets;
    }

    public boolean getIsDTD() {
        return getSchemaLang().equals("DTD");
    }

    public void setIsDTD(boolean isDTD) {
        this.isDTD = isDTD;
    }

    public String getDtdPublicId() {
        return dtdPublicId;
    }

    public void setDtdPublicId(String dtdPublicId) {
        this.dtdPublicId = dtdPublicId;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<CdrFileDto> getCdrfiles() {
        return cdrfiles;
    }

    public void setCdrfiles(List<CdrFileDto> cdrfiles) {
        this.cdrfiles = cdrfiles;
    }

    public Date getDatasetReleased() {
        return datasetReleased;
    }

    public void setDatasetReleased(Date datasetReleased) {
        this.datasetReleased = datasetReleased;
    }

    public List<CrFileDto> getCrfiles() {
        return crfiles;
    }

    public void setCrfiles(List<CrFileDto> crfiles) {
        this.crfiles = crfiles;
    }

    @Override
    public boolean equals(Object oSchema) {
        if (oSchema instanceof Schema) {
            if (oSchema != null && ((Schema) oSchema).getSchema() != null && getSchema() != null) {
                return ((Schema) oSchema).getSchema().equals(getSchema());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return 42;
    }

    /**
     * Gets label
     * @return Label
     */
    public String getLabel() {
        StringBuilder label = new StringBuilder(schema);
        if (id != null && isDDSchema() && getTable() != null) {
            label.append(" - ");
            label.append(getTable());
            label.append(" (");
            label.append(getDataset());
            if (getDatasetReleased() != null) {
                label.append(" - ");
                SimpleDateFormat formatter = new SimpleDateFormat(Properties.dateFormatPattern);
                String strDate = formatter.format(getDatasetReleased());
                label.append(strDate);
            }
            label.append(")");
        }
        return label.toString();
    }

    /**
     * Returns if it is DD schema or not.
     * @return True if it is DD schema.
     */
    public boolean isDDSchema() {
        boolean ret = false;

        if (id != null) {
            ret = id.startsWith("TBL");
        }

        return ret;
    }

    public static String[] getSchemaLanguages() {
        return schemaLanguages;
    }

    public static String getDefaultSchemaLang() {
        return defaultSchemaLang;
    }

    public boolean isDoValidation() {
        return doValidation;
    }

    public void setDoValidation(boolean doValidation) {
        this.doValidation = doValidation;
    }

    /**
     * Gets schema language
     * @return Language
     */
    public String getSchemaLang() {
        if (schemaLang == null) {
            schemaLang = getDefaultSchemaLang();
        }
        return schemaLang;
    }

    public void setSchemaLang(String schemaLang) {
        this.schemaLang = schemaLang;
    }

    public String getUplSchemaFileName() {
        return uplSchemaFileName;
    }

    public void setUplSchemaFileName(String uplSchemaFile) {
        this.uplSchemaFileName = uplSchemaFile;
    }

    /**
     * Returns if schema is expired.
     * @return True if schema is expired.
     */
    public boolean isExpired() {
        Date expDate = getExpireDate();
        Date now = new Date();
        return expDate != null && expDate.before(now);
    }

    /**
     * @return the blocker
     */
    public boolean isBlocker() {
        return blocker;
    }

    /**
     * @param blocker the blocker to set
     */
    public void setBlocker(boolean blocker) {
        this.blocker = blocker;
    }

    /**
     * @return the countStylesheets
     */
    public int getCountStylesheets() {
        return countStylesheets;
    }

    /**
     * @param countStylesheets the countStylesheets to set
     */
    public void setCountStylesheets(int countStylesheets) {
        this.countStylesheets = countStylesheets;
    }

    /**
     * @return the countQaScripts
     */
    public int getCountQaScripts() {
        return countQaScripts;
    }

    /**
     * @param countQaScripts the countQaScripts to set
     */
    public void setCountQaScripts(int countQaScripts) {
        this.countQaScripts = countQaScripts;
    }

    /**
     * @return the stylesheetSchemaId
     */
    public String getStylesheetSchemaId() {
        return stylesheetSchemaId;
    }

    /**
     * @param stylesheetSchemaId the stylesheetSchemaId to set
     */
    public void setStylesheetSchemaId(String stylesheetSchemaId) {
        this.stylesheetSchemaId = stylesheetSchemaId;
    }

    /**
     *
     * @return the maxExecutionTime
     */
    public Long getMaxExecutionTime() {
        return maxExecutionTime;
    }

    /**
     *
     * @param maxExecutionTime the maxExecutionTime to set
     */
    public void setMaxExecutionTime(Long maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    public String getMaxExecutionTimeUI() {
        return maxExecutionTimeUI;
    }

    public void setMaxExecutionTimeUI(String maxExecutionTimeUI) {
        this.maxExecutionTimeUI = maxExecutionTimeUI;
    }
}
