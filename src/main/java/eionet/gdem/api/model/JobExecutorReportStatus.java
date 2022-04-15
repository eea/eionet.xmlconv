package eionet.gdem.api.model;

public class JobExecutorReportStatus {

    Integer lightJobExecutorInstancesRunning;
    Integer heavyJobExecutorInstancesRunning;
    Integer fmeSyncJobExecutorInstancesRunning;
    Integer fmeAsyncJobExecutorInstancesRunning;

    public Integer getLightJobExecutorInstancesRunning() {
        return lightJobExecutorInstancesRunning;
    }

    public JobExecutorReportStatus setLightJobExecutorInstancesRunning(Integer lightJobExecutorInstancesRunning) {
        this.lightJobExecutorInstancesRunning = lightJobExecutorInstancesRunning;
        return this;
    }

    public Integer getHeavyJobExecutorInstancesRunning() {
        return heavyJobExecutorInstancesRunning;
    }

    public JobExecutorReportStatus setHeavyJobExecutorInstancesRunning(Integer heavyJobExecutorInstancesRunning) {
        this.heavyJobExecutorInstancesRunning = heavyJobExecutorInstancesRunning;
        return this;
    }

    public Integer getFmeSyncJobExecutorInstancesRunning() {
        return fmeSyncJobExecutorInstancesRunning;
    }

    public JobExecutorReportStatus setFmeSyncJobExecutorInstancesRunning(Integer fmeSyncJobExecutorInstancesRunning) {
        this.fmeSyncJobExecutorInstancesRunning = fmeSyncJobExecutorInstancesRunning;
        return this;
    }

    public Integer getFmeAsyncJobExecutorInstancesRunning() {
        return fmeAsyncJobExecutorInstancesRunning;
    }

    public JobExecutorReportStatus setFmeAsyncJobExecutorInstancesRunning(Integer fmeAsyncJobExecutorInstancesRunning) {
        this.fmeAsyncJobExecutorInstancesRunning = fmeAsyncJobExecutorInstancesRunning;
        return this;
    }
}
