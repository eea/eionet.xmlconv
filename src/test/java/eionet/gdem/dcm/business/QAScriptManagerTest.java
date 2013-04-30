/*
 * Created on 01.12.20098
 */
package eionet.gdem.dcm.business;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.dto.QAScript;
import eionet.gdem.dto.Schema;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockFormFile;
import eionet.gdem.web.struts.qascript.QAScriptListHolder;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS SchemaManagerDBTest
 */

public class QAScriptManagerTest extends DBTestCase {

    /**
     * Provide a connection to the database.
     */
    public QAScriptManagerTest(String name) {
        super(name);
        DbHelper.setUpConnectionProperties();
    }

    /**
     * Set up test case properties
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.setUpProperties(this);
    }

    /**
     * Load the data which will be inserted for the test
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet loadedDataSet =
            new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream(TestConstants.SEED_DATASET_QA_XML));
        return loadedDataSet;
    }

    /**
     * The method adds QA Script into DB, then it edits the properties and finally deletes the added schema. After each operation it
     * scheks the properties values.
     *
     * @throws Exception
     */
    public void testAddQAScript() throws Exception {

        String queryFileName = TestConstants.SEED_QASCRIPT_XQUERY;
        String description = "test QA script";
        String schemaId = "83";
        String shortName = "New QA script";
        String resultType = "HTML";
        String scriptType = "xquery";
        String schema = "http://schema.xsd";
        String upperLimit = "100";
        String url = "http://url.srcippt.com";

        String user = TestConstants.TEST_ADMIN_USER;

        QAScriptManager qm = new QAScriptManager();

        MockFormFile scriptFile =
            new MockFormFile(getClass().getClassLoader().getResource(TestConstants.SEED_QASCRIPT_XQUERY).getFile());
        // add qa script into db and upoload schema file
        String scriptId = qm.add(user, shortName, schemaId, schema, resultType, description, scriptType, scriptFile,
                upperLimit, url);

        // query script by id and compare fields
        QAScript qascript = qm.getQAScript(scriptId);
        assertEquals(description, qascript.getDescription());
        assertEquals(schemaId, qascript.getSchemaId());
        assertEquals(queryFileName, qascript.getFileName());
        assertEquals(shortName, qascript.getShortName());
        assertEquals(resultType, qascript.getResultType());
        assertEquals(scriptType, qascript.getScriptType());
        assertEquals(upperLimit, qascript.getUpperLimit());
        assertEquals(url, qascript.getUrl());

    }

    /**
     * The method test if it is possible to update schema validation flag
     *
     * @throws Exception
     */
    public void testUpdateSchemaValidation() throws Exception {

        String user = TestConstants.TEST_ADMIN_USER;
        String schemaId = "62";

        SchemaManager sm = new SchemaManager();
        QAScriptListHolder st = sm.getSchemasWithQAScripts(schemaId);
        Schema schema = st.getQascripts().get(0);
        boolean validate = schema.isDoValidation();
        boolean blocker = schema.isBlocker();

        // update validation flag
        QAScriptManager qm = new QAScriptManager();
        qm.updateSchemaValidation(user, schemaId, !validate, !blocker);

        QAScriptListHolder st2 = sm.getSchemasWithQAScripts(schemaId);
        Schema schema2 = st2.getQascripts().get(0);

        assertEquals(!validate, schema2.isDoValidation());
        assertEquals(!blocker, schema2.isBlocker());
    }

    /**
     * The method deletes a qa script and checks if it succeeded
     *
     * @throws Exception
     */
    public void testDeleteQAScript() throws Exception {

        String user = TestConstants.TEST_ADMIN_USER;
        String schemaId = "62";

        SchemaManager sm = new SchemaManager();
        QAScriptListHolder st = sm.getSchemasWithQAScripts(schemaId);
        Schema schema = st.getQascripts().get(0);
        int countQAScripts = schema.getQascripts().size();

        // delete qa script
        QAScriptManager qm = new QAScriptManager();
        qm.delete(user, schema.getQascripts().get(0).getScriptId());

        QAScriptListHolder st2 = sm.getSchemasWithQAScripts(schemaId);
        Schema schema2 = st2.getQascripts().get(0);
        int countQAScripts2 = schema2.getQascripts().size();

        assertEquals(countQAScripts, countQAScripts2 + 1);
    }

