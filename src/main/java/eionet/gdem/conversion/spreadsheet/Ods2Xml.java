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
 * Created on 28.04.2006
 */

package eionet.gdem.conversion.spreadsheet;

import eionet.gdem.conversion.odf.OpenDocumentUtils;

/**
 * This class is creating handlers for creating XML file from OpenDocument Spreadsheet format called from ConversionService.
 *
 * @author Enriko Käsper
 */

public class Ods2Xml extends DDXMLConverter {

    /**
     * Class constructor.
     */
    Ods2Xml(){
        super();
    }

    private static final String FORMAT_NAME = "OpenDocument Spreadsheet";

    @Override
    public SourceReaderIF getSourceReader() {
        return OpenDocumentUtils.getSpreadhseetReader();
    }

    @Override
    public String getSourceFormatName() {
        return FORMAT_NAME;
    }
}
