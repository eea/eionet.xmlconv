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
 * The Original Code is Converters and QA scripts.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *     Enriko Käsper, TripleDev
 */

package eionet.gdem.services.db.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.EqualPredicate;
import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class StylesheetDaoTest {

    @Autowired
    private DataSource db;

    @Autowired
    private IStyleSheetDao stylesheetDao;

    @Autowired
    private ISchemaDao schemaDao;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_CONVERSIONS_XML);
    }

    @Test
    public void getAllStylesheets() {
        List<Stylesheet> stylesheets = stylesheetDao.getStylesheets();

        Stylesheet stylesheet =
                (Stylesheet) CollectionUtils.find(stylesheets, new BeanPredicate("convId", new EqualPredicate("180")));

        assertTrue(stylesheets.size() > 10);
        assertEquals("stylesheet", stylesheet.getDescription());
        assertEquals("HTML", stylesheet.getType());
        assertEquals("file.xsl", stylesheet.getXslFileName());

    }

    @Test
    public void getStylesheetWithoutSchemas() {
        Stylesheet stylesheet = stylesheetDao.getStylesheet("182");
        assertEquals("stylesheet without schemas", stylesheet.getDescription());
        assertEquals("HTML", stylesheet.getType());
        assertEquals("file182.xsl", stylesheet.getXslFileName());
        assertEquals(stylesheet.getSchemas().size(), 0);
    }

    @Test
    public void getStylesheetWithSchemas() {
        Stylesheet stylesheet = stylesheetDao.getStylesheet("181");
        assertEquals("stylesheet", stylesheet.getDescription());
        assertEquals("HTML", stylesheet.getType());
        assertEquals("file.xsl2", stylesheet.getXslFileName());
        assertEquals(stylesheet.getSchemas().size(), 2);
    }

    @Test
    public void countStylesheets() throws SQLException {
        assertTrue(stylesheetDao.checkStylesheetFile("file182.xsl"));
        assertFalse(stylesheetDao.checkStylesheetFile("unknown.xsl"));
    }

    @Test
    public void countStylesheetFiles() throws SQLException {
        assertTrue(stylesheetDao.checkStylesheetFile("182", "file182.xsl"));
        assertFalse(stylesheetDao.checkStylesheetFile("999", "file182.xsl"));
        assertFalse(stylesheetDao.checkStylesheetFile("182", "unknown.xsl"));
    }

    @Test
    public void addStylesheetWithoutSchemas() throws SQLException {
        Stylesheet initialStylesheet = createStylesheetObject();
        String id = stylesheetDao.addStylesheet(initialStylesheet);
        Stylesheet stylesheet = stylesheetDao.getStylesheet(id);
        assertStylesheet(initialStylesheet, stylesheet);
        assertEquals(stylesheet.getSchemas().size(), 0);
    }

    @Test
    public void addStylesheetWithDependsOnAndSchemas() throws SQLException {
        Stylesheet initialStylesheet = createStylesheetWithSchemas();
        initialStylesheet.setDependsOn("11");
        String id = stylesheetDao.addStylesheet(initialStylesheet);
        Stylesheet stylesheet = stylesheetDao.getStylesheet(id);
        assertStylesheet(initialStylesheet, stylesheet);
        assertStylesheetSchemas(initialStylesheet, stylesheet);
    }

    @Test
    public void deleteStylesheet() throws SQLException {
        Stylesheet initialStylesheet = createStylesheetObject();
        String[] schemaUrls = { "uniqueSchema1", "uniqueSchema2" };
        initialStylesheet.setSchemaUrls(Arrays.asList(schemaUrls));

        String id = stylesheetDao.addStylesheet(initialStylesheet);
        Stylesheet stylesheet = stylesheetDao.getStylesheet(id);
        assertStylesheet(initialStylesheet, stylesheet);
        assertStylesheetSchemas(initialStylesheet, stylesheet);

        List<Schema> schemas = stylesheet.getSchemas();

        stylesheetDao.deleteStylesheet(id);
        Stylesheet noStylesheet = stylesheetDao.getStylesheet(id);
        assertNull(noStylesheet);

        // check if relations are deleted
        for (Schema schema : schemas) {
            Vector<?> schemaStylesheets = schemaDao.getSchemaStylesheets(schema.getId());
            assertEquals(schemaStylesheets.size(), 0);
        }
    }

    @Test
    public void updateStylesheet() throws SQLException{
        Stylesheet initialStylesheet = createStylesheetObject();
        String id = stylesheetDao.addStylesheet(initialStylesheet);

        Stylesheet addedStylesheet = stylesheetDao.getStylesheet(id);
        String[] schemaUrls = {"uniqueSchema3", "uniqueSchema4"};
        addedStylesheet.setSchemaUrls(Arrays.asList(schemaUrls));
        addedStylesheet.setDescription("newDescription");
        addedStylesheet.setDependsOn("9999");
        addedStylesheet.setType("EXCEL");

        stylesheetDao.updateStylesheet(addedStylesheet);
        Stylesheet updatedStylesheet = stylesheetDao.getStylesheet(id);
        assertStylesheet(addedStylesheet, updatedStylesheet);
        assertStylesheetSchemas(addedStylesheet, updatedStylesheet);

        //remove uniqueSchema3 and add uniqueSchema5
        String[] addedSchemaUrls = {"uniqueSchema4", "uniqueSchema5"};
        updatedStylesheet.setSchemaUrls(Arrays.asList(addedSchemaUrls));
        String deletedSchemaId = null;
        for (Schema schema : updatedStylesheet.getSchemas()){
            if (schema.getSchema().equals("uniqueSchema4")){
                String[] schemaIds = {schema.getId()};
                updatedStylesheet.setSchemaIds(Arrays.asList(schemaIds));
            }else if (schema.getSchema().equals("uniqueSchema3")){
                deletedSchemaId = schema.getId();
            }
        }
        stylesheetDao.updateStylesheet(updatedStylesheet);
        Stylesheet reUpdatedStylesheet = stylesheetDao.getStylesheet(id);
        assertStylesheet(addedStylesheet, reUpdatedStylesheet);

        // check updated schemas
        assertEquals(reUpdatedStylesheet.getSchemas().size(), 2);
        List<String> remaingSchemaUrls = Arrays.asList(new String[]{"uniqueSchema4", "uniqueSchema5"});
        for (Schema schema: reUpdatedStylesheet.getSchemas()){
            assertTrue(remaingSchemaUrls.contains(schema.getSchema()));
        }

        // check that deleted schema does not have relations anymore
        Vector<?> schemaStylesheets = schemaDao.getSchemaStylesheets(deletedSchemaId);
        assertEquals(schemaStylesheets.size(), 0);

    }

    private void assertStylesheet(Stylesheet initialStylesheet, Stylesheet stylesheet) {

        assertEquals(initialStylesheet.getType(), stylesheet.getType());
        assertEquals(initialStylesheet.getDescription(), stylesheet.getDescription());
        assertEquals(initialStylesheet.getXslFileName(), stylesheet.getXslFileName());
        assertEquals(initialStylesheet.getDependsOn(), stylesheet.getDependsOn());
    }

    private void assertStylesheetSchemas(Stylesheet initialStylesheet, Stylesheet stylesheet) {
        assertEquals(initialStylesheet.getSchemaUrls().size(), stylesheet.getSchemas().size());
        for (Schema schema : stylesheet.getSchemas()) {
            assertTrue(initialStylesheet.getSchemaUrls().contains(schema.getSchema()));
        }
    }

    private Stylesheet createStylesheetObject() {
        Stylesheet stylesheet = new Stylesheet();
        stylesheet.setDescription("Description");
        stylesheet.setType("HTML");
        stylesheet.setXslFileName(System.currentTimeMillis() + "-file.xsl");

        return stylesheet;
    }

    private Stylesheet createStylesheetWithSchemas() {
        Stylesheet stylesheet = createStylesheetObject();
        String[] schemas = { "schema1", "schema2", "schema3" };
        stylesheet.setSchemaUrls(Arrays.asList(schemas));
        return stylesheet;
    }

}
