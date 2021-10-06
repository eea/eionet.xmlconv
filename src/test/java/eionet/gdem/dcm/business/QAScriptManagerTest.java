package eionet.gdem.dcm.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.qa.QAScriptManager;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.scripts.QAScriptListHolder;
import net.xqj.basex.bin.M;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.Properties;
import eionet.gdem.dto.QAScript;
import eionet.gdem.dto.Schema;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS SchemaManagerDBTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class QAScriptManagerTest {

    @Autowired
    private DataSource db;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_QA_XML);
    }

    /**
     * The method adds QA Script into DB, then it edits the properties and finally deletes the added schema. After each operation it
     * checks the properties values.
     *
     * @throws Exception
     */
    @Test
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

        QAScriptManager qm = new QAScriptManager();

        MockMultipartFile scriptFile = new MockMultipartFile("file", queryFileName, MediaType.APPLICATION_XML_VALUE, getClass().getClassLoader().getResource(queryFileName).getFile().getBytes());
        String scriptId = qm.add(TestConstants.ADMIN_USER, shortName, schemaId, schema, resultType, description, scriptType, scriptFile, upperLimit, url, false, true);

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
        assertEquals(false, qascript.isAsynchronousExecution());

    }

    /**
     * The method test if it is possible to update schema validation flag
     *
     * @throws Exception
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
        qm.update(user, scriptId, shortName, schemaId, resultType, description, scriptType, fileName, upperLimit, url, content, false, false, true, 1);

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
    @Test
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

        //delete test file if exists
        FileUtils.deleteQuietly(new File(Properties.queriesFolder + File.separator + TestConstants.SEED_QASCRIPT_XQUERY2));

        MockMultipartFile scriptFile = new MockMultipartFile("scriptFile", fileName, MediaType.APPLICATION_XML_VALUE, getClass().getClassLoader().getResource(fileName).getFile().getBytes());

        QAScriptManager qm = new QAScriptManager();
        qm.update(user, scriptId, shortName, schemaId, resultType, description, scriptType, fileName, scriptFile, upperLimit, url, false, true, 1);

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
}
