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
 * The Original Code is XMLCONV - Converters and QA Services
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Zero Technologies or TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s): Enriko Käsper, TripleDev
 */
package eionet.gdem.conversion.excel.reader;

import eionet.gdem.XMLConvException;
import eionet.gdem.test.TestConstants;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import java.io.File;
import static org.junit.Assert.assertEquals;

/**
 *
 * Test Excel reader methods.
 *
 * @author Enriko Käsper
 */
public class ExcelReaderTest {

    @Test
    public void testTrimIntegerValues() throws Exception {
        File inFile = new File(this.getClass().getClassLoader().getResource(TestConstants.SEED_RIVERS_XLS)
                .getFile());

        ExcelReader excel = new ExcelReader(false);
        excel.initReader(inFile);

        Workbook workbook = excel.getWorkbook();

        //test value in Mean column
        Cell cell = workbook.getSheetAt(0).getRow(1).getCell(13);
        String value = excel.cellValueToString(cell, "xs:integer");
        assertEquals("54.675000", value);
    }
    @Test
    public void testTrimStringValues() throws Exception {
        File inFile = new File(this.getClass().getClassLoader().getResource(TestConstants.SEED_RIVERS_XLS)
                .getFile());

        ExcelReader excel = new ExcelReader(false);
        excel.initReader(inFile);

        Workbook workbook = excel.getWorkbook();

        //test string value trimming. PeriodLength column
        Cell cell = workbook.getSheetAt(0).getRow(1).getCell(4);
        String value = excel.cellValueToString(cell, "xs:string");
        assertEquals("Trim this string", value);
    }

    @Test
    public void testThousantSeparator() throws XMLConvException {
        File inFile = new File(getClass().getClassLoader().getResource(TestConstants.SEED_READER_XLS)
                .getFile());

        ExcelReader excel = new ExcelReader(false);
        excel.initReader(inFile);

        Workbook workbook = excel.getWorkbook();

        //test thousant separator
        Cell cell = workbook.getSheetAt(0).getRow(1).getCell(1);
        String value = excel.cellValueToString(cell, "xs:string");
        assertEquals("123123.21", value);
    }

    @Test
    public void testIntegerParsing() throws XMLConvException {
        File inFile = new File(getClass().getClassLoader().getResource(TestConstants.SEED_READER_XLS)
                .getFile());

        ExcelReader excel = new ExcelReader(false);
        excel.initReader(inFile);

        Workbook workbook = excel.getWorkbook();

        //test thousant separator
        Cell cell = workbook.getSheetAt(0).getRow(2).getCell(1);
        String value = excel.cellValueToString(cell, "xs:string");
        assertEquals("1", value);
    }

    @Test
    public void testDecimalValue() throws XMLConvException {
        File inFile = new File(getClass().getClassLoader().getResource(TestConstants.SEED_READER_XLS)
                .getFile());

        ExcelReader excel = new ExcelReader(false);
        excel.initReader(inFile);

        Workbook workbook = excel.getWorkbook();

        //test thousant separator
        Cell cell = workbook.getSheetAt(0).getRow(3).getCell(1);
        String value = excel.cellValueToString(cell, "xs:string");
        assertEquals("0.00001", value);
    }

    @Test
    public void testGreekLocale() throws XMLConvException {
        File inFile = new File(getClass().getClassLoader().getResource(TestConstants.SEED_READER_XLS)
                .getFile());

        ExcelReader excel = new ExcelReader(false);
        excel.initReader(inFile);

        Workbook workbook = excel.getWorkbook();

        //test thousant separator
        Cell cell = workbook.getSheetAt(0).getRow(4).getCell(1);
        String value = excel.cellValueToString(cell, "xs:string");
        assertEquals("123123.123", value);
    }


}
