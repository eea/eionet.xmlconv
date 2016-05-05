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
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.conversion.datadict;

/**
 * DD element.
 * @author Enriko Käsper, Tieto Estonia DDElement
 * @author George Sofianos
 */

public class DDElement {
    private String elmIdf;
    private String schemaDataType;
    private String delimiter;
    private boolean hasMultipleValues = false;

    /**
     * DD element constructor
     * @param elmIdf Element id
     */
    public DDElement(String elmIdf) {
        this.elmIdf = elmIdf;
        this.schemaDataType = "xs:string";
        this.delimiter = "";
    }

    public String getSchemaDataType() {
        return schemaDataType;
    }

    public void setSchemaDataType(String dataType) {
        this.schemaDataType = dataType;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public boolean isHasMultipleValues() {
        return hasMultipleValues;
    }

    public void setHasMultipleValues(boolean hasMultipleValues) {
        this.hasMultipleValues = hasMultipleValues;
    }

    public String getElmIdf() {
        return elmIdf;
    }

    public void setElmIdf(String elmIdf) {
        this.elmIdf = elmIdf;
    }

    /**
     * DD element equals.
     * @param ddElm DD element
     * @return True if this element is equal with ddElm
     */
    public boolean equals(DDElement ddElm) {
        if (getElmIdf() != null && ddElm != null && ddElm.getElmIdf() != null) {
            return getElmIdf().equalsIgnoreCase(ddElm.getElmIdf());
        }
        return false;
    }

    @Override
    public String toString() {
        return "DDElement [elmIdf=" + elmIdf + "]";
    }

}
