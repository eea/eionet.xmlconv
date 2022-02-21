package eionet.gdem.api;

import eionet.gdem.jpa.Entities.AlertEntry;
import eionet.gdem.jpa.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertsApiController {

    private AlertService alertService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertsApiController.class);

    @Autowired
    public AlertsApiController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/get/all")
    public List<AlertEntry> getAllProperties() {
        return alertService.findAll();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void save(@RequestBody AlertEntry alertEntry) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(alertEntry.getOccurrenceDateMod());
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            alertEntry.setOccurrenceDate(timestamp);
            alertService.save(alertEntry);
        } catch (ParseException e) {
            LOGGER.error("Error parsing alert entry occurrence date of alert " + alertEntry);
        }
    }

    @DeleteMapping(value = "/delete/{alertId}")
    public void delete(@PathVariable(name = "alertId") Integer alertId) {
        alertService.delete(alertId);
    }
}
