package eionet.gdem.jpa.utils;

import eionet.gdem.Properties;

public enum JobExecutorType {

    Light(0, Properties.RANCHER_LIGTH_JOBEXEC_DEPLOYMENT_NAME),
    Heavy(1, Properties.RANCHER_HEAVY_JOBEXEC_DEPLOYMENT_NAME),
    Unknown(2, ""),
    Sync_fme(3, Properties.RANCHER_SYNC_FME_JOBEXEC_DEPLOYMENT_NAME),
    Async_fme(4, Properties.RANCHER_ASYNC_FME_JOBEXEC_DEPLOYMENT_NAME);

    private Integer id;
    private String deploymentName;

    JobExecutorType(Integer id, String deploymentName) {
        this.id = id;
        this.deploymentName = deploymentName;
    }

    public Integer getId() {
        return id;
    }

    public String getDeploymentName() {
        return deploymentName;
    }
}
