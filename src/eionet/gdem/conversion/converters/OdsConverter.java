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
package eionet.gdem.conversion.converters;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eionet.gdem.GDEMException;
import eionet.gdem.conversion.OpenDocumentProcessor;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;

public class OdsConverter extends ConvertStartegy {
    private static LoggerIF _logger = GDEMServices.getLogger();

    public String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws GDEMException,
            Exception {
        FileOutputStream xmlOut = null;
        String xmlFile = tmpFolder + "gdem_out" + System.currentTimeMillis() + ".xml";
        String odsFile = tmpFolder + "gdem_out" + System.currentTimeMillis() + ".ods";

        try {
            xmlOut = new FileOutputStream(xmlFile);
            runXslTransformation(source, xslt, xmlOut);
            OpenDocumentProcessor odp = new OpenDocumentProcessor();
            if (result != null)
                odp.makeSpreadsheet(xmlFile, result);
            else
                odp.makeSpreadsheet(xmlFile, odsFile);

        } catch (FileNotFoundException e) {
            _logger.error("Error " + e.toString(), e);
            throw new GDEMException("Error transforming OpenDocument Spreadhseet " + e.toString(), e);
        } finally {
            if (xmlOut != null) {
                try {
                    xmlOut.close();
                } catch (IOException ioe) {
                }
            }
        }
        try {
            Utils.deleteFile(xmlFile);
        } catch (Exception e) {
            _logger.error("Couldn't delete the result file: " + xmlFile, e);
        }

        return odsFile;
    }

}
