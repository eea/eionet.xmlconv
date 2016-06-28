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

package eionet.gdem.web.struts.schema;

import java.util.List;

import eionet.gdem.dto.RootElem;
import eionet.gdem.dto.Schema;

/**
 * @author Unknown
 * @author George Sofianos
 */
public class SchemaElemHolder {

    private Schema schema;
    private List<RootElem> rootElem;
    private boolean xsduPrm;
    private boolean xsddPrm;
    private boolean isSchemaIdRemoteUrl = false;

    public boolean isXsddPrm() {
        return xsddPrm;
    }

    public void setXsddPrm(boolean xsddPrm) {
        this.xsddPrm = xsddPrm;
    }

    /**
     * Default constructor
     */
    public SchemaElemHolder() {
    }

    public List<RootElem> getRootElem() {
        return rootElem;
    }

    public void setRootElem(List<RootElem> rootElem) {
        this.rootElem = rootElem;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public boolean isXsduPrm() {
        return xsduPrm;
    }

    public void setXsduPrm(boolean xsduPrm) {
        this.xsduPrm = xsduPrm;
    }

    public boolean isRootElemsPresent() {
        return rootElem != null && rootElem.size() > 0;
    }

    public void setSchemaIdRemoteUrl(boolean isSchemaIdRemoteUrl) {
        this.isSchemaIdRemoteUrl = isSchemaIdRemoteUrl;
    }

    public boolean isSchemaIdRemoteUrl() {
        return isSchemaIdRemoteUrl;
    }

}
