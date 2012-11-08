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
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */

package eionet.gdem.conversion.spreadsheet;

import java.math.BigDecimal;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ConversionLogDto;
import eionet.gdem.dto.ConversionLogDto.ConversionLogType;
import eionet.gdem.dto.ConversionResultDto;

/**
 * Logger writes log messages into ConversionResultDto.
 *
 * @author Enriko Käsper
 */
public class SourceReaderLogger {

    /** */
    private static final Log LOGGER = LogFactory.getLog(SourceReaderLogger.class);

    private long startTimestamp = 0;

    /**
     * Enum storing reader type messages.
     */
    public enum ReaderTypeEnum {
        EXCEL("MS Excel"), ODS("OpenOffice Spreadsheet");
        private String message;

        private ReaderTypeEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Conversion result object where to write log messages.
     */
    private ConversionResultDto conversionResult;
    /**
     * Reader type indicating whether it is Excel or OpenDocument conversion
     */
    private ReaderTypeEnum readerType;

    public SourceReaderLogger(ConversionResultDto conversionResult, ReaderTypeEnum readerType) {
        this.conversionResult = conversionResult;
        this.readerType = readerType;
    }

    /**
     * Start reading spreadsheet.
     */
    public void logStartWorkbook() {
        startTimestamp = System.currentTimeMillis();
        conversionResult.addConversionLog(ConversionLogType.INFO,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_START_SPREADSHEET), ConversionLogDto.CATEGORY_WORKBOOK);
    }

    /**
     * Could not find any table definitions from Data Dictionary for given MS Excel file
     */
    public void logNoDefinitionsForTables() {
        conversionResult.addConversionLog(ConversionLogType.ERROR,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_NO_DEFINITIONS, new String[] {readerType.getMessage()}),
                ConversionLogDto.CATEGORY_WORKBOOK);
    }

    /**
     * Found " + numberOfSheets + " sheets from the workbook
     *
     * @param numberOfSheets
     * @param sheetNames
     */
    public void logNumberOfSheets(int numberOfSheets, String sheetNames) {
        String plural = (numberOfSheets != 1) ? "s" : "";
        conversionResult.addConversionLog(
                ConversionLogType.INFO,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_NOF_SHEETS, new String[] {Integer.toString(numberOfSheets),
                        sheetNames, plural}), ConversionLogDto.CATEGORY_WORKBOOK);
    }

    /**
     * Start reading sheet:
     *
     * @param sheetName
     */
    public void logStartSheet(String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.INFO,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_START_SHEET, new String[] {sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * Unable to find sheet
     *
     * @param sheetName
     */
    public void logSheetNotFound(String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.WARNING,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_NO_SHEET, new String[] {sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * Sheet is missing or empty. Skip converting this sheet:
     *
     * @param sheetName
     */
    public void logEmptySheet(String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.INFO,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_EMPTY_SHEET, new String[] {sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * "Found nofColumns columns on sheet: sheetName
     *
     * @param nofColumns
     * @param sheetName
     */
    public void logNumberOfColumns(int nofColumns, String sheetName) {
        String plural = (nofColumns != 1) ? "s" : "";
        conversionResult.addConversionLog(
                ConversionLogType.INFO,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_NOF_COLS, new String[] {Integer.toString(nofColumns),
                        sheetName, plural}), ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * Found redundant columns: extraColumns on sheet sheetName
     *
     * @param extraColumns
     * @param sheetName
     */
    public void logExtraColumns(String extraColumns, String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.WARNING,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_REDUNDANT_COLS, new String[] {extraColumns, sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * The following columns are missing: missingColumns on sheet sheetName
     *
     * @param missingColumns
     * @param sheetName
     */
    public void logMissingColumns(String missingColumns, String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.WARNING,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_MISSING_COLS, new String[] {missingColumns, sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * End reading sheet:
     *
     * @param sheetName
     */
    public void logEndSheet(String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.INFO,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_END_SHEET, new String[] {sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * Found numberOfRows records on sheet: sheetName
     *
     * @param numberOfRows
     * @param sheetName
     */
    public void logNumberOfRows(int numberOfRows, String sheetName) {
        String plural = (numberOfRows != 1) ? "s" : "";
        conversionResult.addConversionLog(
                ConversionLogType.INFO,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_NOF_RECORDS, new String[] {Integer.toString(numberOfRows),
                        sheetName, plural}), ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * End reading spreadsheet.
     */
    public void logEndWorkbook(long fileSize) {
        BigDecimal totalTime = new BigDecimal(System.currentTimeMillis() - startTimestamp).divide(new BigDecimal("1000.0"));
        String fileSizeMessage = "";
        if(fileSize >0){
            fileSizeMessage = FileUtils.byteCountToDisplaySize(fileSize);
        }
        String message =
            Properties.getMessage(BusinessConstants.CONVERSION_LOG_END_SPREADSHEET, new String[] {fileSizeMessage, totalTime.toPlainString()});
        conversionResult.addConversionLog(ConversionLogType.INFO, message, ConversionLogDto.CATEGORY_WORKBOOK);
        LOGGER.info(message);
    }

    /**
     * Sheet schema is: schemaUrl
     *
     * @param instanceUrl
     * @param tblLocalName
     */
    public void logSheetSchema(String instanceUrl, String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.INFO,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_SHEET_SCHEMA, new String[] {instanceUrl}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }
    /**
     * Log system warning:
     *
     * @param sheetName
     */
    public void logSystemWarning(String sheetName, String warnMessage) {
        conversionResult.addConversionLog(ConversionLogType.WARNING,
                Properties.getMessage(BusinessConstants.CONVERSION_LOG_WARNING, new String[] {sheetName, warnMessage}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * Log system info:
     *
     * @param sheetName
     */
    public void logInfo(String sheetName, String infoMessage) {
        conversionResult.addConversionLog(ConversionLogType.INFO,
                infoMessage,
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }
}
