package eionet.gdem.api;

import eionet.gdem.jpa.Entities.ScriptRulesEntry;
import eionet.gdem.jpa.service.ScriptRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scriptRules")
public class ScriptRulesApiController {

    private ScriptRulesService scriptRulesService;

    @Autowired
    public ScriptRulesApiController(ScriptRulesService scriptRulesService) {
        this.scriptRulesService = scriptRulesService;
    }

    @GetMapping("/get/{queryId}")
    public List<ScriptRulesEntry> getScriptRules(@PathVariable(name = "queryId") Integer queryId) {
        return scriptRulesService.findByQueryId(queryId);
    }

    @RequestMapping(value = "/add/{queryId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void save(@PathVariable(name = "queryId")Integer queryId, @RequestBody ScriptRulesEntry scriptRulesEntry) {
        scriptRulesEntry.setQueryId(queryId);
        scriptRulesService.save(scriptRulesEntry);
    }

    @DeleteMapping(value = "/delete/{ruleId}")
    public void delete(@PathVariable(name = "ruleId") Integer ruleId) {
        scriptRulesService.delete(ruleId);
    }
}












