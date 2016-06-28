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

import eionet.gdem.conversion.excel.ExcelUtils;

/**
 * This class is returns MS Excel specific handlers for DDXMLConverter.
 *
 * @author Enriko Käsper
 */

public class Excel2XML extends DDXMLConverter {
    /**
     * Class constructor.
     */
    Excel2XML(){
        super();
    }

    private static final String FORMAT_NAME = "MS Excel";

    @Override
    public SourceReaderIF getSourceReader() {
        return ExcelUtils.getExcelReader();
    }

    @Override
    public String getSourceFormatName() {
        return FORMAT_NAME;
    }
}
