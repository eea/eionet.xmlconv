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
package eionet.gdem.conversion.odf;

import java.io.File;
import java.util.List;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;

/**
 *
 * Test formula calculations in ODS files.
 *
 * @author Enriko Käsper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class OdsReaderTest {

    @Test
    public void testGetFormulaValueXls2007() throws Exception{
        File inFile = new File(this.getClass().getClassLoader().getResource(TestConstants.SEED_FORMULAS_ODS)
                .getFile());

        OdsReader odsReader = new OdsReader();
        odsReader.initReader(inFile);
        OpenDocumentSpreadsheet spreadsheet = odsReader.getSpreadsheet();
        List<List<String>> tableData = spreadsheet.getTableData(spreadsheet.getTableName(0));

        //test integer formula
        String value = tableData.get(0).get(3);
        assertEquals("2011", value);

        //test string formula
        String value2 = tableData.get(0).get(1);
        assertEquals("EE11", value2);

        //test sum formula
        String value3 = tableData.get(2).get(3);
        assertEquals("4011", value3);

        //test decimal formula
        String value4 = tableData.get(0).get(4);
        assertEquals("2010.123", value4);
    }
}
