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
 *    Original code: Nedeljko Pavlovic (ED)
 */

package eionet.gdem.utils.xml;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

/**
 * XQuery interface.
 * @author Unknown
 * @author George Sofianos
 */
public interface IXQuery {

    /**
     * Finds element which conatins given attributes map. For this elemebnt will be searched under element with provided ID .
     * @param parentId Parent Id
     * @param attributes Attributes
     * @throws XmlException If an error occurs.
     */
    Node findElementByAttrs(String parentId, Map<String, String> attributes) throws XmlException;

    /**
     * Gets attribute value.
     * @param parentId Parent Id
     * @param attribute Attribute
     * @return Attribute value
     * @throws XmlException If an error occurs.
     */
    String getAttributeValue(String parentId, String attribute) throws XmlException;

    /**
     * Gets value of element which parent is specified by ID.
     *
     * @param parentId
     *            Identifier of the parent element.
     * @param name
     *            Name of the element we are searching for
     * @return String representing element value
     * @throws XmlException
     *             Thrown in case of system erros
     */
    String getElementValue(String parentId, String name) throws XmlException;

    /**
     * Finds element by Id in a DOM Document
     * @param id Id
     * @throws XmlException If an error occurs.
     */
    Node findElementById(String id) throws XmlException;

    /**
     * Retruns list of all identifiers for the specifed element
     *
     * @param elementName Element name
     * @return Element Identifiers list
     * @throws XmlException If an error occurs.
     */
    List<String> getElementIdentifiers(String elementName) throws XmlException;

    /**
     * Retruns list of all elements attributes for the specifed element name
     *
     * @param elementName Element name
     * @return Elements list
     * @throws XmlException If an error occurs.
     */
    List<Map<String, String>> getElements(String elementName) throws XmlException;

    /**
     * returns the list of element values
     *
     * @param elementName Element name
     * @return Element values
     * @throws XmlException If an error occurs.
     */
    List<String> getElementValues(String elementName) throws XmlException;

    /**
     * returns the list XML Schema element names from xs:element name attribute
     *
     * @return Schema elements
     * @throws XmlException If an error occurs.
     */
    List<String> getSchemaElements() throws XmlException;

    /**
     * returns the xs:restriction/@base for gievn element from XML Schema
     *
     * @return Element Type
     * @throws XmlException If an error occurs.
     */
    String getSchemaElementType(String elementName) throws XmlException;

    /**
     * returns the list XML Schema imports from xs:import schemaLocation attribute
     *
     * @return Schema imports
     * @throws XmlException If an error occurs.
     */
    List<String> getSchemaImports() throws XmlException;

    /**
     * returns the list XML Schema elements that may have multiple values
     *
     * @return XML schema elements with multiple values
     * @throws XmlException If an error occurs.
     */
    Map<String, String> getSchemaElementWithMultipleValues() throws XmlException;

}
