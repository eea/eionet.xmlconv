package eionet.gdem.jpa.enums;

public enum AlertSeverity {

    LOW(0),
    MEDIUM(1),
    CRITICAL(2);

    private Integer id;

    public Integer getId() {
        return id;
    }

    AlertSeverity(Integer id) {
        this.id = id;
    }
}
