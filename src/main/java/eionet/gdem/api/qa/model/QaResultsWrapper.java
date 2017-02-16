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

    public QaResultsWrapper() {
    }

    public QaResultsWrapper(String jobId, String fileUrl) {
        this.jobId = jobId;
        this.fileUrl = fileUrl;
    }

}
