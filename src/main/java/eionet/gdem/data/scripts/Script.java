package eionet.gdem.data.scripts;

import eionet.gdem.data.projects.Project;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table
public class Script {

    @Id
    private Integer id;

    @ManyToOne()
    private Project project;
}
