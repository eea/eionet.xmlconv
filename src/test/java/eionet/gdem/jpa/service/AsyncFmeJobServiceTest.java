package eionet.gdem.jpa.service;

import eionet.gdem.test.ApplicationTestContext;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class AsyncFmeJobServiceTest {

    @Mock
    RestTemplate restTemplate;

    @Spy
    @InjectMocks
    AsyncFmeJobServiceImpl asyncFmeJobService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    public void testJobHasStatusSuccessOnFmeServer() {
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class) , eq(Object.class))).thenReturn(responseEntity);
        asyncFmeJobService.jobHasStatusSuccessOnFmeServer(Long.valueOf("455465"));
        verify(asyncFmeJobService).jobHasStatusSuccessOnFmeServer(anyLong());
    }

    @Test
    public void testCancelJobOnFMEServer() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);
        asyncFmeJobService.cancelJobOnFMEServer(Long.valueOf("455465"));
        verify(asyncFmeJobService).cancelJobOnFMEServer(anyLong());
    }

}





























