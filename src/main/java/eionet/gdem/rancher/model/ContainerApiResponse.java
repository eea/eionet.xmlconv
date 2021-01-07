package eionet.gdem.rancher.model;

public class ContainerApiResponse {

    private String resourceType;
    private ContainerData[] data;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public ContainerData[] getData() {
        return data;
    }

    public void setData(ContainerData[] data) {
        this.data = data;
    }
}
