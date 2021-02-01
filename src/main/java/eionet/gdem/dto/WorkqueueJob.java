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
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.dto;

import java.util.Date;

/**
 * WorkqueueJob.
 *
 * @author Enriko Käsper, Tieto Estonia
 */

public class WorkqueueJob {

    private String jobId;
    private String url;
    private String scriptFile;
    private String resultFile;
    private int status;
    private String scriptId;
    private String srcFile;
    private Date jobTimestamp;
    private Long duration;

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId
     *            the jobId to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the scriptFile
     */
    public String getScriptFile() {
        return scriptFile;
    }

    /**
     * @param scriptFile
     *            the scriptFile to set
     */
    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }

    /**
     * @return the resultFile
     */
    public String getResultFile() {
        return resultFile;
    }

    /**
     * @param resultFile
     *            the resultFile to set
     */
    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the scriptId
     */
    public String getScriptId() {
        return scriptId;
    }

    /**
     * @param scriptId
     *            the scriptId to set
     */
    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    /**
     * @return the srcFile
     */
    public String getSrcFile() {
        return srcFile;
    }

    /**
     * @param srcFile
     *            the srcFile to set
     */
    public void setSrcFile(String srcFile) {
        this.srcFile = srcFile;
    }

    /**
     * @return the jobTimestamp
     */
    public Date getJobTimestamp() {
        return jobTimestamp;
    }

    /**
     * @param jobTimestamp
     *            the jobTimestamp to set
     */
    public void setJobTimestamp(Date jobTimestamp) {
        this.jobTimestamp = jobTimestamp;
    }

    /**
     *
     * @return the job's duration
     */
    public Long getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     *          the job's duration to set
     */
    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
