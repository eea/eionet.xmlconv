package eionet.gdem.rancher.model;

public class ServiceApiRequestBody {

    public Integer scale;
    public String[] instanceIds;

    public Integer getScale() {
        return scale;
    }

    public ServiceApiRequestBody setScale(Integer scale) {
        this.scale = scale;
        return this;
    }

    public String[] getInstanceIds() {
        return instanceIds;
    }

    public ServiceApiRequestBody setInstanceIds(String[] instanceIds) {
        this.instanceIds = instanceIds;
        return this;
    }
}
