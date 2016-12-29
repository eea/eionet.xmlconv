package eionet.gdem.services.projects.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 *
 */
@Component
public class ProjectStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectStorageService.class);

    public int importProject(ProjectImportWrapper fileWrapper) {
        try (ZipInputStream zin = new ZipInputStream(fileWrapper.getFile().getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry ze = zin.getNextEntry();
            while (ze != null) {
                LOGGER.info(ze.getName());
                ze = zin.getNextEntry();
            }
            return 0;
        } catch (IOException e) {
            LOGGER.error("Unexpected error while importing project:" + e);
        }
        return 1;
    }
}
