/**
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
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem.validation;

/**
 * Unparsed Entity.
 * @author Unknown
 * @author George Sofianos
 */
public class UnparsedEntity {

    private String name;
    private String publicID;
    private String systemID;
    private String notationName;

    /**
     * Constructor
     * @param name Name
     * @param publicID Public Id
     * @param systemID System Id
     * @param notationName Notation name
     */
    public UnparsedEntity(String name, String publicID, String systemID, String notationName) {

        this.name = name;
        this.publicID = publicID;
        this.systemID = systemID;
        this.notationName = notationName;

    }

    public String getName() {
        return this.name;
    }

    public String getSystemID() {
        return this.systemID;
    }

    public String getPublicID() {
        return this.publicID;
    }

    public String getNotationName() {
        return this.notationName;
    }

    /**
     * Equals Method
     * @param o Object
     * @return True if objects are equals
     */
    public boolean equals(Object o) {

        if (o instanceof UnparsedEntity) {
            UnparsedEntity entity = (UnparsedEntity) o;
            if (publicID == null) {
                return name.equals(entity.name) && systemID.equals(entity.systemID) && notationName.equals(entity.notationName);
            } else {
                return name.equals(entity.name) && systemID.equals(entity.systemID) && publicID.equals(entity.publicID)
                        && notationName.equals(entity.notationName);
            }
        }
        return false;

    }

    /**
     * Gets hashcode
     * @return Hash code
     */
    public int hashCode() {

        if (publicID == null) {
            return name.hashCode() ^ systemID.hashCode() ^ notationName.hashCode();
        } else {
            return name.hashCode() ^ publicID.hashCode() ^ systemID.hashCode() ^ notationName.hashCode();
        }

    }

    /**
     * Returns toString
     * @return Entity toString
     */
    public String toString() {

        StringBuffer result = new StringBuffer(name);
        if (publicID == null) {
            result.append(" PUBLIC ");
            result.append(publicID);
        } else {
            result.append(" SYSTEM ");
        }
        result.append(" ");
        result.append(systemID);

        return result.toString();
    }

}
