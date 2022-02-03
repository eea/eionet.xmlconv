package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.ScriptRulesEntry;
import eionet.gdem.jpa.repositories.ScriptRulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("scriptRulesServiceImpl")
public class ScriptRulesServiceImpl implements ScriptRulesService {

    private ScriptRulesRepository scriptRulesRepository;

    @Autowired
    public ScriptRulesServiceImpl(ScriptRulesRepository scriptRulesRepository) {
        this.scriptRulesRepository = scriptRulesRepository;
    }


    @Override
    public List<ScriptRulesEntry> findByQueryId(Integer queryId) {
        return scriptRulesRepository.findByQueryId(queryId);
    }

    @Override
    public ScriptRulesEntry save(ScriptRulesEntry entry) {
        if (entry.getValue().contains(",")) {
            String value = entry.getValue();
            entry.setValue(value.replace(",","."));
        }
        return scriptRulesRepository.save(entry);
    }

    @Override
    public void delete(Integer ruleId) {
        scriptRulesRepository.delete(ruleId);
    }
}
