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
import static org.junit.Assert.assertNull;

import java.util.List;

import eionet.gdem.web.spring.schemas.ISchemaDao;
import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.EqualPredicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eionet.gdem.dto.Schema;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class SchemaDaoTest {

    @Autowired
    private DataSource db;

    @Autowired
    private ISchemaDao schemaDao;

    /**
     * Set up test case properties and databaseTester.
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);
    }

    @Test
    public void getSchemasWithRelations() {
        List<Schema> schemas = schemaDao.getSchemasWithRelations();

        Schema schema = (Schema) CollectionUtils.find(schemas, new BeanPredicate("id", new EqualPredicate("1")));

        assertEquals("http://dd.eionet.europa.eu/GetSchema?id=TBL4564", schema.getSchema());
        assertEquals("Groundwater schema", schema.getDescription());
        assertEquals("seed-gw-schema.xsd", schema.getUplSchemaFileName());
        assertEquals(2, schema.getCountQaScripts());
        assertEquals(2, schema.getCountStylesheets());

    }

    @Test
    public void getSchemasWithNoRelations() {
        List<Schema> schemas = schemaDao.getSchemasWithRelations();

        Schema schema = (Schema) CollectionUtils.find(schemas, new BeanPredicate("id", new EqualPredicate("6")));

        assertEquals("http://dd.eionet.europa.eu/GetSchema?id=TBL112", schema.getSchema());
        assertEquals("No relations", schema.getDescription());
        assertNull(schema.getUplSchemaFileName());
        assertEquals(0, schema.getCountQaScripts());
        assertEquals(0, schema.getCountStylesheets());

    }
}
