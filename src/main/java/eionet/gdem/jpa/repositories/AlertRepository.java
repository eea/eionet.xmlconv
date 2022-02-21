package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.AlertEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntry, Integer> {
}
