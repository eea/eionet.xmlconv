/*
 * Created on 20.11.2007
 */
package eionet.gdem.web.spring.xmlfile;

import java.util.List;

/**
 * Class for holding the data for list of XML files
 * 
 * @author Enriko Käsper (TietoEnator)
 * 
 */

public class UplXmlFileHolder {

    private List xmlfiles;
    boolean ssiPrm;
    boolean ssdPrm;
    boolean ssuPrm;
    private String xmlfileFolder;

    public UplXmlFileHolder() {
    }

    public List getXmlfiles() {
        return xmlfiles;
    }

    public void setXmlfiles(List xmlfiles) {
        this.xmlfiles = xmlfiles;
    }

    public boolean isSsdPrm() {
        return ssdPrm;
    }

    public void setSsdPrm(boolean ssdPrm) {
        this.ssdPrm = ssdPrm;
    }

    public boolean isSsiPrm() {
        return ssiPrm;
    }

    public void setSsiPrm(boolean ssiPrm) {
        this.ssiPrm = ssiPrm;
    }

    public boolean isSsuPrm() {
        return ssiPrm;
    }

    public void setSsuPrm(boolean ssuPrm) {
        this.ssuPrm = ssuPrm;
    }

    public String getXmlfileFolder() {
        return xmlfileFolder;
    }

    public void setXmlfileFolder(String xmlfileFolder) {
        this.xmlfileFolder = xmlfileFolder;
    }

}
