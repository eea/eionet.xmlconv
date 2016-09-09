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
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;



import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.odf.OpenDocumentProcessor;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdsConverter extends ConvertStrategy {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(OdsConverter.class);

    @Override
    public String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws XMLConvException,
    Exception {
        FileOutputStream xmlOut = null;
        String xmlFile =  Utils.getUniqueTmpFileName(".xml");
        String odsFile =  Utils.getUniqueTmpFileName(".ods");

        try {
            xmlOut = new FileOutputStream(xmlFile);
            runXslTransformation(source, xslt, xmlOut);
            OpenDocumentProcessor odp = new OpenDocumentProcessor();
            if (result != null) {
                odp.makeSpreadsheet(xmlFile, result);
            } else {
                odp.makeSpreadsheet(xmlFile, odsFile);
            }

        } catch (FileNotFoundException e) {
            LOGGER.error("Error " + e.toString(), e);
            throw new XMLConvException("Error transforming OpenDocument Spreadhseet " + e.toString(), e);
        } finally {
            IOUtils.closeQuietly(xmlOut);
        }
        try {
            Utils.deleteFile(xmlFile);
        } catch (Exception e) {
            LOGGER.error("Couldn't delete the result file: " + xmlFile, e);
        }

        return odsFile;
    }

}
