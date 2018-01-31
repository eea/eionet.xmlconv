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

package eionet.gdem.web.spring.stylesheet;

import eionet.gdem.web.spring.conversions.ConvType;

import java.util.List;

public class ConvTypeHolder {

    private List<ConvType> convTypes;

    public ConvTypeHolder() {
    }

    public List<ConvType> getConvTypes() {
        return convTypes;
    }

    public void setConvTypes(List convTypes) {
        this.convTypes = convTypes;
    }

}
