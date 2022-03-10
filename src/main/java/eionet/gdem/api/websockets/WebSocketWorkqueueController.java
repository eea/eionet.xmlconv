package eionet.gdem.api.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.api.WorkqueueRestController;
import eionet.gdem.web.spring.workqueue.WorkqueuePageInfo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketWorkqueueController {

    private ObjectMapper mapper;
    private WorkqueueRestController workqueueRestController;
    private SimpMessagingTemplate messageTemplate;

    public WebSocketWorkqueueController(ObjectMapper mapper, SimpMessagingTemplate messageTemplate, WorkqueueRestController workqueueRestController) {
        this.mapper = mapper;
        this.messageTemplate = messageTemplate;
        this.workqueueRestController = workqueueRestController;
    }

    @Scheduled(fixedDelay = 1000)
    public void sendWebSocketUpdate() throws JsonProcessingException {
        WorkqueuePageInfo workqueuePageInfo = workqueueRestController.getWorkqueuePageInfo();
        this.messageTemplate.convertAndSend("/info/values",
                mapper.writeValueAsString(modelInfoHolder.getModelInfoList()));
    }
}
