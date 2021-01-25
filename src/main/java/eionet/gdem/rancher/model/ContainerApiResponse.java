package eionet.gdem.rancher.model;

import java.util.List;

public class ContainerApiResponse {

    private String resourceType;
    private List<ContainerData> data;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public List<ContainerData> getData() {
        return data;
    }

    public void setData(List<ContainerData> data) {
        this.data = data;
    }
}
