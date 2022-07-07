package eionet.gdem.api.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.api.WorkqueueRestController;
import eionet.gdem.web.spring.workqueue.WorkqueuePageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @MessageMapping("/websocket/workqueue/tableChanged")
    @SendTo("/websocket")
    @Scheduled(fixedDelay = 30000)
    public void sendWebSocketUpdate() throws JsonProcessingException {
        if(getChangedJobTable()) {
            this.messageTemplate.convertAndSend("/websocket", mapper.writeValueAsString(changedJobTable));
        }
        changedJobTable = false;
    }

    public static void setChangedJobTable(Boolean changedJobTable) {
        WebSocketWorkqueueController.changedJobTable = changedJobTable;
    }

    public static Boolean getChangedJobTable() {
        return changedJobTable;
    }
}
