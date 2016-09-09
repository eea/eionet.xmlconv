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

package eionet.gdem.conversion.excel;

import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.xml.sax.XMLReader;

import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.excel.writer.ExcelConversionHandlerIF;
import eionet.gdem.conversion.excel.writer.ExcelXMLHandler;


/**
 * This class is creating handlers for creating Excel file from xml
 * called from ConversionService.
 * @author Enriko Käsper
 * @author George Sofianos
 */

public class ExcelProcessor  {
    /**
     * Default constructor.
     */
    public ExcelProcessor() {
    }

    /**
     * Converts XML string to Excel
     * @param sIn Input string
     * @param sOut Output string
     * @throws XMLConvException In case an error occurs.
     */
    public void makeExcel(String sIn, String sOut) throws XMLConvException {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(sOut);
            makeExcel(sIn, outStream);
        } catch (Exception e) {
            throw new XMLConvException("ErrorConversionHandler - couldn't save the Excel file: " + e.toString(), e);
        } finally {
            IOUtils.closeQuietly(outStream);
        }
    }

    /**
     * Converts XML string to OutputStream
     * @param sIn Input string
     * @param sOut OutputStream
     * @throws XMLConvException In case an error occurs.
     */
    public void makeExcel(String sIn, OutputStream sOut) throws XMLConvException {

        if (sIn == null) {
            return;
        }
        if (sOut == null) {
            return;
        }

        try {
            ExcelConversionHandlerIF excel = ExcelUtils.getExcelConversionHandler();
            //excel.setFileName(sOut);

            ExcelXMLHandler handler = new ExcelXMLHandler(excel);
            SAXParserFactory spfact = SAXParserFactory.newInstance();
            SAXParser parser = spfact.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            spfact.setValidating(true);

            reader.setContentHandler(handler);
            reader.parse(sIn);
            excel.writeToFile(sOut);
        } catch (Exception e) {
            throw new XMLConvException("Error generating Excel file: " + e.toString(), e);
        }

        return;
    }
}
