package eionet.gdem.web.spring.workqueue;

public class JobHistoryMetadata {

    private String status_name;
    private String date_added;
    private String job_executor_name;

    public JobHistoryMetadata(String status_name, String date_added, String job_executor_name) {
        this.status_name = status_name;
        this.date_added = date_added;
        this.job_executor_name = job_executor_name;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getJob_executor_name() {
        return job_executor_name;
    }

    public void setJob_executor_name(String job_executor_name) {
        this.job_executor_name = job_executor_name;
    }
}
