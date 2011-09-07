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

package eionet.gdem.dcm.business;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.dto.ConvType;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, Tieto Estonia ConvTypeManagerTest
 */

public class ConvTypeManagerTest extends DBTestCase {

    /**
     * Provide a connection to the database.
     */
    public ConvTypeManagerTest(String name) {
        super(name);
        DbHelper.setUpConnectionProperties();
    }

    /**
     * Set up test case properties
     */
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.setUpProperties(this);
    }

    /**
     * Load the data which will be inserted for the test
     */
    protected IDataSet getDataSet() throws Exception {
        IDataSet loadedDataSet =
                new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_QA_XML));
        return loadedDataSet;
    }

    /**
     * The method queries all the conversion types
     * 
     * @throws Exception
     */
    public void testGgetConvType() throws Exception {
        ConvTypeManager ctm = new ConvTypeManager();
        ConvType cType = ctm.getConvType("HTML");
        assertEquals("HTML", cType.getConvType());
        assertEquals("text/html;charset=UTF-8", cType.getContType());
        assertEquals("html", cType.getFileExt());
        assertEquals("HTML files", cType.getDescription());
    }

}
