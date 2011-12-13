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

public class Stylesheet implements Serializable {

    private String xsl;
    private String type;
    private String xsl_descr;
    private String convId;
    private String modified;
    private boolean ddConv;
    private String schema;
    private String xslContent;
    private String xslFileName;
    private String checksum;
    private String dependsOn;

    /**
     * @return the dependsOn
     */
    public String getDependsOn() {
        return dependsOn;
    }

    /**
     * @param dependsOn
     *            the dependsOn to set
     */
    public void setDependsOn(String dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXsl() {
        return xsl;
    }

    public void setXsl(String xsl) {
        this.xsl = xsl;
    }

    public String getXsl_descr() {
        return xsl_descr;
    }

    public void setXsl_descr(String xsl_descr) {
        this.xsl_descr = xsl_descr;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public boolean isDdConv() {
        return ddConv;
    }

    public void setDdConv(boolean ddConv) {
        this.ddConv = ddConv;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getXslContent() {
        return xslContent;
    }

    public void setXslContent(String content) {
        this.xslContent = content;
    }

    public String getXslFileName() {
        return xslFileName;
    }

    public void setXslFileName(String xslFileName) {
        this.xslFileName = xslFileName;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

}
