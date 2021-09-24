package eionet.gdem.jpa.utils;

public enum JobExecutorType {

    Light(0),
    Heavy(1);

    private Integer id;

    public Integer getId() {
        return id;
    }

    JobExecutorType(Integer id) {
        this.id = id;
    }
}
