package eionet.gdem.web.spring.workqueue;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class WorkqueueControllerTest {


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        DbHelper.setUpDatabase(dataSource, TestConstants.SEED_DATASET_QAJOBS_XML);
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/workqueue"))
                .andExpect(model().attributeExists("permissions"))
                .andExpect(model().attributeExists("jobList"))
                .andExpect(model().attribute("form", instanceOf(WorkqueueForm.class)));
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(post("/workqueue")
                .param("delete", "")
                .param("jobs", new String[]{"1"}))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("redirect:/workqueue"));
    }

    @Test
    public void restart() throws Exception {
        mockMvc.perform(post("/workqueue")
                .param("restart", "")
                .param("jobs", new String[]{"1"}))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("redirect:/workqueue"));
    }
}