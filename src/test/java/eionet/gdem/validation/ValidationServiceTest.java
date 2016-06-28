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
 *        Enriko Käsper (TripleDev)
 */
package eionet.gdem.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.Properties;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

/**
 * Tests ValidationService methods
 *
 * @author Enriko Käsper, TietoEnator Estonia AS ValidationServiceTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ValidationServiceTest {

    @Autowired
    private DataSource db;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);
    }

    /**
     * Test XML file validation method. Requires seed-gw-invalid.xml file. The file is not valid.
     *
     * @throws Exception
     */
    @Test
    public void testValidateInvalidXML() throws Exception {
        ValidationService validService = new ValidationService();
        String s =
                validService.validateSchema(TestUtils.getSeedURL(TestConstants.SEED_GW_INVALID_XML, this), TestUtils.getSeedURL(TestConstants.SEED_GW_SCHEMA, this));

        // System.out.println(s);

        assertTrue(s.indexOf("<span id=\"feedbackStatus\" class=\"ERROR\"") > 0);
        // error objects are stored in errors list
        assertTrue(validService.getErrorList().size() > 0);
    }

    /**
     * Test XML file validation method. Requires seed-gw-valid.xml file. The file is valid.
     *
     * @throws Exception
     */
    @Test
    public void testValidateValidXML() throws Exception {
        ValidationService validService = new ValidationService();
        String s =
                validService.validateSchema(TestUtils.getSeedURL(TestConstants.SEED_GW_VALID_XML, this), TestUtils.getSeedURL(TestConstants.SEED_GW_SCHEMA, this));

        // System.out.println(s);

        assertTrue(s.startsWith("<div"));
        assertTrue(s.indexOf("<p>OK</p>") > 0);
        assertTrue(s.indexOf("<span id=\"feedbackStatus\" class=\"INFO\"") > 0);
        // check if errors list is empty
        assertTrue(validService.getErrorList() == null || validService.getErrorList().size() == 0);
    }

    /**
     * Test XML file validation method. Validation Service should use locally stored XML Schema described in T_UPL_SCHEMA table.
     * Requires seed-gw-valid.xml file. The file is valid.
     *
     * @throws Exception
     */
    @Test
    public void testValidateValidXMLAgainstLocalSchema() throws Exception {
        ValidationService validService = new ValidationService();
        String s = validService.validate(TestUtils.getSeedURL(TestConstants.SEED_GW_VALID_XML, this));

        // System.out.println(s);

        assertTrue(s.startsWith("<div"));
        assertTrue(s.indexOf("<span id=\"feedbackStatus\" class=\"INFO\"") > 0);
        assertEquals(validService.getValidatedSchemaURL(), Properties.gdemURL.concat("/").concat(TestConstants.SEED_GW_SCHEMA));
        assertEquals(validService.getOriginalSchema(), "http://dd.eionet.europa.eu/GetSchema?id=TBL4564");
        // check if errors list is empty
        assertTrue(validService.getErrorList() == null || validService.getErrorList().size() == 0);
    }

    /**
     * The method tests, id xmlconv finds the schema stored in T_UPL_TABLE
     *
     * @throws Exception
     */
    @Test
    public void testSetLocalSchema() throws Exception {
        ValidationService validService = new ValidationService();
        validService.setLocalSchemaUrl("http://dd.eionet.europa.eu/GetSchema?id=TBL4564");

        assertTrue(validService.getValidatedSchema().endsWith(TestConstants.SEED_GW_SCHEMA));
        assertEquals(validService.getValidatedSchemaURL(), Properties.gdemURL.concat("/").concat(TestConstants.SEED_GW_SCHEMA));

        validService.setLocalSchemaUrl("http://dd.eionet.europa.eu/GetSchema");

        assertEquals(validService.getValidatedSchema(), "http://dd.eionet.europa.eu/GetSchema");
        assertEquals(validService.getValidatedSchemaURL(), "http://dd.eionet.europa.eu/GetSchema");
    }

    /**
     * Test XML file validation method. Validation Service should use locally stored XML Schema described in T_UPL_SCHEMA table.
     * Requires seed-gw-valid.xml file. The file is valid.
     *
     * @throws Exception
     */
    @Test
    public void testValidateValidXMLAgainstLocalDTD() throws Exception {
        ValidationService validService = new ValidationService();
        String s = validService.validate(TestUtils.getSeedURL(TestConstants.SEED_XLIFF_XML, this));

        // System.out.println(s);

        assertTrue(s.startsWith("<div"));
        assertTrue(s.indexOf("<span id=\"feedbackStatus\" class=\"INFO\"") > 0);
        assertEquals(validService.getValidatedSchemaURL(), Properties.gdemURL.concat("/").concat(TestConstants.SEED_XLIFF_DTD));
        assertEquals(validService.getOriginalSchema(), "http://www.oasis-open.org/committees/xliff/documents/xliff.dtd");
        // check if errors list is empty
        assertTrue(validService.getErrorList() == null || validService.getErrorList().size() == 0);
    }

    /**
     * Try to validate XML against XML schema that is not available
     *
     * @throws Exception
     */
    @Test
    public void testXMLAgainstUnavailableSchema() throws Exception {
        ValidationService validService = new ValidationService();
        String s =
                validService.validateSchema(TestUtils.getSeedURL(TestConstants.SEED_XLIFF_XML, this), "https://svn.eionet.europa.eu/thereisnoschema");

        // System.out.println(s);

        assertTrue(s.startsWith("<div"));
        assertTrue(s.indexOf("<span id=\"feedbackStatus\" class=\"ERROR\"") > 0);
    }

    /**
     * Validate XML against XML schema that should block submission in CDR.
     *
     * @throws Exception
     */
    @Test
    public void testXMLAgainstBlockerSchema() throws Exception {
        ValidationService validService = new ValidationService();
        String s =
                validService.validateSchema(TestUtils.getSeedURL(TestConstants.SEED_GW_INVALID_XML, this), "http://dd.eionet.europa.eu/GetSchema?id=TBL111");

        // System.out.println(s);

        assertTrue(s.startsWith("<div"));
        assertTrue(s.indexOf("<span id=\"feedbackStatus\" class=\"BLOCKER\"") > 0);
    }
}
