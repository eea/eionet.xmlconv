package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "CDR_REQUESTS")
public class CdrRequestEntry implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "UUID")
    private String uuid;

    @Column(name = "ENVELOPE_URL")
    private String envelopeUrl;

    @Column(name = "NUMBER_OF_JOBS")
    private Integer numberOfJobs;

    @Column(name = "DATE_ADDED")
    private Timestamp dateAdded;

    public CdrRequestEntry() {
    }

    public CdrRequestEntry(String uuid, String envelopeUrl, Integer numberOfJobs, Timestamp dateAdded) {
        this.uuid = uuid;
        this.envelopeUrl = envelopeUrl;
        this.numberOfJobs = numberOfJobs;
        this.dateAdded = dateAdded;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEnvelopeUrl() {
        return envelopeUrl;
    }

    public void setEnvelopeUrl(String envelopeUrl) {
        this.envelopeUrl = envelopeUrl;
    }

    public Integer getNumberOfJobs() {
        return numberOfJobs;
    }

    public void setNumberOfJobs(Integer numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }
}
