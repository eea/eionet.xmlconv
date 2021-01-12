package eionet.gdem.rancher.model;

import java.util.List;

public class ServiceApiRequestBody {

    public Integer scale;
    public List<String> instanceIds;

    public Integer getScale() {
        return scale;
    }

    public ServiceApiRequestBody setScale(Integer scale) {
        this.scale = scale;
        return this;
    }

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public ServiceApiRequestBody setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
        return this;
    }
}
