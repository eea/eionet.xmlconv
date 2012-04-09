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
 * The Original Code is XMLCONV - Converters and QA Services
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Zero Technologies or TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s): Enriko Käsper, TripleDev
 */
package eionet.gdem.dto;

/**
 *
 * Type for holding data about Data Dictionary dataset table.
 *
 * @author Enriko Käsper
 */
public class DDDatasetTable {

    private String tblId;
    private String shortName;
    private String dataSet;
    private String dateReleased;


    public DDDatasetTable(String tblId) {
        super();
        this.tblId = tblId;
    }
    /**
     * @return the tblId
     */
    public String getTblId() {
        return tblId;
    }
    /**
     * @param tblId the tblId to set
     */
    public void setTblId(String tblId) {
        this.tblId = tblId;
    }
    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }
    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    /**
     * @return the dataSet
     */
    public String getDataSet() {
        return dataSet;
    }
    /**
     * @param dataSet the dataSet to set
     */
    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }
    /**
     * @return the dateReleased
     */
    public String getDateReleased() {
        return dateReleased;
    }
    /**
     * @param dateReleased the dateReleased to set
     */
    public void setDateReleased(String dateReleased) {
        this.dateReleased = dateReleased;
    }



}
