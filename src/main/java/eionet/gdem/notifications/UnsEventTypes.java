package eionet.gdem.notifications;

public enum UnsEventTypes {

    LONG_RUNNING_JOBS(0),
    RANCHER_CIRCUIT_BREAKER(1);

    private Integer id;

    public Integer getId() {
        return id;
    }

    UnsEventTypes(Integer id) {
        this.id = id;
    }
}
