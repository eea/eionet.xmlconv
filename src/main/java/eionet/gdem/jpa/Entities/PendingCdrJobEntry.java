package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "PENDING_CDR_JOBS")
public class PendingCdrJobEntry implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "JOB_ID")
    private Integer jobId;

    @Column(name = "UUID")
    private String uuid;

    @Column(name = "N_STATUS")
    private Integer nStatus;

    @Column(name = "DATE_ADDED")
    private Timestamp dateAdded;

    public PendingCdrJobEntry() {
    }

    public PendingCdrJobEntry(Integer jobId, String uuid, Integer nStatus, Timestamp dateAdded) {
        this.jobId = jobId;
        this.uuid = uuid;
        this.nStatus = nStatus;
        this.dateAdded = dateAdded;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getnStatus() {
        return nStatus;
    }

    public void setnStatus(Integer nStatus) {
        this.nStatus = nStatus;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }
}
