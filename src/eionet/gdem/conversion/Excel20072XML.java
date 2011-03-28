package eionet.gdem.conversion;

import eionet.gdem.conversion.excel.ExcelUtils;

    /**
     * This class is returns MS Excel specific handlers for DDXMLConverter
     *
     * @author Kaido Laine
     */

    public class Excel20072XML extends DDXMLConverter {
        private final static String FORMAT_NAME = "MS Excel 2007";

        public SourceReaderIF getSourceReader() {
            return ExcelUtils.getExcel2007Reader();
        }

        public String getSourceFormatName() {
            return FORMAT_NAME;
        }
    }


