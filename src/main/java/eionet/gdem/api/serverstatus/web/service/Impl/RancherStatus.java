package eionet.gdem.api.serverstatus.web.service.Impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class RancherStatus {
    
    private String health_state;
    private String name;
    private String state;
    
    public RancherStatus() {
    }
    
    public String getName() {
        return name;
    }

    public String getHealth_state() {
        return health_state;
    }
    
    public String getState() {
        return state;
    }
}
