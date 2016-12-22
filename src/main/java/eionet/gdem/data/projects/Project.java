package eionet.gdem.data.projects;

import eionet.gdem.data.schemata.Schema;
import eionet.gdem.data.scripts.Script;

import javax.persistence.*;
import java.util.List;

/**
 *
 */
@Entity
@Table
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "project")
    private List<Schema> schemata;

    @OneToMany(mappedBy = "project")
    private List<Script> scripts;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
