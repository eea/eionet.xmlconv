package eionet.gdem.jpa.repositories;

import eionet.gdem.jpa.Entities.InternalSchedulingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("intSchedulingStatusRepository")
public interface IntSchedulingStatusRepository extends JpaRepository<InternalSchedulingStatus, Integer> {

}
