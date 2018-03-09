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
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.conversion.datadict;

import java.util.Map;

import eionet.gdem.test.ApplicationTestContext;
import junit.framework.TestCase;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 *
 * @author Enriko Käsper, Tieto Estonia DataDictUtilTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class DataDictUtilTest {

    @Before
    public void setUp() {
        TestUtils.setUpProperties(this);
    }
    /**
     * Tests convert method - validate the result file and metadata( content type and file name)
     */
    @Test
    public void testGetElementsDefs() throws Exception {
        String schemaUrl = TestUtils.getSeedURL(TestConstants.SEED_GW_CONTAINER_SCHEMA, this);
        Map<String, DDElement> elemDefs = DataDictUtil.importDDElementSchemaDefs(null, schemaUrl);
        assertEquals(43, elemDefs.size());

        String type = elemDefs.get("GWEWN-Code").getSchemaDataType();
        assertEquals("xs:string", type);

        String type2 = elemDefs.get("GWArea").getSchemaDataType();
        assertEquals("xs:decimal", type2);
    }

    /**
     * Tests DD schema URL handling
     */
    @Test
    public void testDDUrlhandling() throws Exception {
        assertEquals("http://dd.eionet.europa.eu/GetXmlInstance?id=3739&type=tbl",
                DataDictUtil.getInstanceUrl("http://dd.eionet.europa.eu/GetSchema?id=TBL3739"));
        assertEquals("TBL3739", DataDictUtil.getSchemaIdParamFromUrl(("http://dd.eionet.europa.eu/GetSchema?id=TBL3739")));
        assertEquals("DST1111", DataDictUtil.getSchemaIdParamFromUrl(("http://dd.eionet.europa.eu/GetSchema?id=DST1111")));
        assertEquals("http://dd.eionet.europa.eu/GetContainerSchema?id=DST1111",
                DataDictUtil.getContainerSchemaUrl(("http://dd.eionet.europa.eu/GetSchema?id=DST1111")));
    }

    /**
     * Tests convert method - validate the result file and metadata( content type and file name)
     */
    @Test
    public void testGetContainerSchemaUrl() throws Exception {
        String url = DataDictUtil.getContainerSchemaUrl("http://dd.eionet.europa.eu/GetSchema?id=TBL4948");
        assertEquals("http://dd.eionet.europa.eu/GetContainerSchema?id=TBL4948", url);
    }

    @Test
//    @Ignore
    // TODO FIX
    public void testMultivalueElementsDefs() throws Exception {
        String schemaUrl = TestUtils.getSeedURL(TestConstants.SEED_GW_SCHEMA, this);

        Map<String, DDElement> elemDefs = DataDictUtil.importDDTableSchemaElemDefs(schemaUrl);

        DDElement stratElement = elemDefs.get("Stratigraphy");
        assertTrue(stratElement.isHasMultipleValues());
        assertEquals(";", stratElement.getDelimiter());

    }

    @Test
    public void testNewSchemaDataset() {
        String schemaUrl = "http://dd.eionet.europa.eu/v2/dataset/3381/schema-dst-3381.xsd";
        Map<String, String> map = DataDictUtil.getDatasetReleaseInfoForSchema(schemaUrl);
        assertNotNull(map);
    }

    @Test
    public void testNewSchemaTable() {
        String schemaUrl = "http://dd.eionet.europa.eu/v2/dataset/3381/schema-tbl-11181.xsd";
        Map<String, String> map = DataDictUtil.getDatasetReleaseInfoForSchema(schemaUrl);
        assertNotNull(map);
    }
}
