/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Nenad Popovic (ED)
 */
package eionet.gdem.utils.uimanage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FSUtil {
    /**
     * method for uploading files
     * 
     * @param path
     *            Directory where file will be uploaded
     * @param filename
     *            Name of file
     * @param InputStream
     *            Stream to be written to a file
     */
    public void uploadFile(String path, String filename, InputStream in) throws IOException, NullPointerException {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            OutputStream w = new FileOutputStream(path + File.separatorChar + filename);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
                w.write(buffer, 0, bytesRead);
            }
            w.close();
            in.close();
        } catch (IOException IO) {
            throw new IOException("Error in writting file");
        } catch (NullPointerException np) {
            throw new NullPointerException("Path name is null");
        }
    }

    /**
     * method for file deletion
     * 
     * @param path
     *            Directory where file is located
     * @param filename
     *            Name of file
     */
    public void deleteFile(String path, String filename) throws IOException {
        try {
            File delFile = new File(path + File.separatorChar + filename);
            delFile.delete();
        } catch (NullPointerException np) {
            throw new NullPointerException("Error in deleting file: Path or file name is null");
        }
    }

    /**
     * method for getting file names in directory specified by path
     * 
     * @param path
     *            Directory where files are located
     * @return Array of file names in directory specified in path
     */
    public String[] listFiles(String path) throws NullPointerException {
        try {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    String[] suppTypes =
                            new String[] {".gif", ".jpg", ".png", ".jpeg", ".jp2", ".bmp", ".tiff", ".tiff-fx", ".cgm"};
                    boolean isSuppFile = false;
                    for (int i = 0; i < suppTypes.length; i++) {
                        if (name.toLowerCase().endsWith(suppTypes[i])) {
                            isSuppFile = true;
                        }
                    }
                    return isSuppFile;
                }
            };
            File dir = new File(path);
            return dir.list(filter);
        } catch (NullPointerException np) {
            throw new NullPointerException("Error: Path name is null");
        }
    }

}
