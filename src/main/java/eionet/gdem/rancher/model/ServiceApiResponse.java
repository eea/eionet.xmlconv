package eionet.gdem.rancher.model;

import java.util.List;

public class ServiceApiResponse {

    private String id;
    private String type;
    private String name;
    private String state;
    private String accountId;
    private Integer currentScale;
    private String healthState;
    private List<String> instanceIds;
    private String kind;
    private Integer scale;
    private ServiceLaunchConfig launchConfig;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Integer getCurrentScale() {
        return currentScale;
    }

    public void setCurrentScale(Integer currentScale) {
        this.currentScale = currentScale;
    }

    public String getHealthState() {
        return healthState;
    }

    public void setHealthState(String healthState) {
        this.healthState = healthState;
    }

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public ServiceLaunchConfig getLaunchConfig() {
        return launchConfig;
    }

    public void setLaunchConfig(ServiceLaunchConfig launchConfig) {
        this.launchConfig = launchConfig;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }
}
