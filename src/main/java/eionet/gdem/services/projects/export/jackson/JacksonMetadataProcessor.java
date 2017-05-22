package eionet.gdem.services.projects.export.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eionet.gdem.services.projects.export.ProjectMetadataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 *
 *
 */
public class JacksonMetadataProcessor implements ProjectMetadataProcessor<JacksonMetadata[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonMetadataProcessor.class);

    @Override
    public JacksonMetadata[] deserialize(String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        mapper.registerModule(new Hibernate5Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        return mapper.readValue(data, JacksonMetadata[].class);
    }
}
