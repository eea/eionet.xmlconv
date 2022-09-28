package eionet.gdem.rabbitMQ.model;

import java.util.List;

public class CdrSummaryResponseMessage {

    private String uuid;
    private Integer numberOfJobs;

    private String envelopeUrl;
    private List<String> jobIds;

    public CdrSummaryResponseMessage() {
    }

    public CdrSummaryResponseMessage(String uuid, Integer numberOfJobs, String envelopeUrl, List<String> jobIds) {
        this.uuid = uuid;
        this.numberOfJobs = numberOfJobs;
        this.envelopeUrl = envelopeUrl;
        this.jobIds = jobIds;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getNumberOfJobs() {
        return numberOfJobs;
    }

    public void setNumberOfJobs(Integer numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
    }

    public List<String> getJobIds() {
        return jobIds;
    }

    public void setJobIds(List<String> jobIds) {
        this.jobIds = jobIds;
    }

    public String getEnvelopeUrl() {
        return envelopeUrl;
    }

    public void setEnvelopeUrl(String envelopeUrl) {
        this.envelopeUrl = envelopeUrl;
    }
}