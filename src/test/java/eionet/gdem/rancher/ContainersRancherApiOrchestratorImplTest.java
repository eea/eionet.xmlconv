package eionet.gdem.rancher;

import eionet.gdem.rancher.exception.RancherApiException;
import eionet.gdem.rancher.model.ContainerApiResponse;
import eionet.gdem.rancher.model.ContainerData;
import eionet.gdem.rancher.service.ContainersRancherApiOrchestratorImpl;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ContainersRancherApiOrchestratorImplTest {

    @Mock
    RestTemplate restTemplate;

    @Spy
    @InjectMocks
    ContainersRancherApiOrchestratorImpl containersRancherApiOrchestrator;

    ContainerApiResponse containerApiResponse;
    List<ContainerData> containerDataList;
    ContainerData containerData;
    private static final String CONTAINER_ID = "1i1646587";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        containerDataList = new ArrayList<>();
        containerData = new ContainerData();
        containerData.setId(CONTAINER_ID);
        containerDataList.add(containerData);
        containerApiResponse = new ContainerApiResponse();
        containerApiResponse.setData(containerDataList);
    }

    @Test
    public void testGetContainerId() throws RancherApiException {
        doReturn(containerApiResponse).when(containersRancherApiOrchestrator).getContainerInfo(anyString());
        String actualRes = containersRancherApiOrchestrator.getContainerId(anyString());
        Assert.assertEquals(CONTAINER_ID, actualRes);
    }

    @Test
    public void testGetContainerInfo() throws RancherApiException {
        ResponseEntity<ContainerApiResponse> responseEntity = new ResponseEntity<>(containerApiResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(ContainerApiResponse.class), anyString())).thenReturn(responseEntity);
        ContainerApiResponse actualRes = containersRancherApiOrchestrator.getContainerInfo("testContainer");
        Assert.assertEquals(containerApiResponse, actualRes);
    }

    @Test
    public void testStartContainer() throws RancherApiException {
        doReturn(CONTAINER_ID).when(containersRancherApiOrchestrator).getContainerId(anyString());
        ResponseEntity<ContainerData> responseEntity = new ResponseEntity<>(containerData, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ContainerData.class))).thenReturn(responseEntity);
        ContainerData actualRes = containersRancherApiOrchestrator.startContainer("testContainer");
        Assert.assertEquals(containerData, actualRes);
    }

    @Test
    public void testStopContainer() throws RancherApiException {
        doReturn(CONTAINER_ID).when(containersRancherApiOrchestrator).getContainerId(anyString());
        ResponseEntity<ContainerData> responseEntity = new ResponseEntity<>(containerData, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ContainerData.class))).thenReturn(responseEntity);
        ContainerData actualRes = containersRancherApiOrchestrator.stopContainer("testContainer");
        Assert.assertEquals(containerData, actualRes);
    }

    @Test
    public void testRestartContainer() throws RancherApiException {
        doReturn(CONTAINER_ID).when(containersRancherApiOrchestrator).getContainerId(anyString());
        containerData.setState("running");
        ResponseEntity<ContainerData> responseEntity = new ResponseEntity<>(containerData, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ContainerData.class))).thenReturn(responseEntity);
        ContainerData actualRes = containersRancherApiOrchestrator.restartContainer("testContainer");
        Assert.assertEquals(containerData, actualRes);
    }

}





































