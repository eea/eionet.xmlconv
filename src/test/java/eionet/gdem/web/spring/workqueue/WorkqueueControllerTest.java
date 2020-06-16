package eionet.gdem.web.spring.workqueue;

import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.WebContextConfig;
import eionet.gdem.utils.json.Json;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.junit.Ignore;

import static eionet.gdem.test.TestConstants.ADMIN_USER;
import static eionet.gdem.test.TestConstants.SESSION_USER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.*;
import org.springframework.http.MediaType;

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
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/workqueue").sessionAttr(SESSION_USER, ADMIN_USER)
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
                    .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/workqueue").sessionAttr(SESSION_USER, ADMIN_USER)
                .param("restart", "")
                .param("jobs", new String[]{"1"}))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("redirect:/workqueue"));
    }

    @Test
    public void getJobDetailsEmptyId() throws Exception {
        Json expected = new Json();
        MvcResult actual = mockMvc.perform(post("/workqueue/getJobDetails/job3").accept(String.valueOf(MediaType.APPLICATION_JSON))).andReturn();
        String content = actual.getResponse().getContentAsString();
        Assert.assertThat(actual.getResponse().getStatus(), is(HttpStatus.SC_OK));
        Assert.assertThat(content, is("[]"));
    }

    @Test
    public void getJobDetailsIdWithNoEntries() throws Exception {
        Json expected = new Json();
        MvcResult actual = mockMvc.perform(post("/workqueue/getJobDetails/job3").accept(String.valueOf(MediaType.APPLICATION_JSON))).andReturn();
        String content = actual.getResponse().getContentAsString();
        Assert.assertThat(actual.getResponse().getStatus(), is(HttpStatus.SC_OK));
        Assert.assertThat(content, is("[]"));
    }


        //TODO fix broken test with json body comparison 
    @Test
    @Ignore
    public void getJobDetailsIdWithEntries() throws Exception {
        Json expected = new Json();
        mockMvc.perform(post("/workqueue/getJobDetails/job2").accept(MediaType.APPLICATION_JSON)).andDo(print());

         mockMvc.perform(post("/getJobDetails/job2").accept(MediaType.APPLICATION_JSON))
        .andExpect(content().json("{\"id\":1,\"jobName\":\"job2\",\"status\":1,"+
        "\"dateAdded\":1500804611000,\"url\":null,\"xqFile\":null,\"resultFile\":null,\"xqType\":null,"+
        "\"duration\":null,\"fullStatusName\":\"DOWNLOADING SOURCE FILE\"},{\"id\":3,\"jobName\":\"job2\",\"status\":5,\"dateAdded\":1500804611000,"+
        "\"url\":null,\"xqFile\":null,\"resultFile\":null,\"xqType\":null,\"duration\":null,\"fullStatusName\":\"LIGHT ERROR\"},"+
        "{\"id\":4,\"jobName\":\"job2\",\"status\":2,\"dateAdded\":1500804611000,\"url\":null,\"xqFile\":null,\"resultFile\":null,"+
        "\"xqType\":null,\"duration\":null,\"fullStatusName\":\"PROCESSING JOB\"}]"));
       

    }
}