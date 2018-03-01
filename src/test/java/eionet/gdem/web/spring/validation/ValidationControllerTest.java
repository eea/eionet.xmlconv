package eionet.gdem.web.spring.validation;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.WebContextConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 *
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebContextConfig.class, ApplicationTestContext.class})
public class ValidationControllerTest {

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
    public void form() throws Exception {
        mockMvc.perform(get("/validation"))
                .andExpect(model().attributeExists("form"))
                .andExpect(view().name("/validation"));
    }

    @Test
    public void submit() throws Exception {
        mockMvc.perform(post("/validation")
                .param("xmlUrl", TestUtils.getLocalURL(TestConstants.SEED_DATASET_QA_XML))
                .param("schemaUrl", ""))
                .andExpect(model().hasNoErrors())
                .andExpect(status().isOk())
                .andExpect(view().name("/validation"));
    }
}