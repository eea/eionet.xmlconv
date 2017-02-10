package eionet.gdem.services.projects.export.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;

/**
 *
 *
 */
/*@JsonIgnoreProperties(ignoreUnknown = true)*/
public class JacksonMetadata {

    private int id;
    private String name;
    private List<String> obligations;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Schema> schemata;
    private List<Script> scripts;
    private List<Transformation> transformations;

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

    public List<String> getObligations() {
        return this.obligations;
    }

    public void setObligations(List<String> obligations) {
        this.obligations = obligations;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Schema> getSchemata() {
        return schemata;
    }

    public void setSchemata(List<Schema> schemata) {
        this.schemata = schemata;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }

    public List<Transformation> getTransformations() {
        return transformations;
    }

    public void setTransformations(List<Transformation> transformations) {
        this.transformations = transformations;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Schema {
        private int id;
        private String url;
        private String description;
        private String language;
        private boolean validation;
        private boolean blocking;
        private LocalDate expireDate;

        public Schema() {
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
    static class Script {
        private String name;
        private String description;
        private String type;
        private List<Schema> linkedSchemata;
        private boolean active;
        private LocalDate modified;

        public Script() {
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


        public List<Schema> getLinkedSchemata() {
            return linkedSchemata;
        }

        public void setLinkedSchemata(List<Schema> linkedSchemata) {
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
    static class Transformation {
        private String name;
        private String description;
        private String type;
        private boolean active;
        private String remoteurl;
        private LocalDate modified;

        public Transformation() {
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
