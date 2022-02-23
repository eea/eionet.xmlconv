package eionet.gdem.jpa.Entities;

import eionet.gdem.jpa.enums.AlertSeverity;
import eionet.gdem.jpa.enums.AlertSeverityTypeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "ALERTS")
public class AlertEntry implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Convert(converter = AlertSeverityTypeConverter.class)
    @Column(name = "SEVERITY")
    private AlertSeverity severity;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "NOTIFICATION_SENT_TO_UNS")
    private boolean notificationSentToUns;

    @Column(name = "OCCURRENCE_DATE")
    private Timestamp occurrenceDate;

    @Transient
    private String occurrenceDateMod;

    public AlertEntry() {
    }

    public Integer getId() {
        return id;
    }

    public AlertEntry setId(Integer id) {
        this.id = id;
        return this;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public AlertEntry setSeverity(AlertSeverity severity) {
        this.severity = severity;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AlertEntry setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isNotificationSentToUns() {
        return notificationSentToUns;
    }

    public AlertEntry setNotificationSentToUns(boolean notificationSentToUns) {
        this.notificationSentToUns = notificationSentToUns;
        return this;
    }

    public Timestamp getOccurrenceDate() {
        return occurrenceDate;
    }

    public AlertEntry setOccurrenceDate(Timestamp occurrenceDate) {
        this.occurrenceDate = occurrenceDate;
        return this;
    }

    public String getOccurrenceDateMod() {
        return occurrenceDateMod;
    }

    public void setOccurrenceDateMod(String occurrenceDateMod) {
        this.occurrenceDateMod = occurrenceDateMod;
    }

    @Override
    public String toString() {
        return "AlertEntry{" +
                "id=" + id +
                ", severity=" + severity +
                ", description='" + description + '\'' +
                ", notificationSentToUns=" + notificationSentToUns +
                ", occurrenceDate=" + occurrenceDate +
                '}';
    }
}
