package eionet.gdem.web.spring.scripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.jpa.Entities.ScriptRulesEntry;
import eionet.gdem.qa.XQScript;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.WebContextConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static eionet.gdem.test.TestConstants.ADMIN_USER;
import static eionet.gdem.test.TestConstants.SESSION_USER;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
public class QAScriptsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;

    private String scriptRulesString;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        DbHelper.setUpDatabase(dataSource, TestConstants.SEED_DATASET_QA_XML);
        ObjectMapper mapper = new ObjectMapper();
        List<ScriptRulesEntry> rules = new ArrayList<>();
        ScriptRulesEntry rule = new ScriptRulesEntry().setField("collection path").setType("includes").setValue("test");
        rules.add(rule);
        scriptRulesString = mapper.writeValueAsString(rules);
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/scripts"))
            .andExpect(model().attributeExists("scripts"))
            .andExpect(model().attribute("scripts", hasProperty("qascripts", hasSize(11))));
    }

    @Test
    public void add() throws Exception {
        mockMvc.perform(get("/scripts/add"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("form"))
            .andExpect(model().attribute("form", instanceOf(QAScriptForm.class)));
    }

    @Test
    public void addSubmit() throws Exception {
        MockMultipartFile file = new MockMultipartFile("scriptFile", "test.xq", "application/xml", "1 + 1".getBytes());
        mockMvc.perform(fileUpload("/scripts")
                .file(file)
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("add", "")
                .param("schema", "http://localhost/not_existing2.xsd")
                .param("schemaId", "88")
                .param("scriptRules", scriptRulesString)
                .param("shortName", "test"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void history() throws Exception {
        mockMvc.perform(get("/scripts/25/history"))
                .andExpect(status().isOk());
    }

    @Test
    public void show() throws Exception {
        mockMvc.perform(get("/scripts/25"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("form", hasProperty("shortName", is("Summer ozone - information"))));

    }

    @Test
    public void edit() throws Exception {
        mockMvc.perform(get("/scripts/25/edit").sessionAttr(SESSION_USER, ADMIN_USER))
                .andExpect(status().isOk());
    }

    @Test
    public void upload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("scriptFile", "test.xq", "application/xml", "1 + 1".getBytes());
        mockMvc.perform(fileUpload("/scripts")
                .file(file)
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("upload", "")
                .param("schema", "http://localhost/not_existing2.xsd")
                .param("schemaId", "88")
                .param("shortName", "test")
                .param("scriptId", "54")
                .param("fileName", "previous.xq")
                .param("scriptRules", scriptRulesString)
                .param("scriptType", XQScript.SCRIPT_LANG_XQUERY3))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void update() throws Exception {
        mockMvc.perform(post("/scripts")
            .sessionAttr(SESSION_USER, ADMIN_USER)
            .param("update", "")
            .param("scriptId","25")
            .param("schemaId", "62")
            .param("shortName", "test")
            .param("scriptType", XQScript.SCRIPT_LANG_XQUERY3)
            .param("scriptRules", scriptRulesString)
            .param("fileName", "sum-oz_info_1920_1.xql"))
            .andExpect(model().hasNoErrors())
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void deleteGet() throws Exception {
        // not authorized
        // todo fix
/*        mockMvc.perform(get("/scripts/25/delete"))
                .andExpect(status().isUnauthorized());*/

        mockMvc.perform(get("/scripts/25/delete").sessionAttr(SESSION_USER, ADMIN_USER))
            .andExpect(model().hasNoErrors())
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/scripts"));
    }

    @Test
    public void deletePost() throws Exception {
        mockMvc.perform(post("/scripts")
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("delete", "")
                .param("scriptId", "25")
                .param("schemaId", "62"))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/schemas/62/scripts"));

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