/*
 * Created on 21.04.2008
 */
package eionet.gdem.test.mocks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.struts.upload.FormFile;

/**
 * The class mocks struts FormFile object.
 * @author Enriko Käsper, TietoEnator Estonia AS
 * MockFormFile
 */

public class MockFormFile implements FormFile {

        private String fileName = null;
        private String contentType = null;
        private int fileSize=0;
        private File file = null;

        public MockFormFile(String s)throws IOException{
            file = new File(s);
            fileName = file.getName();
        }
        public void destroy() {
            // FIXME Auto-generated method stub
        }

        public String getContentType() {
            return contentType;
        }

        public byte[] getFileData() throws FileNotFoundException, IOException {
            return null;
        }

        public String getFileName() {
            return fileName;
        }

        public int getFileSize() {
            return fileSize;
        }

        public InputStream getInputStream() throws FileNotFoundException,
                IOException {
            FileInputStream fis = new FileInputStream(file);
            return fis;
        }

        public void setContentType(String s) {
            contentType = s;

        }

        public void setFileName(String s) {
            fileName = s;
        }

        public void setFileSize(int i) {
            fileSize = i;
        }
    }

