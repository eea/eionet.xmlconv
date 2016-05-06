/**
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
 * The Original Code is "GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA).
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 * Created on 20.07.2006
 */
package eionet.gdem.conversion.odf;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import eionet.gdem.GDEMException;

/**
 * Processes OpenDocument files.
 * @author Unknown
 * @author George Sofianos
 */
public class OpenDocumentProcessor {

    /**
     * This class is creating handlers for creating OpenDocument file from xml called from ConversionService
     */
    public OpenDocumentProcessor() {
    }

    /**
     * Creates ODS Spreadsheet
     * @param sIn Input String
     * @param sOut Output String
     * @throws GDEMException If an error occurs.
     */
    public void makeSpreadsheet(String sIn, String sOut) throws GDEMException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(sOut);
            makeSpreadsheet(sIn, out);
        } catch (Exception e) {
            throw new GDEMException("ErrorConversionHandler - couldn't save the OpenDocumentSpreadheet file: " + e.toString(), e);
        }
        finally{
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Creates ODS Spreadsheet
     * @param sIn Input String
     * @param sOut Output String
     * @throws GDEMException If an error occurs.
     */
    public void makeSpreadsheet(String sIn, OutputStream sOut) throws GDEMException {

        if (sIn == null) {
            return;
        }
        if (sOut == null) {
            return;
        }

        try {
            OpenDocument od = new OpenDocument();
            od.setContentFile(sIn);
            od.createOdsFile(sOut);
        } catch (Exception e) {
            throw new GDEMException("Error generating OpenDocument Spreadsheet file: " + e.toString(), e);
        }

        return;
    }
}
