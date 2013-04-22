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

import java.io.File;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import eionet.gdem.test.TestConstants;

/**
 *
 * Test formula evaluations in Excel spreadsheets.
 *
 * @author Enriko Käsper
 */
public class ExcelReaderFormulaTest extends TestCase{

    public void testGetFormulaValueXls() throws Exception{
        File inFile = new File(this.getClass().getClassLoader().getResource(TestConstants.SEED_FORMULAS_XLS)
                .getFile());

        ExcelReader excel = new ExcelReader(false);
        excel.initReader(inFile);

        Workbook workbook = excel.getWorkbook();

        //test integer formula
        Cell cell = workbook.getSheetAt(0).getRow(1).getCell(3);
        String value = excel.cellValueToString(cell, "xs:integer");
        assertEquals("2011", value);

        //test string formula
        Cell cell2 = workbook.getSheetAt(0).getRow(1).getCell(1);
        String value2 = excel.cellValueToString(cell2, "xs:string");
        assertEquals("EE11", value2);

        //test sum formula
        Cell cell3 = workbook.getSheetAt(0).getRow(3).getCell(3);
        String value3 = excel.cellValueToString(cell3, "xs:integer");
        assertEquals("4011", value3);

        //test decimal formula
        Cell cell4 = workbook.getSheetAt(0).getRow(1).getCell(4);
        String value4 = excel.cellValueToString(cell4, "xs:decimal");
        assertEquals("2010.123", value4);
    }

    public void testGetFormulaValueXls2007() throws Exception{
        File inFile = new File(this.getClass().getClassLoader().getResource(TestConstants.SEED_FORMULAS_XLSX)
                .getFile());

        ExcelReader excel = new ExcelReader(true);
        excel.initReader(inFile);

        Workbook workbook = excel.getWorkbook();

        //test integer formula
        Cell cell = workbook.getSheetAt(0).getRow(1).getCell(3);
        String value = excel.cellValueToString(cell, "xs:integer");
        assertEquals("2011", value);

        //test string formula
        Cell cell2 = workbook.getSheetAt(0).getRow(1).getCell(1);
        String value2 = excel.cellValueToString(cell2, "xs:string");
        assertEquals("EE11", value2);

        //test sum formula
        Cell cell3 = workbook.getSheetAt(0).getRow(3).getCell(3);
        String value3 = excel.cellValueToString(cell3, "xs:integer");
        assertEquals("4011", value3);

        //test decimal formula
        Cell cell4 = workbook.getSheetAt(0).getRow(1).getCell(4);
        String value4 = excel.cellValueToString(cell4, "xs:decimal");
        assertEquals("2010.123", value4);
    }
}
