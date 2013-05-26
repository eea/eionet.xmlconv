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
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */

package eionet.gdem.dto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.GDEMException;
import eionet.gdem.utils.Utils;

/**
 * The object stores all the needed information about the converted file.
 *
 * @author Enriko Käsper
 */
public class ConvertedFileDto {
    /** */
    private static final Log LOGGER = LogFactory.getLog(ConvertedFileDto.class);

    private String fileName;
    private String filePath;
    private String fileUrl;

    /**
     * Class constructor.
     *
     * @param fileName
     * @param filePath
     */
    public ConvertedFileDto(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public byte[] getFileContentAsByteArray() throws GDEMException {
        FileInputStream fis = null;
        File convFile = new File(getFilePath());
        byte[] result;
        try {
            fis = new FileInputStream(convFile);
            result = IOUtils.toByteArray(fis);
        } catch (IOException e) {
            LOGGER.error("Converted file not found: " + getFilePath());
            throw new GDEMException("Converted file not found: " + getFileName());
        } finally {
            IOUtils.closeQuietly(fis);
        }
        Utils.deleteFile(convFile);
        return result;
    }
}
