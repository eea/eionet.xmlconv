package eionet.gdem.data.schemata;

import eionet.gdem.data.projects.Project;

import javax.persistence.*;

/**
 *
 */
@Entity
@Table
public class Schema {

    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;
}
