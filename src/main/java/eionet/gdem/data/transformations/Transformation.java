package eionet.gdem.data.transformations;

import eionet.gdem.data.projects.Project;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    private String name;

    private String description;

    @Convert(converter = TransformationTypeConverter.class)
    private TransformationType type;

    private String localPath;

    private LocalDateTime lastModified;

    private boolean active;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransformationType getType() {
        return this.type;
    }

    public void setType(TransformationType type) {
        this.type = type;
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public LocalDateTime getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
