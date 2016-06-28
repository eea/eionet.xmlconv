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
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Istvan Alfeldi (ED)
 */

package eionet.gdem.dto;

import java.io.Serializable;

/**
 * Conversion data transfer object.
 */
public class ConversionDto implements Serializable {
    private String convId;
    private String description;
    private String resultType;
    private String stylesheet;
    private String contentType;
    private String xmlSchema;
    private boolean ignoreGeneratedIfManualExists = false;


    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the xmlSchema
     */
    public String getXmlSchema() {
        return xmlSchema;
    }

    /**
     * @param xmlSchema
     *            the xmlSchema to set
     */
    public void setXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    /**
     * @return the ignoreGeneratedIfManualExists
     */
    public boolean isIgnoreGeneratedIfManualExists() {
        return ignoreGeneratedIfManualExists;
    }

    /**
     * @param ignoreGeneratedIfManualExists
     *            the ignoreGeneratedIfManualExists to set
     */
    public void setIgnoreGeneratedIfManualExists(boolean ignoreGeneratedIfManualExists) {
        this.ignoreGeneratedIfManualExists = ignoreGeneratedIfManualExists;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConversionDto [convId=" + convId + ", description=" + description + ", resultType=" + resultType + ", stylesheet="
        + stylesheet + ", contentType=" + contentType + ", xmlSchema=" + xmlSchema + ", ignoreGeneratedIfManualExists="
        + ignoreGeneratedIfManualExists + "]";
    }

}
