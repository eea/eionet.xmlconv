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

import eionet.gdem.dto.Schema;

/**
 *
 * Holds the list of Schemas and user permissions displayed on Schemas page.
 *
 * @author Enriko Käsper
 */
public class UplSchemaHolder {

    /** Holds the list of Schema objects. */
    private List<Schema> schemas;
    /** Has insert permission. */
    boolean ssiPrm;
    /** Has delete permission. */
    boolean ssdPrm;
    /** Has update permission. */
    boolean ssuPrm;

    /**
     * Default constructor.
     */
    public UplSchemaHolder() {
    }

    public List<Schema> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<Schema> schemas) {
        this.schemas = schemas;
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

}
