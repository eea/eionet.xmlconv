package eionet.gdem.services.projects.export.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import eionet.gdem.data.obligations.Obligation;
import eionet.gdem.data.schemata.Schema;
import eionet.gdem.data.scripts.Script;
import eionet.gdem.data.transformations.Transformation;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 *
 */
/*@JsonIgnoreProperties(ignoreUnknown = true)*/
public class JacksonMetadata {

    private int id;
    private String name;
    private List<ObligationMetadata> obligations;
    private List<SchemaMetadata> schemata;
    private List<ScriptMetadata> scripts;
    private List<TransformationMetadata> transformations;

    public JacksonMetadata() {
        //
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObligationMetadata> getObligations() {
        return this.obligations;
    }

    public void setObligations(List<ObligationMetadata> obligations) {
        this.obligations = obligations;
    }

    public Set<Obligation> getObligationsObj() {
        Set<Obligation> tmp = new LinkedHashSet<>();
        for (ObligationMetadata ob : this.obligations) {
            Obligation obligation = new Obligation();
            obligation.setId(ob.getId());
            obligation.setActive(ob.isActive());
            obligation.setUrl(ob.getUrl());
            tmp.add(obligation);
        }
        return tmp;
    }

    public Set<Schema> getSchemataObj() {
        Set<Schema> tmp = new LinkedHashSet<>();
        for (SchemaMetadata sc : this.schemata) {
            Schema schema = new Schema();
            schema.setId(sc.getId());
            schema.setUrl(sc.getUrl());
            schema.setBlocking(sc.isBlocking());
            schema.setValidation(sc.isValidation());
            schema.setDescription(sc.getDescription());
            schema.setExpireDate(sc.getExpireDate());
            /*schema.setSchemaLanguage(sc.);*/
            tmp.add(schema);
        }
        return tmp;
    }

    public Set<Script> getScriptsObj() {
        Set<Script> tmp = new LinkedHashSet<>();
        for (ScriptMetadata sc : this.scripts) {
            Script script = new Script();
            /*script.setId(sc.get);*/
            script.setName(sc.getName());
            script.setDescription(sc.getDescription());
            script.setActive(sc.isActive());
            /*script.setType(sc.getType());*/
            /*script.setLastModified(sc.getModified());*/
            /*script.setLinkedSchemata(sc.getLinkedSchemata());*/
            /*script.setRemotePath(sc.get);*/
            /*script.setLocalPath();*/
            tmp.add(script);
        }
        return tmp;
    }

    public Set<Transformation> getTransformationsObj() {
        Set<Transformation> tmp = new LinkedHashSet<>();
        for (TransformationMetadata tr : this.transformations) {
            Transformation transformation = new Transformation();
            transformation.setName(tr.getName());
            /*transformation.setProject();*/
            transformation.setActive(tr.isActive());
            /*transformation.setLastModified(tr.getModified());*/
            transformation.setDescription(tr.getDescription());
            transformation.setRemotePath(tr.getRemoteurl());
            /*transformation.setLocalPath(tr.get);*/
            tmp.add(transformation);
        }
        return tmp;
    }

    public List<SchemaMetadata> getSchemata() {
        return schemata;
    }

    public void setSchemata(List<SchemaMetadata> schemata) {
        this.schemata = schemata;
    }

    public List<ScriptMetadata> getScripts() {
        return scripts;
    }

    public void setScripts(List<ScriptMetadata> scripts) {
        this.scripts = scripts;
    }

    public List<TransformationMetadata> getTransformations() {
        return transformations;
    }

    public void setTransformations(List<TransformationMetadata> transformations) {
        this.transformations = transformations;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ObligationMetadata {
        private int id;
        private String url;
        private boolean active;

        public ObligationMetadata() {
            //
        }

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isActive() {
            return this.active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SchemaMetadata {
        private int id;
        private String url;
        private String description;
        private String language;
        private boolean validation;
        private boolean blocking;
        private LocalDate expireDate;

        public SchemaMetadata() {
            //
        }

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLanguage() {
            return this.language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public boolean isValidation() {
            return this.validation;
        }

        public void setValidation(boolean validation) {
            this.validation = validation;
        }

        public boolean isBlocking() {
            return this.blocking;
        }

        public void setBlocking(boolean blocking) {
            this.blocking = blocking;
        }

        public LocalDate getExpireDate() {
            return this.expireDate;
        }

        public void setExpireDate(LocalDate expireDate) {
            this.expireDate = expireDate;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ScriptMetadata {
        private String name;
        private String description;
        private String type;
        private List<SchemaMetadata> linkedSchemata;
        private boolean active;
        private LocalDate modified;

        public ScriptMetadata() {
            //
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

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }


        public List<SchemaMetadata> getLinkedSchemata() {
            return linkedSchemata;
        }

        public void setLinkedSchemata(List<SchemaMetadata> linkedSchemata) {
            this.linkedSchemata = linkedSchemata;
        }

        public boolean isActive() {
            return this.active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public LocalDate getModified() {
            return this.modified;
        }

        public void setModified(LocalDate modified) {
            this.modified = modified;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class TransformationMetadata {
        private String name;
        private String description;
        private String type;
        private boolean active;
        private String remoteurl;
        private LocalDate modified;

        public TransformationMetadata() {
            //
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

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isActive() {
            return this.active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getRemoteurl() {
            return this.remoteurl;
        }

        public void setRemoteurl(String remoteurl) {
            this.remoteurl = remoteurl;
        }

        public LocalDate getModified() {
            return this.modified;
        }

        public void setModified(LocalDate modified) {
            this.modified = modified;
        }
    }
}
