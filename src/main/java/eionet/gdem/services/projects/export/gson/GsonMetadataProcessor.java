package eionet.gdem.services.projects.export.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eionet.gdem.services.projects.export.ProjectMetadataProcessor;

import java.io.IOException;
import java.time.LocalDate;

/**
 * RFC 7159 JSON compatible processor.
 * https://tools.ietf.org/html/rfc7159
 *
 */
public class GsonMetadataProcessor implements ProjectMetadataProcessor<GsonMetadata[]> {

    @Override
    public GsonMetadata[] deserialize(String data) throws IOException {

        GsonMetadata metadata = new GsonMetadata();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

        return gson.fromJson(data, GsonMetadata[].class);
    }

}
