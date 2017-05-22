package eionet.gdem.services.projects.export.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eionet.gdem.data.projects.Project;
import eionet.gdem.services.projects.export.ProjectExporter;
import eionet.gdem.services.projects.export.ProjectStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;


/**
 *
 *
 */
@Component
public class JacksonProjectExporter implements ProjectExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonProjectExporter.class);

    private ProjectStorageService projectStorageService;

    @Autowired
    public JacksonProjectExporter(ProjectStorageService projectStorageService) {
        this.projectStorageService = projectStorageService;
    }

    @Override
    public File export(Project project) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Hibernate5Module());
        mapper.registerModule(new JavaTimeModule());
        try {
            LOGGER.debug("Trying to serialize project: " + project.getId());
            String metadata = mapper.writeValueAsString(project);
            File file = projectStorageService.exportZip(project, metadata);
            LOGGER.debug("Serialization completed");
            return file;
        } catch (JsonProcessingException e) {
            LOGGER.error("Test", e);
        }
        return null;
    }
}
