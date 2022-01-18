package eionet.gdem.jpa.Entities;

import eionet.gdem.jpa.utils.PropertiesEntryType;
import eionet.gdem.jpa.utils.PropertiesEntryTypeConverter;

import javax.persistence.*;

@Entity
@Table(name = "PROPERTIES")
public class PropertiesEntry {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Convert(converter = PropertiesEntryTypeConverter.class)
    @Column(name = "TYPE")
    private PropertiesEntryType type;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "DESCRIPTION")
    private String description;

    public PropertiesEntry() {
    }

    public PropertiesEntry(String name, PropertiesEntryType type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public PropertiesEntry setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PropertiesEntry setName(String name) {
        this.name = name;
        return this;
    }

    public PropertiesEntryType getType() {
        return type;
    }

    public PropertiesEntry setType(PropertiesEntryType type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public PropertiesEntry setValue(String value) {
        this.value = value;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PropertiesEntry setDescription(String description) {
        this.description = description;
        return this;
    }
}
