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
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "project")
    private List<Schema> schemata;

    @OneToMany(mappedBy = "project")
    private List<Script> scripts;

}
