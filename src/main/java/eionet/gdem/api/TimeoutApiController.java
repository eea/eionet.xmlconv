package eionet.gdem.api;

import eionet.gdem.models.TimeoutEntity;
import eionet.gdem.services.TimeoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/timeoutProperties")
public class TimeoutApiController {

    private TimeoutService timeoutService;

    @Autowired
    public TimeoutApiController(TimeoutService timeoutService) {
        this.timeoutService = timeoutService;
    }

    @GetMapping("/get/all")
    public List<TimeoutEntity> getAllTimeoutProperties() {
        return timeoutService.getAllTimeoutProperties();
    }

}
