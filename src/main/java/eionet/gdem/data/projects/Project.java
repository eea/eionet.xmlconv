package eionet.gdem.data.projects;

import eionet.gdem.data.schemata.Schema;
import eionet.gdem.data.scripts.Script;
import eionet.gdem.data.transformations.Transformation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @NotNull
    @Size(min = 5, max = 50)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<Schema> schemata;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<Script> scripts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<Transformation> transformations;

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

    public List<Schema> getSchemata() {
        return schemata;
    }

    public void setSchemata(List<Schema> schemata) {
        this.schemata = schemata;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }

    public List<Transformation> getTransformations() {
        return transformations;
    }

    public void setTransformations(List<Transformation> transformations) {
        this.transformations = transformations;
    }
}
