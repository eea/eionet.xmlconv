package eionet.gdem.conversion.spreadsheet;

import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.excel.ExcelUtils;

import java.io.File;

/**
 * This class returns MS Excel 2007+ specific handlers for DDXMLConverter
 *
 * @author Thanos Tourikas
 */
public class Excel2XMLStreamingConverter extends DDXMLConverter {

    private static final String FORMAT_NAME = "MS Excel 2007";

    /**
     *
     * Initializes converter
     *
     * @param inFile Input file
     * @throws XMLConvException If an error occurs.
     */
    @Override
    public void initConverter(File inFile) throws XMLConvException {
        sourcefile = getSourceReader();
        sourcefile.initReader(inFile);
        setInitialized(true);
    }

    @Override
    public SourceReaderIF getSourceReader() {
        return ExcelUtils.getExcelStreamingReader();
    }

    @Override
    public String getSourceFormatName() {
        return FORMAT_NAME;
    }

}