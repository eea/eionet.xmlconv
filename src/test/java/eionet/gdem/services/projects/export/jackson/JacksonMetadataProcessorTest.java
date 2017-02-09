package eionet.gdem.services.projects.export.jackson;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 *
 */
public class JacksonMetadataProcessorTest {
    @Test
    public void serialize() throws Exception {

    }

    @Test
    public void deserialize() throws Exception {
        JacksonMetadataProcessor projectMetadataProcessor = new JacksonMetadataProcessor();
        JacksonMetadata[] metadata = projectMetadataProcessor.deserialize("[ { \"id\":1 }]");
        for (JacksonMetadata m : metadata) {
            assertEquals("Wrong id found", 1, m.getId());
        }
    }

}