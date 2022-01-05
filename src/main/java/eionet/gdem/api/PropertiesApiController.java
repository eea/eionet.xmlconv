package eionet.gdem.api;

import eionet.gdem.jpa.Entities.PropertiesEntry;
import eionet.gdem.jpa.service.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
public class PropertiesApiController {

    private PropertiesService propertiesService;

    @Autowired
    public PropertiesApiController(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    @GetMapping("/get/all")
    public List<PropertiesEntry> getAllProperties() {
        return propertiesService.findAll();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void save(@RequestBody PropertiesEntry propertiesEntry) {
        propertiesService.save(propertiesEntry);
    }

    @DeleteMapping(value = "/delete/{propertyId}")
    public void delete(@PathVariable(name = "propertyId") Integer propertyId) {
        propertiesService.delete(propertyId);
    }
}
