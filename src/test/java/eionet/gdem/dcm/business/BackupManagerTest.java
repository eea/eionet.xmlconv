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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.web.spring.scripts.BackupManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dto.BackupDto;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

/**
 * @author Enriko Käsper, Tieto Estonia BackupManagerTest
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class BackupManagerTest {

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
     * The method adds UPL schema into DB, then it edits the properties and finally deletes the added schema. After each operation
     * it scheks the properties values.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    //TODO this doesn't always run - find out why and fix it or remove it
    public void testFileBackup() throws Exception {

        String folderName = Properties.queriesFolder;
        String fileName = TestConstants.SEED_QASCRIPT_TEST;
        String id = "100";
        String user = "testuser";

        // create backup folder and 2 new backupfiles
        BackupManager bm = new BackupManager();
        bm.backupFile(folderName, fileName, id, user);
        bm.backupFile(folderName, fileName, id, user);

        // check if backupfolder exists
        File backupFolder = new File(folderName, Constants.BACKUP_FOLDER_NAME);
        assertTrue((backupFolder).exists());

        // get the list of backups from database
        List<BackupDto> backups = bm.getBackups(id);
        assertEquals(2, backups.size());

        // check if backup files exist in backup folder
        File backupFile1 = new File(backupFolder, backups.get(0).getFileName());
        File backupFile2 = new File(backupFolder, backups.get(1).getFileName());
        assertTrue((backupFile1).exists());
        assertTrue((backupFile2).exists());

        // purge all backups
        int deleted = bm.purgeBackup(-1); // purge files before tomorrow
        assertEquals(deleted >= 2, true);

        // check if database rows are deleted
        backups = bm.getBackups(id);
        assertEquals(0, backups.size());

        // check if files are deleted
        assertFalse((backupFile1).exists());
        assertFalse((backupFile2).exists());

    }

}
