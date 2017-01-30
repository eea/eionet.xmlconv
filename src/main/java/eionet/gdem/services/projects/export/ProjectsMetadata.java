package eionet.gdem.services.projects.export;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.List;

/**
 *
 */
public class ProjectsMetadata {

    @SerializedName("project")
    private List<Project> projects;

    public ProjectsMetadata() {
        //
    }

    static class Project {
        private String name;
        private String obligation;
        private LocalDate startDate;
        private LocalDate endDate;

        @SerializedName("schema")
        private List<Schema> schemata;
        @SerializedName("script")
        private List<Script> scripts;
        @SerializedName("transformation")
        private List<Transformation> transformations;

    }

    static class Schema {
        private String url;
        private String description;
        private String language;
        private boolean validation;
        private boolean blocking;
        private LocalDate expireDate;

        public Schema() {
            //
        }
    }

    static class Script {
        private String name;
        private String description;
        private int type;
        private List<String> linkedSchemata;
        private boolean active;
        private LocalDate modified;

        public Script() {
            //
        }
    }

    static class Transformation {
        private String name;
        private String description;
        private int type;
        private boolean active;
        private String remoteurl;
        private LocalDate modified;

        public Transformation() {
            //
        }
    }
}
