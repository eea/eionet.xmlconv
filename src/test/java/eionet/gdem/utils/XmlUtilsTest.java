package eionet.gdem.utils;

import eionet.gdem.test.ApplicationTestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.hamcrest.CoreMatchers.is;


import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class XmlUtilsTest {

    @Test
    public void testGetNumberOfBytesBasedOnUrl() {
        String url = null;
        assertThat(XmlUtils.getNumberOfBytesBasedOnUrl(url), is(nullValue()));
    }
}
