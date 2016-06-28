/**
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator. The Original Code
 * code was developed for the European Environment Agency (EEA) under the
 * IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency. All Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */
package eionet.gdem.conversion.odf;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import eionet.gdem.conversion.spreadsheet.SourceReaderIF;

/**
 * OpenDocument utility class.
 * @author Unknown
 * @author George Sofianos
 */
public class OpenDocumentUtils {

    /**
     * returns a valid SpreadsheetReaderIF
     */
    public static SourceReaderIF getSpreadhseetReader() {
        return new OdsReader();
    }
    /**
     * Returns true, if inputstream is zip file
     * @param input InputStream
     * @return True if InputStream is a zip file.
     */
    public static boolean isSpreadsheetFile(InputStream input) {

        ZipInputStream zipStream = null;
        ZipEntry zipEntry = null;
        try {
            zipStream = new ZipInputStream(input);
            while (zipStream.available() == 1 && (zipEntry = zipStream.getNextEntry()) != null) {
                if (zipEntry != null) {
                    if ("content.xml".equals(zipEntry.getName())) {
                        // content file found, it is OpenDocument.
                        return true;
                    }
                }
            }
        } catch (IOException ioe) {
            return false;
        } finally {
            IOUtils.closeQuietly(zipStream);
        }
        return false;

    }
}
