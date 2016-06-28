/*
 * The contents of this file are subject to the Mozilla private String
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
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.conversion.datadict;

import java.util.List;

/**
 * @author Enriko Käsper, Tieto Estonia DDTable
 * @author George Sofianos
 */

public class DDTable {
    private String tblIdf;
    private String tblNr;
    private String tblNsID;
    private String tblNsURL;
    private String tblSchemaURL;
    private List<DDElement> elms;

    /**
     * Default constructor.
     */
    public DDTable() {
    }

    /**
     * Constructor
     * @param tblIdf2 Table id
     */
    public DDTable(String tblIdf2) {
        this.tblIdf = tblIdf2;
    }

    public String getTblIdf() {
        return tblIdf;
    }

    public void setTblIdf(String tblIdf) {
        this.tblIdf = tblIdf;
    }

    public String getTblNr() {
        return tblNr;
    }

    public void setTblNr(String tblNr) {
        this.tblNr = tblNr;
    }

    public String getTblNsID() {
        return tblNsID;
    }

    public void setTblNsID(String tblNsID) {
        this.tblNsID = tblNsID;
    }

    public String getTblNsURL() {
        return tblNsURL;
    }

    public void setTblNsURL(String tblNsURL) {
        this.tblNsURL = tblNsURL;
    }

    public String getTblSchemaURL() {
        return tblSchemaURL;
    }

    public void setTblSchemaURL(String tblSchemaURL) {
        this.tblSchemaURL = tblSchemaURL;
    }

    public List<DDElement> getElms() {
        return elms;
    }

    public void setElms(List<DDElement> elms) {
        this.elms = elms;
    }

    @Override
    public String toString() {
        return "DDTable [elms=" + elms + ", tblIdf=" + tblIdf + ", tblNr=" + tblNr + ", tblNsID=" + tblNsID + ", tblNsURL="
                + tblNsURL + ", tblSchemaURL=" + tblSchemaURL + "]";
    }
}
