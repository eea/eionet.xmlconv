/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):* Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.web.struts.qascript;

import java.util.List;

import eionet.gdem.dto.Schema;

public class QAScriptListHolder {
    /**
     * @author Enriko Käsper, Tieto Estonia QAScriptListHolder
     */

    private List<Schema> qascripts;
    boolean ssiPrm;
    boolean ssdPrm;
    boolean wqiPrm;
    boolean wquPrm;
    boolean qsiPrm;
    boolean qsuPrm;

    public boolean isQsuPrm() {
        return qsuPrm;
    }

    public void setQsuPrm(boolean qsuPrm) {
        this.qsuPrm = qsuPrm;
    }

    public boolean isQsiPrm() {
        return qsiPrm;
    }

    public void setQsiPrm(boolean qsiPrm) {
        this.qsiPrm = qsiPrm;
    }

    public QAScriptListHolder() {
    }

    public List<Schema> getQascripts() {
        return qascripts;
    }

    public void setQascripts(List<Schema> qascripts) {
        this.qascripts = qascripts;
    }

    public boolean isSsiPrm() {
        return ssiPrm;
    }

    public void setSsiPrm(boolean ssiPrm) {
        this.ssiPrm = ssiPrm;
    }

    public boolean isSsdPrm() {
        return ssdPrm;
    }

    public void setSsdPrm(boolean ssdPrm) {
        this.ssdPrm = ssdPrm;
    }

    public boolean isWqiPrm() {
        return wqiPrm;
    }

    public void setWqiPrm(boolean wqiPrm) {
        this.wqiPrm = wqiPrm;
    }

    public boolean isWquPrm() {
        return wquPrm;
    }

    public void setWquPrm(boolean wquPrm) {
        this.wquPrm = wquPrm;
    }
}
