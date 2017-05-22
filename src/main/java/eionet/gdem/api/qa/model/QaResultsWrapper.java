/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eionet.gdem.api.qa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QaResultsWrapper implements Serializable {
 
    private String jobId;

    private String fileUrl;

    private String scriptId;

    private String scriptTitle;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getScriptId() {
        return this.scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptTitle() {
        return this.scriptTitle;
    }

    public void setScriptTitle(String scriptTitle) {
        this.scriptTitle = scriptTitle;
    }

    public QaResultsWrapper() {
    }

    public QaResultsWrapper(String jobId, String fileUrl, String scriptId, String scriptTitle) {
        this.jobId = jobId;
        this.fileUrl = fileUrl;
        this.scriptId = scriptId;
        this.scriptTitle = scriptTitle;
    }

}
