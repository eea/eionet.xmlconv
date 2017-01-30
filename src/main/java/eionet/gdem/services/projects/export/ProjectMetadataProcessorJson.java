package eionet.gdem.services.projects.export;

import com.google.gson.Gson;

import java.util.Set;

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
    public ProjectsMetadata deserialize(String data) {

        ProjectsMetadata metadata = new ProjectsMetadata();
        Gson gson = new Gson();

        return gson.fromJson(data, ProjectsMetadata.class);
    }
}
