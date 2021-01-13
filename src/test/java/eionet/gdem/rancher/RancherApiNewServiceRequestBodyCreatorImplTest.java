package eionet.gdem.rancher;

import eionet.gdem.rancher.model.RancherApiNewServiceRequestBody;
import eionet.gdem.rancher.service.RancherApiNewServiceRequestBodyCreator;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class RancherApiNewServiceRequestBodyCreatorImplTest {

    @Autowired
    RancherApiNewServiceRequestBodyCreator requestBodyCreator;

    @Test
    public void testBuildBody() {
        RancherApiNewServiceRequestBody result = requestBodyCreator.buildBody("testService", "testStack");
        Assert.assertEquals("testStack", result.getStackId());
    }

}



































