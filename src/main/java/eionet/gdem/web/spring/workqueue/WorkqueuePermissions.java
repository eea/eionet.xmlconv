package eionet.gdem.web.spring.workqueue;

/**
 *
 */
public class WorkqueuePermissions {

    private boolean wqdPrm;
    private boolean wquPrm;
    private boolean wqvPrm;
    private boolean logvPrm;

    public boolean isWqdPrm() {
        return wqdPrm;
    }

    public void setWqdPrm(boolean wqdPrm) {
        this.wqdPrm = wqdPrm;
    }

    public boolean isWquPrm() {
        return wquPrm;
    }

    public void setWquPrm(boolean wquPrm) {
        this.wquPrm = wquPrm;
    }

    public boolean isWqvPrm() {
        return wqvPrm;
    }

    public void setWqvPrm(boolean wqvPrm) {
        this.wqvPrm = wqvPrm;
    }

    public boolean isLogvPrm() {
        return logvPrm;
    }

    public void setLogvPrm(boolean logvPrm) {
        this.logvPrm = logvPrm;
    }
}
