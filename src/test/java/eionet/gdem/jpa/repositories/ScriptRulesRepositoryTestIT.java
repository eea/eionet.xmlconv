package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.ScriptRulesEntry;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ScriptRulesRepositoryTestIT {

    @Autowired
    private DataSource db;

    @Autowired
    ScriptRulesRepository scriptRulesRepository;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpProperties(this);
        DbHelper.setUpDatabase(db, TestConstants.SEED_DATASET_SCRIPT_RULES_XML);
    }

    @Test
    public void testFindByQueryId() {
        List<ScriptRulesEntry> rules = scriptRulesRepository.findByQueryId(100);
        assertThat(rules.size(), is(2));
    }
}

















