package eionet.gdem.api.serverstatus.web.service.Impl;

import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.serverstatus.web.service.ServerStatusObject;
import eionet.gdem.api.serverstatus.web.service.ServerStatusService;
import eionet.gdem.rancher.service.RancherApiService;
import eionet.gdem.services.impl.QueueJobsServiceImpl;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
@Service
public class ServerStatusServiceImpl implements ServerStatusService {
    
    private IXQJobDao ixqJobDao;
    private final RancherApiService rancherApiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueJobsServiceImpl.class);

    private static class isRancher {
        public static final int IS_RANCHER = Properties.getIsRancher();
    }

    @Autowired
    public ServerStatusServiceImpl(@Qualifier("xqJobDao") IXQJobDao ixqJobDao, RancherApiService rancherApiService) {
        this.ixqJobDao = ixqJobDao;
        this.rancherApiService = rancherApiService;
    }

    @Override
    public ServerStatusObject getServerStatus() throws XMLConvException {
        ServerStatusObject res = new ServerStatusObject() ;
        if (isRancher.IS_RANCHER == 1) {
            getRancherInfo(res);
        }
        return getWorkqueueInfo(res);
        
    }
    
    private ServerStatusObject getWorkqueueInfo(ServerStatusObject res) {
        try {
            String[][] queryResults = ixqJobDao.getJobsSumInstanceAndStatus();
            if (isNull(queryResults)) {
                return (isRancher.IS_RANCHER == 1) ? res : null;
            }
            for (int i = 0; i < queryResults.length; i ++) {
                res.insertJobStatusByInstance(queryResults[i][0], queryResults[i][1], parseInt(queryResults[i][2]));
            }
            return res;

        } catch (SQLException ex) {
            LOGGER.error( "getWorkqueueInfo: ", ex);
            return null;
        }
    }
    
    private void getRancherInfo(ServerStatusObject res) {
        getWorkerInfo(Properties.RANCHER_LIGTH_JOBEXEC_DEPLOYMENT_NAME, res);
        getWorkerInfo(Properties.RANCHER_HEAVY_JOBEXEC_DEPLOYMENT_NAME, res);
        getWorkerInfo(Properties.RANCHER_SYNC_FME_JOBEXEC_DEPLOYMENT_NAME, res);
        getWorkerInfo(Properties.RANCHER_ASYNC_FME_JOBEXEC_DEPLOYMENT_NAME, res);
    }

    private void getWorkerInfo(String deploymentName, ServerStatusObject res) {
        rancherApiService.getPods(deploymentName).forEach(
                pod -> res.insertHealthStatusByInstance(
                        pod.getMetadata().getName(),
                        (pod.getStatus() != null) ? pod.getStatus().getPhase() : "Unknown")
        );
    }
}
