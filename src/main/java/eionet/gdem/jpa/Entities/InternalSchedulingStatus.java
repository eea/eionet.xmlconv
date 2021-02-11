package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "INTERNAL_STATUS")
public class InternalSchedulingStatus implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    public InternalSchedulingStatus() {
    }

    public InternalSchedulingStatus(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public InternalSchedulingStatus setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public InternalSchedulingStatus setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public InternalSchedulingStatus setDescription(String description) {
        this.description = description;
        return this;
    }
}







