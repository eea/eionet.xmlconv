package eionet.gdem.jpa.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SCRIPT_RULES")
public class ScriptRulesEntry implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "QUERY_ID")
    private Integer queryId;

    @Column(name = "FIELD")
    private String field;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ENABLED")
    private boolean enabled;

    public Integer getId() {
        return id;
    }

    public ScriptRulesEntry setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getQueryId() {
        return queryId;
    }

    public ScriptRulesEntry setQueryId(Integer queryId) {
        this.queryId = queryId;
        return this;
    }

    public String getField() {
        return field;
    }

    public ScriptRulesEntry setField(String field) {
        this.field = field;
        return this;
    }

    public String getType() {
        return type;
    }

    public ScriptRulesEntry setType(String
                                            type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ScriptRulesEntry setValue(String value) {
        this.value = value;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ScriptRulesEntry setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ScriptRulesEntry setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}




















