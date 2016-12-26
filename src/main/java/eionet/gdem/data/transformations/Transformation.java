package eionet.gdem.data.transformations;

import eionet.gdem.data.projects.Project;

import javax.persistence.*;

/**
 *
 */
@Entity
@Table
public class Transformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Project project;

}
