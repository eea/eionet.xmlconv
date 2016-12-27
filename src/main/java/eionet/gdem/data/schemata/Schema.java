package eionet.gdem.data.schemata;

import eionet.gdem.data.projects.Project;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 */
@Entity
@Table(name = "`Schema`")
public class Schema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String url;

    private String description;

    @Convert(converter = SchemaLanguageConverter.class)
    @Column(name = "schema_language")
    private SchemaLanguage schemaLanguage;

    private boolean validation;

    private boolean blocking;

    //TODO change to date
    @Column(name = "expire_date")
    private String expireDate;

    @Column(name = "local_path")
    private String localPath;

    @ManyToOne
    private Project project;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SchemaLanguage getSchemaLanguage() {
        return schemaLanguage;
    }

    public void setSchemaLanguage(SchemaLanguage schemaLanguage) {
        this.schemaLanguage = schemaLanguage;
    }

    public boolean isValidation() {
        return validation;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
