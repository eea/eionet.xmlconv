package eionet.gdem.services.projects.export.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eionet.gdem.data.projects.Project;
import eionet.gdem.data.schemata.Schema;
import eionet.gdem.services.projects.export.ProjectExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *
 *
 */
public class ProjectExporterGson implements ProjectExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectExporterGson.class);

    @Override
    public File export(Project project) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Schema.class, new SchemaSerializer())
                .create();

        String result = gson.toJson(project);
        LOGGER.info(result);
        return null;
    }
}
