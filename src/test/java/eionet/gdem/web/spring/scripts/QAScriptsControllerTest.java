package eionet.gdem.web.spring.scripts;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.WebContextConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 *
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebContextConfig.class, ApplicationTestContext.class})
public class QAScriptsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        DbHelper.setUpDatabase(dataSource, TestConstants.SEED_DATASET_UPLXML_XML);
    }

    @Test
    public void list() {
    }

    @Test
    public void add() {
    }

    @Test
    public void history() {
    }

    @Test
    public void show() {
    }

    @Test
    public void edit() {
    }

    @Test
    public void editSubmit() {
    }

    @Test
    public void addSubmit() {
    }

    @Test
    public void delete1() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void activate() {
    }

    @Test
    public void deactivate() {
    }

    @Test
    public void toggleSchemaValidation() {
    }
}