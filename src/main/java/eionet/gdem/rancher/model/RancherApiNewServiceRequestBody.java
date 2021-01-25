package eionet.gdem.rancher.model;

public class RancherApiNewServiceRequestBody {

    private ServiceLaunchConfig launchConfig;
    private String name;
    private Integer scale;
    private String stackId;
    private boolean startOnCreate;

    public ServiceLaunchConfig getLaunchConfig() {
        return launchConfig;
    }

    public RancherApiNewServiceRequestBody setLaunchConfig(ServiceLaunchConfig launchConfig) {
        this.launchConfig = launchConfig;
        return this;
    }

    public String getName() {
        return name;
    }

    public RancherApiNewServiceRequestBody setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getScale() {
        return scale;
    }

    public RancherApiNewServiceRequestBody setScale(Integer scale) {
        this.scale = scale;
        return this;
    }

    public String getStackId() {
        return stackId;
    }

    public RancherApiNewServiceRequestBody setStackId(String stackId) {
        this.stackId = stackId;
        return this;
    }

    public boolean isStartOnCreate() {
        return startOnCreate;
    }

    public RancherApiNewServiceRequestBody setStartOnCreate(boolean startOnCreate) {
        this.startOnCreate = startOnCreate;
        return this;
    }
}
