package eionet.gdem.data.projects;

import eionet.gdem.data.schemata.Schema;
import eionet.gdem.data.scripts.Script;
import eionet.gdem.data.transformations.Transformation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "project")
    private Set<Schema> schemata;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "project")
    private Set<Script> scripts;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "project")
    private Set<Transformation> transformations;

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

    public Set<Schema> getSchemata() {
        return schemata;
    }

    public void setSchemata(Set<Schema> schemata) {
        this.schemata = schemata;
    }

    public Set<Script> getScripts() {
        return scripts;
    }

    public void setScripts(Set<Script> scripts) {
        this.scripts = scripts;
    }

    public Set<Transformation> getTransformations() {
        return transformations;
    }

    public void setTransformations(Set<Transformation> transformations) {
        this.transformations = transformations;
    }
}
