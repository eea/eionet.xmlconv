package eionet.gdem.api.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.api.WorkqueueRestController;
import eionet.gdem.web.spring.workqueue.WorkqueuePageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketWorkqueueController {

    private ObjectMapper mapper = new ObjectMapper();
    private WorkqueueRestController workqueueRestController;
    private SimpMessagingTemplate messageTemplate;
    private static volatile Boolean changedJobTable = false;

    @Autowired
    public WebSocketWorkqueueController(SimpMessagingTemplate messageTemplate, WorkqueueRestController workqueueRestController) {
        this.messageTemplate = messageTemplate;
        this.workqueueRestController = workqueueRestController;
    }

    @Scheduled(fixedDelay = 20000)
    public void sendWebSocketUpdate() throws JsonProcessingException {
       this.messageTemplate.convertAndSend("/new/workqueue",
                mapper.writeValueAsString(changedJobTable));
        changedJobTable = false;
    }
}
