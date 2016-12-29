package eionet.gdem.services.projects.export;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 */
public class ProjectImportWrapper {

    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
