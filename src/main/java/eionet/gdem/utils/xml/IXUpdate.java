/*
 * The contents of this file are subject to the Mozilla
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
 *    Original code: Nedeljko Pavlovic (ED)
 */

package eionet.gdem.utils.xml;

/**
 * XUpdate interface.
 * @author Unknown
 * @author George Sofianos
 */
public interface IXUpdate {
    /**
     * Inserts element
     * @param parentElementName Parent element name
     * @param elementName element name
     * @throws XmlException If an error occurs.
     */
    void insertElement(String parentElementName, String elementName) throws XmlException;

    /**
     * Inserts Attribute
     * @param parentElementName Parent element name
     * @param attributeName Attribute name
     * @param attributeValue Attribute value
     * @throws XmlException If an error occurs.
     */
    void insertAttribute(String parentElementName, String attributeName, String attributeValue) throws XmlException;

    /**
     * Update text value of the existing XML element.
     * @param parentId Parent Id
     * @param name Name
     * @param newValue New Value
     * @throws XmlException If an error occurs.
     */
    void updateElement(String parentId, String name, String newValue) throws XmlException;

    /**
     * Deletes element
     * @param parentId Parent Id
     * @param name Name
     * @throws XmlException If an error occurs.
     */
    void deleteElement(String parentId, String name) throws XmlException;

}
