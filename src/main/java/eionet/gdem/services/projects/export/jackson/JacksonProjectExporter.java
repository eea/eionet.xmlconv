package eionet.gdem.services.projects.export.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import eionet.gdem.data.projects.Project;
import eionet.gdem.services.projects.export.ProjectExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 */
public class JacksonProjectExporter implements ProjectExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonProjectExporter.class);

    @Override
    public void export(Project project) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Hibernate5Module());
        try {
            String result = mapper.writeValueAsString(project);
            LOGGER.info(result);
        } catch (JsonProcessingException e) {
            LOGGER.error("Test", e);
        }
    }
}
