package eionet.gdem.web.spring;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 */
public class FileUploadWrapper {

    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
