package eionet.gdem.web.spring.schemas;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.WebContextConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.mail.internet.ContentType;
import javax.sql.DataSource;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static eionet.gdem.test.TestConstants.ADMIN_USER;
import static eionet.gdem.test.TestConstants.SESSION_USER;
import static org.junit.Assert.*;
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
public class SchemasControllerTest {

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
    public void list() throws Exception {
        mockMvc.perform(get("/schemas"))
                .andExpect(view().name("/schemas/list"))
                .andExpect(model().attributeExists("form"))
                .andExpect(status().isOk());
    }

    @Test
    public void add() throws Exception {
        mockMvc.perform(get("/schemas/add"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", instanceOf(UploadSchemaForm.class)))
                .andExpect(view().name("/schemas/add"));
    }

    @Test
    public void addSubmit() throws Exception {
        mockMvc.perform(post("/schemas/add")
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("schemaUrl", "http://test.gr/test.xsd"))
                .andExpect(view().name("redirect:/schemas"));
    }

    @Test
    public void addSubmitFileUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("schemaFile", "test.xsd", MediaType.APPLICATION_XML_VALUE, "test".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/schemas/add")
                .file(file)
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("schemaUrl", "http://test.gr/test.xsd"))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/schemas"));
    }

    @Test
    public void addSubmitFileUploadNoPermissions() throws Exception {
        MockMultipartFile file = new MockMultipartFile("schemaFile", "test.xsd", MediaType.APPLICATION_XML_VALUE, "test".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/schemas/add")
                .file(file)
                .param("schemaUrl", "http://test.gr/test.xsd"))
                .andExpect(model().hasNoErrors())
                .andExpect(status().isUnauthorized())
                .andExpect(view().name("Error"));
    }

    @Test
    public void addDeniedModelError() throws Exception {
        mockMvc.perform(post("/schemas/add")
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("schemaUrl", ""))
                .andExpect(model().hasErrors())
                .andExpect(view().name("/schemas/add"));
    }

    @Test
    public void displayBySchemaId() throws Exception {
        mockMvc.perform(get("/schemas/1"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("form", allOf(
                        hasProperty("schema"),
                        hasProperty("description"),
                        hasProperty("schemaLang", is("XSD")),
                        hasProperty("doValidation"),
                        hasProperty("blocker"),
                        hasProperty("expireDate"),
                        hasProperty("uplSchemaFileName"))))
                .andExpect(view().name("/schemas/view"));
    }

    @Test
    public void displayBySchemaUrl() throws Exception {
        mockMvc.perform(get("/schemas/one").param("schemaUrl","http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("form", allOf(
                        hasProperty("schema",is("http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd")),
                        hasProperty("schemaId",is("83")),
                        hasProperty("schemaLang", is("XSD")),
                        hasProperty("doValidation",is(true)))))
                .andExpect(view().name("/schemas/view"));
    }
    @Test
    public void showNoPermissions() throws Exception {
        mockMvc.perform(get("/schemas/1"))
                .andExpect(model().attribute("rootElements", hasProperty("xsduPrm", is(false))));
    }

    @Test
    public void showWithPermissions() throws Exception {
        mockMvc.perform(get("/schemas/1")
                .sessionAttr(SESSION_USER, ADMIN_USER))
                .andExpect(model().attribute("rootElements", hasProperty("xsduPrm", is(true))));
    }

    @Test
    public void editWithoutPermissions() throws Exception {
        mockMvc.perform(get("/schemas/1/edit"))
                .andExpect(model().attribute("form", instanceOf(SchemaForm.class)))
                .andExpect(model().attribute("form", hasProperty("schema", is("http://dd.eionet.europa.eu/GetSchema?id=TBL4564"))))
                .andExpect(model().attribute("rootElements",hasProperty("xsduPrm",is(false))))
                .andExpect(view().name("/schemas/edit"));
    }

    @Test
    public void editWithPermissions() throws Exception {
        mockMvc.perform(get("/schemas/1/edit").sessionAttr(SESSION_USER, ADMIN_USER))
                .andExpect(model().attribute("rootElements", hasProperty("xsduPrm", is(true))))
                .andExpect(view().name("/schemas/edit"));
    }

    @Test
    public void editSubmit() throws Exception {
        mockMvc.perform(post("/schemas").sessionAttr(SESSION_USER, ADMIN_USER)
                .param("update", "")
                .param("schema", "http://dd.eionet.europa.eu/GetSchema?id=TBL4564")
                .param("schemaLang", "XSD")
                .param("schemaId", "1"))
                .andExpect((model().hasNoErrors()))
                .andExpect(view().name("redirect:/schemas/1/edit"));
    }

    @Test
    public void editSubmitModelErrors() throws Exception {
        mockMvc.perform(post("/schemas").sessionAttr(SESSION_USER, ADMIN_USER)
                .param("update", "")
                .param("schema", "wrong_url_format")
                .param("schemaLang", "XSD")
                .param("schemaId", "1"))
                .andExpect((model().hasErrors()))
                .andExpect(view().name("/schemas/edit"));
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(post("/schemas")
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("delete", "")
                .param("schemaId","7"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("redirect:/schemas"));
    }

    @Test
    public void deleteNoPermissions() throws Exception {
        mockMvc.perform(post("/schemas")
                .param("delete", "")
                .param("schemaId","7"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("Error"));
    }
}