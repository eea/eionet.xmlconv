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

package eionet.gdem.dto;

import java.io.Serializable;

/**
 * @author Enriko Käsper, Tieto Estonia CrFileDto
 */

public class CrFileDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String url;

    private String lastModified;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Gets label
     * @return Label
     */
    public String getLabel() {
        StringBuilder label = new StringBuilder(getUrl());
        if (getLastModified() != null && getLastModified().length() > 0) {
            label.append(" - (modified: ");
            label.append(getLastModified());
            label.append(")");
        }

        return label.toString();
    }
}
