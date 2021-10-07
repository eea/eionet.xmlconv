package eionet.gdem.api;

import eionet.gdem.jpa.Entities.QueryHistoryEntry;
import eionet.gdem.jpa.service.QueryHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/scripts/history")
public class ScriptsHistoryController {

    private QueryHistoryService queryHistoryService;

    @Autowired
    public ScriptsHistoryController(QueryHistoryService queryHistoryService) {
        this.queryHistoryService = queryHistoryService;
    }

    @GetMapping("/all")
    public List<QueryHistoryEntry> getAllScriptsHistory() {
        return queryHistoryService.findAll();
    }

}
