package eionet.gdem.services.projects.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import eionet.gdem.services.projects.export.gson.LocalDateAdapter;

import java.time.LocalDate;

/**
 * RFC 7159 JSON compatible processor.
 * https://tools.ietf.org/html/rfc7159
 *
 */
public class ProjectMetadataProcessorJson implements ProjectMetadataProcessor {


    @Override
    public String serialize(ProjectsMetadata metadata) {

        return null;
    }

    @Override
    public ProjectsMetadata[] deserialize(String data) {

        ProjectsMetadata metadata = new ProjectsMetadata();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

        return gson.fromJson(data, ProjectsMetadata[].class);
    }
}