    /**
     * The method checks if qa script file exists
     *
     * @throws Exception
     */
    public void testQAScriptFileExists() throws Exception {

        // delete qa script
        QAScriptManager qm = new QAScriptManager();

        // exists in DB and in filesystem
        boolean exists = qm.fileExists(TestConstants.SEED_QASCRIPT_TEST);
        assertTrue(exists);

        // exists in the DB
        boolean exists2 = qm.fileExists("sum-oz_info_1920_1.xql");
        assertTrue(exists2);

        // does not exist
        boolean exists3 = qm.fileExists("unknown.xql");
        assertFalse(exists3);
    }

    /**
     * The method updates QA Script properties and verifies the values in database afterwards.
     *
     * @throws Exception
     */
    public void testUpdateQAScript() throws Exception {

        String description = "QA script description";
        String scriptId = "49";
        String schemaId = "62";
        String shortName = "New short name";
        String resultType = "XML";
        String scriptType = "xsl";
        String fileName = TestConstants.SEED_QASCRIPT_TEST;
        String content = "The source of script file";
        String upperLimit = "1000";
        String url = "http://blahh.com";

        String user = TestConstants.TEST_ADMIN_USER;

        QAScriptManager qm = new QAScriptManager();

        // update qa script properties
        qm.update(user, scriptId, shortName, schemaId, resultType, description, scriptType, fileName, upperLimit, url, content, false);

        // query script by id and compare fields
        QAScript qascript = qm.getQAScript(scriptId);
        assertEquals(description, qascript.getDescription());
        assertEquals(schemaId, qascript.getSchemaId());
        assertEquals(fileName, qascript.getFileName());
        assertEquals(shortName, qascript.getShortName());
        assertEquals(resultType, qascript.getResultType());
        assertEquals(scriptType, qascript.getScriptType());
        assertEquals(upperLimit, qascript.getUpperLimit());
        assertEquals(url, qascript.getUrl());

    }

    /**
     * The method updates QA Script properties and verifies the values in database afterwards.
     *
     * @throws Exception
     */
    public void testUpdateQAScriptFile() throws Exception {

        String description = "QA script description";
        String scriptId = "50";
        String schemaId = "62";
        String shortName = "New short name";
        String resultType = "XML";
        String scriptType = "xsl";
        String fileName = TestConstants.SEED_QASCRIPT_XQUERY2;
        String upperLimit = "100";
        String url = "http://blahh.script.com";

        String user = TestConstants.TEST_ADMIN_USER;

        MockFormFile scriptFile =
            new MockFormFile(getClass().getClassLoader().getResource(TestConstants.SEED_QASCRIPT_XQUERY2).getFile());

        QAScriptManager qm = new QAScriptManager();

        // update qa script properties
        qm.update(user, scriptId, shortName, schemaId, resultType, description, scriptType, fileName, scriptFile, upperLimit, url);

        // query script by id and compare fields
        QAScript qascript = qm.getQAScript(scriptId);
        assertEquals(description, qascript.getDescription());
        assertEquals(schemaId, qascript.getSchemaId());
        assertEquals(fileName, qascript.getFileName());
        assertEquals(shortName, qascript.getShortName());
        assertEquals(resultType, qascript.getResultType());
        assertEquals(scriptType, qascript.getScriptType());
        assertEquals(upperLimit, qascript.getUpperLimit());
        assertEquals(url, qascript.getUrl());
    }

    /**
     * The method updates QA Script content in file system
     *
     * @throws Exception
     */
    public void testStoreQAScriptFromString() throws Exception {

        String scriptId = "49";
        String user = TestConstants.TEST_ADMIN_USER;

        QAScriptManager qm = new QAScriptManager();
        QAScript script = qm.getQAScript(scriptId);

        String content = script.getScriptContent();
        String newLine = "(:  This is the new line to be added :)\n";
        StringBuffer contentBuf = new StringBuffer(newLine);
        contentBuf.append(content);

        qm.storeQAScriptFromString(user, scriptId, contentBuf.toString());
        script = qm.getQAScript(scriptId);
        String newContent = script.getScriptContent();

        assertTrue(newContent.startsWith(newLine));
        assertEquals(newContent.length(), content.length() + newLine.length());
    }
}
