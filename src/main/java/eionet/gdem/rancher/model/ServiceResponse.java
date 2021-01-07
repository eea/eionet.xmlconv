package eionet.gdem.rancher.model;

public class ServiceResponse {

    private String id;
    private String type;
    private String name;
    private String state;
    private String accountId;
    private Integer currentScale;
    private String[] instanceIds;
    private String kind;

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

    public String[] getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(String[] instanceIds) {
        this.instanceIds = instanceIds;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
