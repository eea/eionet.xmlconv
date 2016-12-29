package eionet.gdem.services.projects.export;

/**
 *
 */
public interface ProjectMetadataProcessor {

    public String serialize(ProjectMetadata metadata);

    public ProjectMetadata deserialize(String data);
}
