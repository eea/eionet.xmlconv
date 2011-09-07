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
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA).
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 * Created on 27.06.2006
 */
package eionet.gdem.dto;

import java.io.Serializable;

public class CdrFileDto implements Serializable {

    private String url;
    private String country;
    private String partofyear;
    private int endyear;
    private int year;
    private String title;
    private String iso;

    public CdrFileDto() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPartofyear() {
        return partofyear;
    }

    public void setPartofyear(String partofyear) {
        this.partofyear = partofyear;
    }

    public int getEndyear() {
        return endyear;
    }

    public void setEndyear(int endyear) {
        this.endyear = endyear;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getLabel() {
        StringBuilder label = new StringBuilder(country);
        label.append(" - ");
        label.append(title);
        if (getYear() > 0) {
            label.append(" - (");
            label.append(year);
            if (getEndyear() > 0) {
                label.append(" - ");
                label.append(getEndyear());
            }
            if (getEndyear() == 0) {
                label.append(" - ");
                label.append(getPartofyear());
            }
            label.append(")");
        }
        return label.toString();
    }
}
