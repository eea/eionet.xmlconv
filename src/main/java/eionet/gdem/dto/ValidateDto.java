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

import eionet.gdem.validation.ValidatorErrorType;

/**
 * Validate data transfer object.
 */
public class ValidateDto implements Serializable {

    private ValidatorErrorType type;
    private int line;
    private int column;
    private String description;

    /**
     * Default constructor
     */
    public ValidateDto() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ValidatorErrorType getType() {
        return type;
    }

    public void setType(ValidatorErrorType type) {
        this.type = type;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return type + ": line " + line + ", col " + column + " - " + description;
    }
}
