package eionet.gdem.jpa.utils;

public enum PropertiesEntryType {

    Integer(0),
    Long(1),
    Big_Integer(2),
    String(3),
    Date(4);

    private Integer id;

    public Integer getId() {
        return id;
    }

    PropertiesEntryType(Integer id) {
        this.id = id;
    }
}
