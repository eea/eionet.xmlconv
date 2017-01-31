package eionet.gdem.services.projects.export;

import eionet.gdem.data.projects.Project;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 */
@Component
public class ProjectStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectStorageService.class);

    public int importProject(ProjectImportWrapper fileWrapper) {
        ProjectsMetadata[] projectMetadata = null;
        try (ZipInputStream zin = new ZipInputStream(fileWrapper.getFile().getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry ze = zin.getNextEntry();
            while (ze != null) {
                LOGGER.info(ze.getName());
                if ("metadata.json".equals(ze.getName())) {
                    LOGGER.info("Found metadata, starting validation. ");
                    ProjectMetadataProcessor processor = new ProjectMetadataProcessorJson();
                    ByteArrayOutputStream metadataOutputStream = new ByteArrayOutputStream();
                    IOUtils.copy(zin, metadataOutputStream);
                    projectMetadata = processor.deserialize(metadataOutputStream.toString(StandardCharsets.UTF_8.name()));
                }
                ze = zin.getNextEntry();
            }
            return 0;
        } catch (IOException e) {
            LOGGER.error("Unexpected error while importing project:" + e);
        }
        return 1;
    }

}
