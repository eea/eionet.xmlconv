package eionet.gdem.web.spring.config;

/**
 *
 */
public class LdapForm {
    private String url;
    private String context;
    private String userDir;
    private String attrUid;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUserDir() {
        return userDir;
    }

    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }

    public String getAttrUid() {
        return attrUid;
    }

    public void setAttrUid(String attrUid) {
        this.attrUid = attrUid;
    }
}
