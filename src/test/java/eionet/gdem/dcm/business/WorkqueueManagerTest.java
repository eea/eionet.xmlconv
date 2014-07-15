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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.dbunit.IDatabaseTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.qa.XQScript;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, Tieto Estonia WorkqueueManagerTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class WorkqueueManagerTest {

    @Autowired
    private IDatabaseTester databaseTester;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDefaultDatabaseTester(databaseTester, TestConstants.SEED_DATASET_QA_XML);
    }

    /**
     * The method adds a new QA job into the workqueue
     *
     * @throws Exception
     */
    @Test
    public void testAddQAScriptToWorkqueue() throws Exception {

        String user = TestConstants.TEST_ADMIN_USER;
        String sourceUrl = "http://test.eu/test.xml";
        String scriptType = XQScript.SCRIPT_LANG_XQUERY;
        String scriptContent =
                "xquery version \"1.0\";\n let $numbers := (1,2,3,4) "
                        + "\n for $n in $numbers \n return \n <number>{data($n)}</number>\n";

        WorkqueueManager wqm = new WorkqueueManager();

        String jobId = wqm.addQAScriptToWorkqueue(user, sourceUrl, scriptContent, scriptType);
        WorkqueueJob job = wqm.getWqJob(jobId);
        String contentFile = job.getScriptFile();
        String content = Utils.readStrFromFile(contentFile);

        assertEquals(sourceUrl, job.getUrl());
        assertEquals(0, job.getStatus());
        assertEquals("0", job.getScriptId());
        assertEquals(content, scriptContent);
    }

    /**
     * The method adds several jobs to workqueue
     *
     * @throws Exception
     */
    @Test
    public void testAddSchemaScriptsToWorkqueue() throws Exception {

        String user = TestConstants.TEST_ADMIN_USER;
        String sourceUrl = "http://test.eu/test.xml";
        String schemaUrl = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/species.xsd";

        WorkqueueManager wqm = new WorkqueueManager();

        List<String> jobIds = wqm.addSchemaScriptsToWorkqueue(user, sourceUrl, schemaUrl);
        for (int i = 0; i < jobIds.size(); i++) {
            String jobId = jobIds.get(i);
            WorkqueueJob job = wqm.getWqJob(jobId);
            assertEquals(sourceUrl, job.getUrl());
            assertEquals(0, job.getStatus());
            int scriptId = new Integer(job.getScriptId());
            assertTrue(scriptId > 0 || scriptId == -1); // related with script
                                                        // or it is validation
                                                        // job
        }
    }
}
