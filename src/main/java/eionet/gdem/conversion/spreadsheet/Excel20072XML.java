package eionet.gdem.conversion.spreadsheet;

import eionet.gdem.conversion.excel.reader.ExcelReader;
import eionet.gdem.conversion.excel.reader.ExcelStreamingReader;

/**
 * This class is returns MS Excel specific handlers for DDXMLConverter.
 *
 * @author Kaido Laine
 */

public class Excel20072XML extends DDXMLConverter {
    /**
     * Class constructor.
     */
    Excel20072XML(){
        super();
    }

    private static final String FORMAT_NAME = "MS Excel 2007";

    @Override
    public SourceReaderIF getSourceReader() {
        return new ExcelReader(true);
    }

    @Override
    public String getSourceFormatName() {
        return FORMAT_NAME;
    }
}
