package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.ScriptRulesEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScriptRulesRepository extends JpaRepository<ScriptRulesEntry, Integer> {

    List<ScriptRulesEntry> findByQueryId(Integer queryId);
}
