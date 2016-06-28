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

package eionet.gdem.web.struts.stylesheet;

import java.util.List;

import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;

/**
 * Wrapper object holding stylesheet related information displayed on web forms.
 *
 * @author Enriko Käsper
 */
public class StylesheetListHolder {

    /** List of Stylesheet objects. */
    private List<Stylesheet> stylesheetList;
    /** List of Schema and related Stylesheet objects. */
    private List<Schema> handCodedStylesheets;
    /** List of DD Schemas and related generated stylesheets. */
    private List<Schema> ddStylesheets;
    boolean ssiPrm;
    boolean ssdPrm;
    boolean convPrm;
    boolean handcoded;

    /**
     * Default constructor
     */
    public StylesheetListHolder() {
    }

    public List<Schema> getDdStylesheets() {
        return ddStylesheets;
    }

    public void setDdStylesheets(List<Schema> ddStylesheets) {
        this.ddStylesheets = ddStylesheets;
    }

    public List<Schema> getHandCodedStylesheets() {
        return handCodedStylesheets;
    }

    public void setHandCodedStylesheets(List<Schema> handCodedStylesheets) {
        this.handCodedStylesheets = handCodedStylesheets;
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

    public boolean isConvPrm() {
        return convPrm;
    }

    public void setConvPrm(boolean convPrm) {
        this.convPrm = convPrm;
    }

    public boolean isHandcoded() {
        return handcoded;
    }

    public void setHandcoded(boolean handcoded) {
        this.handcoded = handcoded;
    }

    /**
     * @return the stylesheetList
     */
    public List<Stylesheet> getStylesheetList() {
        return stylesheetList;
    }

    /**
     * @param stylesheetList the stylesheetList to set
     */
    public void setStylesheetList(List<Stylesheet> stylesheetList) {
        this.stylesheetList = stylesheetList;
    }

}
