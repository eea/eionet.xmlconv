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
import junit.framework.TestCase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 *
 * Test Excel reader methods.
 *
 * @author Enriko Käsper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContext.class})
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
}
