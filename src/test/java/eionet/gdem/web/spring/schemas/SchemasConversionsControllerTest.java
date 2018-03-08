package eionet.gdem.web.spring.schemas;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.WebContextConfig;
import eionet.gdem.web.spring.stylesheet.StylesheetForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static eionet.gdem.test.TestConstants.ADMIN_USER;
import static eionet.gdem.test.TestConstants.SESSION_USER;

import javax.sql.DataSource;

import static org.junit.Assert.*;
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
public class SchemasConversionsControllerTest {

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

    @Test
    public void conversions() throws Exception {
        mockMvc.perform(get("/schemas/1/conversions"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("schemaId", "1"))
            .andExpect(view().name("/schemas/conversions"));
    }

    @Test
    public void conversionsAdd() throws Exception {
        mockMvc.perform(get("/schemas/1/conversions/add").sessionAttr(SESSION_USER, ADMIN_USER))
            .andExpect(model().attribute("form", instanceOf(StylesheetForm.class)))
            .andExpect(model().attributeExists("outputtypes", "schemaId"))
            .andExpect(view().name("/conversions/add"));
    }
}