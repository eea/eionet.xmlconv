package eionet.gdem.services.projects.export.gson;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;
import java.util.List;

/**
 *
 */
public class GsonMetadata {

    private String name;
    private String obligation;
    private LocalDate startDate;
    private LocalDate endDate;

    @SerializedName("schemata")
    private List<Schema> schemata;
    @SerializedName("scripts")
    private List<Script> scripts;
    @SerializedName("transformations")
    private List<Transformation> transformations;

    public GsonMetadata() {
        //
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
