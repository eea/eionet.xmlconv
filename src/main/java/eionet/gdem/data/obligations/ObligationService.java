package eionet.gdem.data.obligations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 *
 */
@Service
@Transactional
public class ObligationService {

    private ObligationDao dao;

    @Autowired
    public ObligationService(ObligationDao dao) {
        this.dao = dao;
    }

    public List<Obligation> findAll() {
        return dao.findAll();
    }

    public void delete(Integer id) {
        Obligation o = dao.findById(id);
        dao.delete(o);
    }

    public Obligation insert(Obligation o) {
        return dao.insert(o);
    }

    public Obligation update(Obligation o) {
        return dao.update(o);
    }

    public void deleteList(List<Obligation> obligations) {
        for (Obligation obligation : obligations) {
            dao.delete(obligation);
        }
    }

    public Obligation findById(Integer id) { return dao.findById(id); }

}
