package eionet.gdem.web.spring.schemas;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.WebContextConfig;
import eionet.gdem.web.spring.scripts.QAScriptForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

import static eionet.gdem.test.TestConstants.ADMIN_USER;
import static eionet.gdem.test.TestConstants.SESSION_USER;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 *
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration(classes = ApplicationTestContext.class),
        @ContextConfiguration(classes = WebContextConfig.class)
})
public class SchemasScriptsControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemasScriptsControllerTest.class);

    private static final String SCRIPTS_URL = "/schemas/1/scripts";
    private static final String SCRIPTS_ADD_URL = SCRIPTS_URL + "/add";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        DbHelper.setUpDatabase(dataSource, TestConstants.SEED_DATASET_UPL_SCHEMAS_XML);
    }

    @Test
    public void scripts() throws Exception {
        mockMvc.perform(get(SCRIPTS_URL).sessionAttr(SESSION_USER, ADMIN_USER))
                .andExpect(model().attributeExists("schemaId", "scripts", "schemaForm", "scriptForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("/schemas/scripts"));
    }

    @Test
    public void scriptsNoPermissions() throws Exception {
        //TODO: check that a non-logged in user has no permissions to see "Add QA script" and "Run QA service" links in view.
    }

    @Test
    public void scriptsAdd() throws Exception {
        this.mockMvc.perform(get(SCRIPTS_ADD_URL).sessionAttr(SESSION_USER, ADMIN_USER))
                .andExpect(model().attribute("form", instanceOf(QAScriptForm.class)))
                .andExpect(model().attributeExists("resulttypes", "scriptlangs"))
                .andExpect(view().name("/scripts/add"))
                .andExpect(status().isOk());
    }
}