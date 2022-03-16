package eionet.gdem.jpa.utils;

public enum JobExecutorType {

    Light(0),
    Heavy(1),
    Unknown(2),
    Sync_fme(3),
    Async_fme(4);

    private Integer id;

    public Integer getId() {
        return id;
    }

    JobExecutorType(Integer id) {
        this.id = id;
    }
}
