package eionet.gdem.web.spring.scripts;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 */
public class QAScriptSyncForm {
    private static final long serialVersionUID = 1L;

    private String scriptId;
    private String url;
    private String scriptFile;
    private String fileName;

    /*@Override
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        return null;
    }*/

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String id) {
        this.scriptId = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

/*    @Override
    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        scriptId = null;
        url = null;
        scriptFile = null;
        fileName = null;
    }*/
}
