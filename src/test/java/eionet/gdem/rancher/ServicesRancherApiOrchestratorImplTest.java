package eionet.gdem.rancher;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.RancherApiNewServiceRequestBody;
import eionet.gdem.rancher.model.ServiceApiRequestBody;
import eionet.gdem.rancher.model.ServiceApiResponse;
import eionet.gdem.rancher.service.RancherApiNewServiceRequestBodyCreator;
import eionet.gdem.rancher.service.ServicesRancherApiOrchestratorImpl;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ServicesRancherApiOrchestratorImplTest {

    @Mock
    RestTemplate restTemplate;

    @Mock
    RancherApiNewServiceRequestBodyCreator newServiceRequestBodyCreator;

    @Spy
    @InjectMocks
    ServicesRancherApiOrchestratorImpl servicesRancherApiOrchestrator;

    ServiceApiResponse response;
    List<String> instanceIds;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        response = new ServiceApiResponse();
        response.setId("1s9121");
        instanceIds = new ArrayList<>();
        instanceIds.add("1i1644182");
        response.setInstanceIds(instanceIds);
        response.setState("active");
        response.setHealthState("healthy");
    }

    @Test
    public void testGetContainerInstances() throws RancherApiException {
        doReturn(response).when(servicesRancherApiOrchestrator).getServiceInfo(anyString());
        List<String> actualRes = servicesRancherApiOrchestrator.getContainerInstances("1s9121");
        Assert.assertEquals(instanceIds.get(0),actualRes.get(0));
    }

    @Test
    public void testGetServiceInfo() throws RancherApiException {
        ResponseEntity<ServiceApiResponse> result = new ResponseEntity(response, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(ServiceApiResponse.class))).thenReturn(result);
        ServiceApiResponse actualRes = servicesRancherApiOrchestrator.getServiceInfo("1s9121");
        Assert.assertEquals(result.getBody(), actualRes);
    }

    @Test
    public void testScaleUpContainerInstances() throws RancherApiException {
        ResponseEntity<ServiceApiResponse> result = new ResponseEntity(response, HttpStatus.OK);
        ServiceApiRequestBody serviceApiRequestBody = new ServiceApiRequestBody().setScale(1);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ServiceApiResponse.class))).thenReturn(result);
        doReturn(response).when(servicesRancherApiOrchestrator).getServiceInfo(anyString());
        ServiceApiResponse actualRes = servicesRancherApiOrchestrator.scaleUpOrDownContainerInstances("1s9121", serviceApiRequestBody);
        Assert.assertEquals(result.getBody(), actualRes);
    }

    @Test
    public void testCreateService() throws RancherApiException {
        RancherApiNewServiceRequestBody rancherApiNewServiceRequestBody = new RancherApiNewServiceRequestBody().setName("testService")
                .setScale(1).setStackId("1st1968").setStartOnCreate(true);
        ResponseEntity<ServiceApiResponse> result = new ResponseEntity(response, HttpStatus.OK);
        when(newServiceRequestBodyCreator.buildBody(anyString(), anyString())).thenReturn(rancherApiNewServiceRequestBody);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ServiceApiResponse.class))).thenReturn(result);
        ServiceApiResponse actualRes = servicesRancherApiOrchestrator.createService("testService");
        Assert.assertEquals(result.getBody(), actualRes);
    }

}





















