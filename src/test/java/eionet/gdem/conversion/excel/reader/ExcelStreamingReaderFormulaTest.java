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

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Test formula evaluations in Excel spreadsheets.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class ExcelStreamingReaderFormulaTest {

    @Test
    public void testGetFormulaValueXls2007() throws Exception {
        File inFile = new File(this.getClass().getClassLoader().getResource(TestConstants.SEED_FORMULAS_XLSX)
                .getFile());

        ExcelStreamingReader reader = new ExcelStreamingReader();
        reader.initReader(inFile);

        Workbook workbook = reader.getWorkbook();

        for (Row row : workbook.getSheetAt(0)) {
            if (row.getRowNum() == 0) {
                continue;
            }
            if (row.getRowNum() == 1) {
                System.out.println("VALUE: " + row.getCell(3).getStringCellValue());
                String value = reader.cellValueToString(row.getCell(3), "xs:integer");
                assertEquals("2011", value);

                String value2 = reader.cellValueToString(row.getCell(1), "xs:string");
                assertEquals("EE11", value2);

                String value4 = reader.cellValueToString(row.getCell(4), "xs:decimal");
                assertEquals("2010.123", value4);
            } else if (row.getRowNum() == 3) {
                String value3 = reader.cellValueToString(row.getCell(3), "xs:integer");
                assertEquals("4011", value3);
            }

        }
    }
}

