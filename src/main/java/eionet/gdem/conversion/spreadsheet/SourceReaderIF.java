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

package eionet.gdem.conversion.spreadsheet;

import java.io.File;
import java.util.Map;

import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.datadict.DD_XMLInstance;
import eionet.gdem.dto.ConversionResultDto;

/**
 * Generic source file reader interface.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */
public interface SourceReaderIF {

    /**
     * If the source file is generated from Data Dictionary, then it should contain XML Shema in metada or somewhere in content
     *
     * @return - XML Schema URL
     */
    String getXMLSchema();

    /**
     * Initialize the Source file from InputStream
     *
     * @param input
     *            input Excel or OpenDocument File
     * @throws XMLConvException If an error occurs.
     */
    void initReader(File input) throws XMLConvException;

    /**
     * Goes through the source file and writes the data into DD_XMLInstance as xml
     *
     * @param instance
     *            XML instance file, where the structure xml has been efined before
     * @throws Exception If an error occurs.
     */
    void writeContentToInstance(DD_XMLInstance instance) throws Exception;

    /**
     * Finds the first sheet name, that is not DO_NOT_DELETE_THIS_SHEET
     *
     * @return - sheet name
     */
    String getFirstSheetName();

    /**
     * If the spurce file is generated from Data Dictionary, then it finds the XML Shemas for each spreadsheet
     *
     * @return - Spreadsheet name
     */
    Map<String, String> getSheetSchemas();

    /**
     * Check if sheet has data or not
     *
     * @param sheet_name
     *            - sheet name
     * @return boolean - true if has data
     */
    boolean isEmptySheet(String sheet_name);

    /**
     * Source reader ended it's job. Do some closing stuff.
     */
    void closeReader();

    /**
     * Source reader ended it's job. Do some closing stuff.
     * @param resultObject Object for storing conversion log messages.
     */
    void startReader(ConversionResultDto resultObject);
}
