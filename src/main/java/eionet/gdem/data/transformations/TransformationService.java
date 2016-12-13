package eionet.gdem.data.transformations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service
@Transactional
public class TransformationService {

    private TransformationDao dao;

    @Autowired
    TransformationService(TransformationDao dao) {
        this.dao = dao;
    }

    public List<Transformation> getAllTransformations() {
        return dao.getAllTransformations();
    }
}
