package eionet.gdem.api.model;

public class ApplicationStatus {

    public enum Status {

        UP("up"),
        DOWN("down");

        private String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private String databaseConnection;
    private String rabbitmqConnection;
    private String rancherConnection;
    private JobExecutorReportStatus jobExecutorReportStatus;

    public String getDatabaseConnection() {
        return databaseConnection;
    }

    public ApplicationStatus setDatabaseConnection(String databaseConnection) {
        this.databaseConnection = databaseConnection;
        return this;
    }

    public String getRabbitmqConnection() {
        return rabbitmqConnection;
    }

    public ApplicationStatus setRabbitmqConnection(String rabbitmqConnection) {
        this.rabbitmqConnection = rabbitmqConnection;
        return this;
    }

    public String getRancherConnection() {
        return rancherConnection;
    }

    public ApplicationStatus setRancherConnection(String rancherConnection) {
        this.rancherConnection = rancherConnection;
        return this;
    }

    public JobExecutorReportStatus getJobExecutorReportStatus() {
        return jobExecutorReportStatus;
    }

    public ApplicationStatus setJobExecutorReportStatus(JobExecutorReportStatus jobExecutorReportStatus) {
        this.jobExecutorReportStatus = jobExecutorReportStatus;
        return this;
    }
}
