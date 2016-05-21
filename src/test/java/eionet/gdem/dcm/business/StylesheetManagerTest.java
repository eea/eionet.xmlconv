package eionet.gdem.dcm.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class StylesheetManagerTest {

    @Autowired
    private DataSource db;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_CONVERSIONS_XML);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getStylesheetWithContent() throws DCMException {
        StylesheetManager stylesheetManager = new StylesheetManager();
        Stylesheet stylesheet = stylesheetManager.getStylesheet("168");

        assertEquals(stylesheet.getConvId(), "168");
        assertTrue(stylesheet.getXslFileFullPath().startsWith(Properties.xslFolder));
        assertTrue(stylesheet.getXslFileFullPath().endsWith(stylesheet.getXslFileName()));
        assertTrue(stylesheet.getChecksum().length() > 0);
        assertTrue(stylesheet.getModified().length() > 0);
        assertTrue(stylesheet.getXslContent().startsWith("<?xml version='1.0' encoding='UTF-8'?>"));

    }

    @Test
    public void getStylesheetWithoutContent() throws DCMException {
        StylesheetManager stylesheetManager = new StylesheetManager();
        Stylesheet stylesheet = stylesheetManager.getStylesheet("182");

        assertEquals(stylesheet.getConvId(), "182");
        assertTrue(stylesheet.getXslFileFullPath().startsWith(Properties.xslFolder));
        assertEquals(stylesheet.getXslFileName(), "file182.xsl");
        assertEquals(stylesheet.getChecksum(), "");
        assertNull(stylesheet.getModified());
        assertTrue(stylesheet.getXslContent().contains(Constants.FILEREAD_EXCEPTION));

    }

    @Test
    public void deleteStylesheetWithoutFile() throws DCMException {
        String stylesheetId = "182";
        StylesheetManager stylesheetManager = new StylesheetManager();
        Stylesheet stylesheet = stylesheetManager.getStylesheet(stylesheetId);
        assertEquals(stylesheet.getConvId(), stylesheetId);

        stylesheetManager.delete(TestConstants.TEST_ADMIN_USER, stylesheetId);

        assertNull(stylesheetManager.getStylesheet(stylesheetId));
    }

    @Test
    public void deleteStylesheetWithFile() throws DCMException {
        String stylesheetId = "183";
        StylesheetManager stylesheetManager = new StylesheetManager();
        Stylesheet stylesheet = stylesheetManager.getStylesheet(stylesheetId);
        assertEquals(stylesheet.getConvId(), stylesheetId);

        stylesheetManager.delete(TestConstants.TEST_ADMIN_USER, stylesheetId);

        assertNull(stylesheetManager.getStylesheet(stylesheetId));
    }

    @Test
    public void addStylesheetWithFile() throws DCMException, IOException {
        StylesheetManager stylesheetManager = new StylesheetManager();
        Stylesheet initialStylesheet = addContentToStylesheet(createStylesheetObject());
        stylesheetManager.add(initialStylesheet, TestConstants.TEST_ADMIN_USER);

        Stylesheet addedStylesheet = stylesheetManager.getStylesheet(initialStylesheet.getXslFileName());
        assertStylesheet(initialStylesheet, addedStylesheet);

        assertEquals(FileUtils.readFileToString(new File(addedStylesheet.getXslFileFullPath()), "utf-8"),
                initialStylesheet.getXslContent());
        assertEquals(addedStylesheet.getXslContent(), initialStylesheet.getXslContent());

    }
    @Test
    public void addStylesheetWithNoPermissions() throws DCMException, IOException {
        StylesheetManager stylesheetManager = new StylesheetManager();
        Stylesheet initialStylesheet = addContentToStylesheet(createStylesheetObject());
        thrown.expect(DCMException.class);
        stylesheetManager.add(initialStylesheet, TestConstants.TEST_USER);
    }
    @Test
    public void deleteStylesheetWithNoPermissions() throws DCMException, IOException {
        StylesheetManager stylesheetManager = new StylesheetManager();
        thrown.expect(DCMException.class);
        stylesheetManager.delete(TestConstants.TEST_USER, "168");
    }
    @Test
    public void updateStylesheetWithNoPermissions() throws DCMException, IOException {
        StylesheetManager stylesheetManager = new StylesheetManager();
        Stylesheet stylesheet = stylesheetManager.getStylesheet("168");
        thrown.expect(DCMException.class);
        stylesheetManager.update(stylesheet, TestConstants.TEST_USER, true);
    }

    @Test
    public void updateStylesheet() throws DCMException, IOException {

        // add stylesheet without content
        StylesheetManager stylesheetManager = new StylesheetManager();
        Stylesheet initialStylesheet = createStylesheetObject();
        stylesheetManager.add(initialStylesheet, TestConstants.TEST_ADMIN_USER);
        Stylesheet addedStylesheet = stylesheetManager.getStylesheet(initialStylesheet.getXslFileName());
        assertStylesheet(initialStylesheet, addedStylesheet);

        // add content and update
        addContentToStylesheet(addedStylesheet);
        stylesheetManager.update(addedStylesheet, TestConstants.TEST_ADMIN_USER, true);
        Stylesheet updatedStylesheet = stylesheetManager.getStylesheet(addedStylesheet.getConvId());
        assertStylesheet(addedStylesheet, updatedStylesheet);

        assertEquals(FileUtils.readFileToString(new File(updatedStylesheet.getXslFileFullPath()), "utf-8"),
                addedStylesheet.getXslContent());
        assertEquals(updatedStylesheet.getXslContent(), addedStylesheet.getXslContent());

        // add new content but to not update
        String newContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xsl:stylesheet>NEW XSL</xsl:stylesheet>";
        updatedStylesheet.setXslContent(newContent);
        stylesheetManager.update(addedStylesheet, TestConstants.TEST_ADMIN_USER, false);
        Stylesheet reUpdatedStylesheet = stylesheetManager.getStylesheet(updatedStylesheet.getConvId());
        assertStylesheet(reUpdatedStylesheet, updatedStylesheet);
        assertEquals(reUpdatedStylesheet.getXslContent(), addedStylesheet.getXslContent());

        // update content
        stylesheetManager.update(updatedStylesheet, TestConstants.TEST_ADMIN_USER, true);
        Stylesheet contentUpdatedStylesheet = stylesheetManager.getStylesheet(updatedStylesheet.getConvId());
        assertStylesheet(contentUpdatedStylesheet, updatedStylesheet);
        assertEquals(contentUpdatedStylesheet.getXslContent(), updatedStylesheet.getXslContent());
    }

    private Stylesheet createStylesheetObject() {
        Stylesheet stylesheet = new Stylesheet();
        stylesheet.setDescription("Description");
        stylesheet.setType("HTML");
        stylesheet.setDependsOn("0");

        stylesheet.setXslFileName(System.currentTimeMillis() + "-file.xsl");
        stylesheet.setXslContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xsl:stylesheet></xsl:stylesheet>");

        return stylesheet;
    }

    private Stylesheet addContentToStylesheet(Stylesheet stylesheet) {
        stylesheet.setXslFileName(System.currentTimeMillis() + "-file.xsl");
        stylesheet.setXslContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xsl:stylesheet></xsl:stylesheet>");

        return stylesheet;
    }

    private void assertStylesheet(Stylesheet initialStylesheet, Stylesheet stylesheet) {

        assertEquals(initialStylesheet.getType(), stylesheet.getType());
        assertEquals(initialStylesheet.getDescription(), stylesheet.getDescription());
        assertEquals(initialStylesheet.getXslFileName(), stylesheet.getXslFileName());
        assertEquals(initialStylesheet.getDependsOn(), stylesheet.getDependsOn());
    }
}
