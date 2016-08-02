package eionet.gdem.api.serverstatus.web.service.Impl;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.api.serverstatus.web.service.ServerStatusObject;
import eionet.gdem.api.serverstatus.web.service.ServerStatusService;
import eionet.gdem.services.db.dao.IXQJobDao;
import eionet.gdem.services.impl.QueueJobsServiceImpl;
import static java.lang.Integer.parseInt;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
@Service
public class ServerStatusServiceImpl implements ServerStatusService {
    
    private IXQJobDao ixqJobDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueJobsServiceImpl.class);

    @Autowired
    public ServerStatusServiceImpl(@Qualifier("xqJobDao") IXQJobDao ixqJobDao) {
        this.ixqJobDao = ixqJobDao;
    }    
    @Override
    public ServerStatusObject getServerStatus() throws GDEMException {
        int isRancher = Properties.getIsRancher();
        if (isRancher == 1 ) {
            
        }
        else {
            
        }
        return getWorkqueueInfo();
        
    }
    
    private ServerStatusObject getWorkqueueInfo () {
        try {
            String [] [] queryResults = ixqJobDao.getJobsSumInstanceAndStatus();
            
            //--------------
            
            ServerStatusObject res = new ServerStatusObject () ;
            for ( int i = 0 ; i < queryResults.length ; i ++ ) {
                res.insertJobStatusByInstance(queryResults [i][0], queryResults [i][1], parseInt ( queryResults [i][2]) );
            }
            //-------------
            
            return res;

        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(ServerStatusServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
}
