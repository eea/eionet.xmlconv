package eionet.gdem.rancher.model;

public class ServiceBody {

    public Integer scale;
    public String[] instanceIds;

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String[] getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(String[] instanceIds) {
        this.instanceIds = instanceIds;
    }
}
