package eionet.gdem.web.spring.xmlfile;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.WebContextConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.*;

/**
 *
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebContextConfig.class, ApplicationTestContext.class})
public class XmlFilesControllerTest {

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

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/xmlFiles"))
                .andExpect(view().name("/xmlfiles/list"))
                .andExpect(model().attributeExists("xmlfiles", "form"));
    }

    @Test
    public void add() throws Exception {
        mockMvc.perform(get("/xmlFiles/add"))
                .andExpect(view().name("/xmlfiles/add"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    public void upload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("test", "test.xml", "application/xml", "test".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/xmlFiles")
                .file(file).param("add", "").param("form", ""))
                .andExpect(status().isOk());
    }

    @Test
    public void edit() throws Exception {
        mockMvc.perform(get("/xmlFiles/1/edit"))
            .andExpect(model().attributeExists("form"))
            .andExpect(model().attribute("form", hasProperty("title", is("Boundaries of EU  countries"))))
            .andExpect(model().attribute("form", hasProperty("xmlFileName", is("seed-ozone-station.xml"))));
    }

    @Test
    public void update() {

    }

    @Test
    public void delete() {

    }

}