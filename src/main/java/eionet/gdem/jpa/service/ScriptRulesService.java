package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.ScriptRulesEntry;

import java.util.List;

public interface ScriptRulesService {

    List<ScriptRulesEntry> findByQueryId(Integer queryId);

    ScriptRulesEntry save(ScriptRulesEntry entry);

    void delete(Integer ruleId);
}
