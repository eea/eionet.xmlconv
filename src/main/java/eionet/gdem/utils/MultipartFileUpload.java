/**
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem.utils;

import java.io.File;

public class MultipartFileUpload {

    private MultipartFileUpload() {}

    /**
     * Generates filename.
     *
     * @param fileName File name
     * @param n set larger than 0, if file with the same name already exists in the tmp folder ex: genFileName( test.xls, 1 )= test_1.xls
     *            genFileName( test_1.xls, 2 )= test_2.xls
     */
    public static String getGeneratedFileName(String fileName, int n) {
        String ret;
        int pos = fileName.lastIndexOf(".");

        int dashPos = fileName.lastIndexOf("_");
        if (dashPos > 1 && dashPos < pos) {
            String snum = fileName.substring(dashPos + 1, pos);
            try {
                int inum = Integer.parseInt(snum);
                ret = fileName.substring(0, dashPos) + "_" + (inum + 1) + fileName.substring(pos);
            } catch (Exception e) {
                ret = fileName.substring(0, pos) + "_" + n + fileName.substring(pos);
            }
        } else {
            ret = fileName.substring(0, pos) + "_" + n + fileName.substring(pos);
        }

        return ret;
    }

    /**
     * Finds unique filename using genFileName method.
     *
     * @param folderName
     *            Folder where the file will be stored
     * @param fileName
     *            File name that should be used for generating the unique filename
     *
     * @return Filename that does not exist in the folder
     */
    public static File getUniqueFile(String folderName, String fileName) {
        int n = 0;
        File file = new File(folderName, fileName);
        while (file.exists()) {
            n++;
            fileName = getGeneratedFileName(fileName, n);
            file = new File(folderName, fileName);
        }

        return file;
    }

    /**
     * Returns unique file name
     * @param folderName Directory name
     * @param fileName File name
     * @return Unique file name
     */
    public static String getUniqueFileName(String folderName, String fileName) {
        File file = getUniqueFile(folderName, fileName);
        return file.getName();
    }

}
