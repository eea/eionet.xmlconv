package eionet.gdem.rancher.model;

public class RancherServiceRequestBody {

    private ServiceLaunchConfig launchConfig;
    private String name;
    private Integer scale;
    private String stackId;
    private boolean startOnCreate;

    public ServiceLaunchConfig getLaunchConfig() {
        return launchConfig;
    }

    public RancherServiceRequestBody setLaunchConfig(ServiceLaunchConfig launchConfig) {
        this.launchConfig = launchConfig;
        return this;
    }

    public String getName() {
        return name;
    }

    public RancherServiceRequestBody setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getScale() {
        return scale;
    }

    public RancherServiceRequestBody setScale(Integer scale) {
        this.scale = scale;
        return this;
    }

    public String getStackId() {
        return stackId;
    }

    public RancherServiceRequestBody setStackId(String stackId) {
        this.stackId = stackId;
        return this;
    }

    public boolean isStartOnCreate() {
        return startOnCreate;
    }

    public RancherServiceRequestBody setStartOnCreate(boolean startOnCreate) {
        this.startOnCreate = startOnCreate;
        return this;
    }
}
