package eionet.gdem.api.serverstatus.web.service;

import eionet.gdem.GDEMException;
import java.util.HashMap;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
public interface ServerStatusService {
    ServerStatusObject getServerStatus() throws GDEMException;
}
