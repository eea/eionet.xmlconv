package eionet.gdem.data.scripts;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import eionet.gdem.data.projects.Project;
import eionet.gdem.data.schemata.Schema;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 *
 */
@Entity
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Script {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Project project;

    private String name;

    private String description;

    @Convert(converter = ScriptTypeConverter.class)
    private ScriptType type;

    private String localPath;

    private String remotePath;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Schema> linkedSchemata;

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

    public ScriptType getType() {
        return this.type;
    }

    public void setType(ScriptType type) {
        this.type = type;
    }

    public String getRemotePath() {
        return this.remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public Set<Schema> getLinkedSchemata() {
        return this.linkedSchemata;
    }

    public void setLinkedSchemata(Set<Schema> linkedSchemata) {
        this.linkedSchemata = linkedSchemata;
    }
}
