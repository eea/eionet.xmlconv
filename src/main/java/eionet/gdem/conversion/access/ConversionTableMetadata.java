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

package eionet.gdem.conversion.access;

import java.util.ArrayList;
import java.util.List;

import eionet.gdem.conversion.datadict.DDTable;

/**
 * TODO: Check if this isn't used any more.
 * @author Enriko Käsper, Tieto Estonia ConversionMetadata
 * @author George Sofianos
 */

public class ConversionTableMetadata {
    private String dstIdf;
    private String dstNr;
    private String dstNsID;
    private String dstNsURL;
    private String dstSchemaURL;
    private String dstSchemaLocation;
    private String dstsNsID;
    private String dstsNsURL;
    private String tblsNamespaces;
    private List<DDTable> tbl;

    /**
     * Constructor
     */
    public ConversionTableMetadata() {
        this.dstIdf = "";
        this.dstNr = "";
        this.dstNsID = "";
        this.dstNsURL = "";
        this.dstSchemaURL = "";
        this.dstSchemaLocation = "";
        this.dstsNsID = "";
        this.dstsNsURL = "";
        this.tblsNamespaces = "";
        this.tbl = new ArrayList<DDTable>();
    }

    public List<DDTable> getTbl() {
        return tbl;
    }

    public void setTbl(List<DDTable> tbl) {
        this.tbl = tbl;
    }

    public String getDstIdf() {
        return dstIdf;
    }

    public void setDstIdf(String dstIdf) {
        this.dstIdf = dstIdf;
    }

    public String getDstNr() {
        return dstNr;
    }

    public void setDstNr(String dstNr) {
        this.dstNr = dstNr;
    }

    public String getDstNsID() {
        return dstNsID;
    }

    public void setDstNsID(String dstNsID) {
        this.dstNsID = dstNsID;
    }

    public String getDstNsURL() {
        return dstNsURL;
    }

    public void setDstNsURL(String dstNsURL) {
        this.dstNsURL = dstNsURL;
    }

    public String getDstSchemaURL() {
        return dstSchemaURL;
    }

    public void setDstSchemaURL(String dstSchemaURL) {
        this.dstSchemaURL = dstSchemaURL;
    }

    public String getDstSchemaLocation() {
        return dstSchemaLocation;
    }

    public void setDstSchemaLocation(String dstSchemaLocation) {
        this.dstSchemaLocation = dstSchemaLocation;
    }

    public String getDstsNsID() {
        return dstsNsID;
    }

    public void setDstsNsID(String dstsNsID) {
        this.dstsNsID = dstsNsID;
    }

    public String getDstsNsURL() {
        return dstsNsURL;
    }

    public void setDstsNsURL(String dstsNsURL) {
        this.dstsNsURL = dstsNsURL;
    }

    public String getTblsNamespaces() {
        return tblsNamespaces;
    }

    public void setTblsNamespaces(String tblsNamespaces) {
        this.tblsNamespaces = tblsNamespaces;
    }

    @Override
    public String toString() {
        return "ConversionTableMetadata [dstIdf=" + dstIdf + ", dstNr=" + dstNr + ", dstNsID=" + dstNsID + ", dstNsURL="
                + dstNsURL + ", dstSchemaLocation=" + dstSchemaLocation + ", dstSchemaURL=" + dstSchemaURL + ", dstsNsID="
                + dstsNsID + ", dstsNsURL=" + dstsNsURL + ", tbl=" + tbl + ", tblsNamespaces=" + tblsNamespaces + "]";
    }
}
