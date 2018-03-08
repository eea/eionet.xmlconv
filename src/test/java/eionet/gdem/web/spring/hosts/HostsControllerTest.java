package eionet.gdem.web.spring.hosts;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.WebContextConfig;
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

import javax.sql.DataSource;

import static eionet.gdem.test.TestConstants.ADMIN_USER;
import static eionet.gdem.test.TestConstants.SESSION_USER;
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
@ContextHierarchy({
        @ContextConfiguration(classes = ApplicationTestContext.class),
        @ContextConfiguration(classes = WebContextConfig.class)
})
public class HostsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        DbHelper.setUpDatabase(dataSource, TestConstants.SEED_DATASET_HOSTS_XML);
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(get("/hosts").sessionAttr(SESSION_USER, ADMIN_USER))
                .andExpect(view().name("/hosts/list"))
                .andExpect(status().isOk());
    }

    @Test
    public void listDenied() throws Exception {
        mockMvc.perform(get("/hosts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void edit() throws Exception {
        mockMvc.perform(get("/hosts/1/edit").sessionAttr(SESSION_USER, ADMIN_USER))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("form", hasProperty("host")))
                .andExpect(model().attribute("form", hasProperty("username")))
                .andExpect(model().attribute("form", hasProperty("password")))
                .andExpect(view().name("/hosts/edit"))
                .andExpect(status().isOk());
    }

    @Test
    public void update() throws Exception {
        mockMvc.perform(post("/hosts").sessionAttr(SESSION_USER, ADMIN_USER)
                .param("update", "")
                .param("id", "1")
                .param("host", "http://updated.dev")
                .param("username", "updated")
                .param("password", "updated"))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void add() throws Exception {
        mockMvc.perform(get("/hosts/add").sessionAttr(SESSION_USER, ADMIN_USER))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("form", hasProperty("host")))
                .andExpect(model().attribute("form", hasProperty("username")))
                .andExpect(model().attribute("form", hasProperty("password")))
                .andExpect(view().name("/hosts/add"));
    }

    @Test
    public void addSubmit() throws Exception {
        mockMvc.perform(post("/hosts")
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("add", "")
                .param("host", "test")
                .param("username", "testuser")
                .param("password", "testpass"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("redirect:/hosts"));
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(post("/hosts")
                .sessionAttr(SESSION_USER, ADMIN_USER)
                .param("delete", "")
                .param("id", "1"))
                .andExpect(view().name("redirect:/hosts"));
    }
}