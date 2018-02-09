package eionet.gdem.web.spring.conversions;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import eionet.gdem.test.ApplicationTestContext;
import eionet.gdem.test.WebContextConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.Assert.*;


/**
 * sample spring mvc test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
//loader = AnnotationConfigWebContextLoader.class,
@ContextConfiguration(classes = {WebContextConfig.class})
public class ConversionsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private WebClient webClient;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        webClient = MockMvcWebClientBuilder.webAppContextSetup(webApplicationContext).build();
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Test
    public void main() throws Exception {
        mockMvc.perform(get("/conversions"));
    }

    @Test
    public void formTest() throws IOException {
        HtmlPage page = webClient.getPage("http://localhost/");
        System.out.println("out");
    }


}