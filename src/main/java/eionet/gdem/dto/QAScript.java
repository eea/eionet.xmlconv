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
 * Contributor(s):* Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.dto;

import java.io.Serializable;

/**
 * @author Enriko Käsper, Tieto Estonia QAScript
 */

public class QAScript implements Serializable {

    String scriptId;
    String description;
    String shortName;
    String fileName;
    String schemaId;
    String schema;
    String resultType;
    String scriptType;
    String modified;
    String checksum;
    String scriptContent;
    String upperLimit;
    String url;
    boolean active;
    boolean asynchronousExecution;
    Boolean markedHeavy;
    Integer markedHeavyReason;
    String markedHeavyReasonOther;
    String ruleMatch;
    
    private boolean blocker = false;

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    String filePath;

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
     * Default constructor
     */
    public QAScript() {
        super();
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String queryId) {
        this.scriptId = queryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String queryType) {
        this.scriptType = queryType;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setActive(String isActive) {
        this.active = isActive.equals("1");
    } 
    
    public boolean isActive() {
        return this.active;
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

    public boolean isAsynchronousExecution() {
        return asynchronousExecution;
    }

    public void setAsynchronousExecution(boolean asynchronousExecution) {
        this.asynchronousExecution = asynchronousExecution;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isMarkedHeavy() {
        return markedHeavy;
    }

    public Boolean getMarkedHeavy() {
        return markedHeavy;
    }

    public void setMarkedHeavy(Boolean markedHeavy) {
        this.markedHeavy = markedHeavy;
    }

    public Integer getMarkedHeavyReason() {
        return markedHeavyReason;
    }

    public void setMarkedHeavyReason(Integer markedHeavyReason) {
        this.markedHeavyReason = markedHeavyReason;
    }

    public String getMarkedHeavyReasonOther() {
        return markedHeavyReasonOther;
    }

    public void setMarkedHeavyReasonOther(String markedHeavyReasonOther) {
        this.markedHeavyReasonOther = markedHeavyReasonOther;
    }

    public String getRuleMatch() {
        return ruleMatch;
    }

    public void setRuleMatch(String ruleMatch) {
        this.ruleMatch = ruleMatch;
    }
}
