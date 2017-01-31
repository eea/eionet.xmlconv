package eionet.gdem.services.projects.export;

/**
 *
 */
public interface ProjectMetadataProcessor {

    public String serialize(ProjectsMetadata metadata);

    public ProjectsMetadata[] deserialize(String data);
}
