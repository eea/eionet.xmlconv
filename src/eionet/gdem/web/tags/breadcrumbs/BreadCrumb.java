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
 *    Original code: Dusan Popovic (ED)
 */

package eionet.gdem.web.tags.breadcrumbs;

public class BreadCrumb {
    private String url;

    private String label;

    public BreadCrumb(String url, String label) {
        this.url = url;
        this.label = label;
    }

    public String getUrl() {
        return this.url;
    }

    public String getLabel() {
        return this.label;
    }

    public int hashCode() {
        return this.url.hashCode() & this.label.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BreadCrumb)) {
            return false;
        }

        BreadCrumb b = (BreadCrumb) obj;

        // this is all to make the equality of a url not include the query
        // string
        // Could just use java.net.URL
        // TODO
        String u1 = this.url;
        String u2 = b.url;
        int idx1 = u1.indexOf("?");
        if (idx1 != -1) {
            u1 = u1.substring(0, idx1);
        }
        int idx2 = u2.indexOf("?");
        if (idx2 != -1) {
            u2 = u2.substring(0, idx2);
        }

        return u1.equals(u2) && this.label.equals(b.label);
    }

    public String toString() {
        return "[" + this.label + "|" + this.url + "]";
    }

}
