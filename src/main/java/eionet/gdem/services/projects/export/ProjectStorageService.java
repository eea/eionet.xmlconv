package eionet.gdem.services.projects.export;

import eionet.gdem.data.projects.Project;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
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

    public File exportZip(Project project, String metadata) {
        String projectDir = "/home/dev-gso/eea-run/eionet.xmlconv/projects/" + project.getId();
        Map<String, Object> env = new HashMap<>();
        env.put("create", "true");
        env.put("useTempFile", Boolean.TRUE);
        FileSystems.getDefault().getPath("/tmp/test.zip");
        URI uri = URI.create("jar:file:/tmp/test.zip");
        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            Path projectDirPath = Paths.get(projectDir);
            Iterable<Path> pathInZip = zipfs.getRootDirectories();
            Path root = pathInZip.iterator().next();
            /*Files.copy(parentDirPath, pathInZip, StandardCopyOption.REPLACE_EXISTING);*/
            Files.walkFileTree(projectDirPath, new CopyFileVisitor(root));
            Path metadataPath = Files.createTempFile("metadata", "json");
            Files.write(metadataPath, metadata.getBytes(StandardCharsets.UTF_8));
            Files.copy(metadataPath, zipfs.getPath(root.toString(), "metadata.json"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Error while creating zip", e);
        }
        return new File(uri.getPath());
    }

    private static class CopyFileVisitor extends SimpleFileVisitor<Path> {
        private final Path targetPath;
        private Path sourcePath = null;
        public CopyFileVisitor(Path targetPath) {
            this.targetPath = targetPath;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir,
                                                 final BasicFileAttributes attrs) throws IOException {
            if (sourcePath == null) {
                sourcePath = dir;
            } else {
                Files.createDirectories(targetPath.resolve(sourcePath
                        .relativize(dir).toString()));
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file,
                                         final BasicFileAttributes attrs) throws IOException {
            Files.copy(file,
                    targetPath.resolve(sourcePath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
            return FileVisitResult.CONTINUE;
        }
    }

}
