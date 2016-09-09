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
 *    Original code: Nedeljko Pavlovic (ED)
 *    							 Alfeldi Istvan (ED)
 */

package eionet.gdem.conversion.converters;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;



import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.excel.ExcelProcessor;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts XML files to Excel.
 * @author Unknown
 * @author George Sofianos
 */
public class ExcelConverter extends ConvertStrategy {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelConverter.class);

    @Override
    public String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws XMLConvException,
    Exception {
        String xmlFile = Utils.getUniqueTmpFileName(".xml");
        String excelFile = Utils.getUniqueTmpFileName(".xls");
        OutputStream xmlOut = null;
        try {
            xmlOut = new FileOutputStream(xmlFile);
            runXslTransformation(source, xslt, xmlOut);
            ExcelProcessor ep = new ExcelProcessor();
            if (result != null) {
                ep.makeExcel(xmlFile, result);
            } else {
                ep.makeExcel(xmlFile, excelFile);
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("Error " + e.toString(), e);
            throw new XMLConvException("Error transforming Excel " + e.toString(), e);
        }
        finally{
            IOUtils.closeQuietly(xmlOut);
        }
        try {
            Utils.deleteFile(xmlFile);
        } catch (Exception e) {
            LOGGER.error("Couldn't delete the result file: " + xmlFile, e);
        }
        return excelFile;
    }

}
