package eionet.gdem.web.spring.conversions;

import eionet.gdem.cache.CacheManagerUtil;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.WebContextConfig;
import eionet.gdem.web.spring.stylesheet.StylesheetForm;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

import static eionet.gdem.test.TestConstants.ADMIN_USER;
import static eionet.gdem.test.TestConstants.SESSION_USER;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;



/**
 * sample spring mvc test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration(classes = ApplicationTestContext.class),
        @ContextConfiguration(classes = WebContextConfig.class)
})
public class ConversionsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        DbHelper.setUpDatabase(dataSource, TestConstants.SEED_DATASET_CONVERSIONS_XML);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/conversions"))
            .andExpect(view().name("/conversions/list"))
            .andExpect(model().attributeExists("conversions"));
    }

    @Test
    public void viewIT() throws Exception {
        mockMvc.perform(get("/conversions/{id}", 1))
                .andExpect(view().name("/conversions/view"))
                .andExpect(model().attribute("form", instanceOf(StylesheetForm.class)))
                .andExpect(model().attributeExists("outputtypes"));
    }

    @Test
    public void conversionMissing() throws Exception {
        mockMvc.perform(get("/conversions/{id}", 999))
                .andExpect(view().name("Error"));
    }

/*    @Test
    public void delete() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("delete", "1");
        map.add("value", "1");
        MvcResult result = mockMvc.perform(post("/conversions").params(map))
                .andExpect(view().name("/redirect:/conversions")).andReturn();
        List<String> errors = (List) result.getFlashMap().get("errors");
        assertTrue(errors.isEmpty());
    }*/

    @Test
    public void addIT() throws Exception {
        mockMvc.perform(get("/conversions/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("/conversions/add"))
                .andExpect(model().attributeExists("outputtypes", "form"))
                .andExpect(model().hasNoErrors());
    }

    @Test
//    todo fix
    public void addSubmitIT() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("add", "");
        map.add("add", "");
        mockMvc.perform(post("/conversions").params(map))
                .andExpect(status().isOk());
    }

    @Test
    public void upload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("xslFile", "test.xsl", "application/xml", "1 + 1".getBytes());
        mockMvc.perform(fileUpload("/conversions")
                .file(file)
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("upload", "")
                .param("outputtype", "HTML")
                .param("stylesheetId", "183")
                .param("description", "test")
                .param("xslFileName", "test"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void save() throws Exception {
        mockMvc.perform(post("/conversions")
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("save", "")
                .param("outputtype", "HTML")
                .param("stylesheetId", "183")
                .param("description", "test")
                .param("xslContent", "1 + 1")
                .param("xslFileName", "test"))
                .andExpect(status().is3xxRedirection());
    }

}