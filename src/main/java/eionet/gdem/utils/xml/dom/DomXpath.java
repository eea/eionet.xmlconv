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

package eionet.gdem.utils.xml.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XPathQuery;
import eionet.gdem.utils.xml.XmlException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML Query class.
 * TODO: Check XPATH performance vs Saxon XPATH.
 * @author Unknown
 * @author George Sofianos
 */
public class DomXpath implements XPathQuery {

    private IXmlCtx ctx = null;

    /**
     * Constructor
     * @param ctx Context
     */
    public DomXpath(IXmlCtx ctx) {
        this.ctx = ctx;
    }

    /**
     * Finds all elements by attribute
     * @param parentId Parent Id
     * @param attributes Attributes
     * @return Node
     * @throws XmlException If an error occurs.
     */
    public Node findElementByAttrs(String parentId, Map<String, String> attributes) throws XmlException {
        String xpath = "//*[@id='" + parentId + "']/*[";
        Iterator<String> attrs = attributes.keySet().iterator();
        int i = 0;
        while (attrs.hasNext()) {
            String key = attrs.next();
            if (i == 0) {
                xpath += "@" + key + "='" + attributes.get(key) + "' ";
            } else {
                xpath += "and @" + key + "='" + attributes.get(key) + "' ";
            }
            ++i;
        }
        xpath += "]";
        // System.out.println(xpath);
        Node result = null;
        try {
            result = XPathAPI.selectSingleNode(ctx.getDocument(), xpath);
        } catch (TransformerException e) {
            throw new XmlException(e);
        }
        return result;
    }

    /**
     * Returns attribute value
     * @param elementId Element id
     * @param attribute Attribute
     * @return Attribute value
     * @throws XmlException If an error occurs.
     */
    public String getAttributeValue(String elementId, String attribute) throws XmlException {
        String xpath = "//*[@id='" + elementId + "']/@" + attribute;
        Attr el = null;
        String result = null;
        try {
            el = (Attr) XPathAPI.selectSingleNode(ctx.getDocument(), xpath);
            if (el != null) {
                result = el.getValue();
            }
        } catch (TransformerException e) {
            throw new XmlException(e);
        }
        return result;
    }

    /**
     * Returns element value
     * @param parentId
     *            Identifier of the parent element.
     * @param name
     *            Name of the element we are searching for
     * @return
     * @throws XmlException If an error occurs.
     */
    public String getElementValue(String parentId, String name) throws XmlException {
        String value = null;
        try {
            String xpath = "//*[@id='" + parentId + "']/" + name + "/text()";
            Node textNode = XPathAPI.selectSingleNode(ctx.getDocument(), xpath);
            if (textNode != null) {
                value = textNode.getNodeValue().trim();
                if (value.equalsIgnoreCase(""))
                    value = null;
            }
        } catch (TransformerException e) {
            throw new XmlException(e);
        }
        return value;
    }

    /**
     * Finds element by Id
     * @param id Id
     * @return Element
     * @throws XmlException If an error occurs.
     */
    public Node findElementById(String id) throws XmlException {
        String xpath = "//*[@id='" + id + "']";
        Node result = null;
        try {
            result = XPathAPI.selectSingleNode(ctx.getDocument(), xpath);
        } catch (TransformerException e) {
            throw new XmlException(e);
        }
        return result;
    }

    /**
     * Returns element identifiers
     * @param elementName Element name
     * @return Element identifiers list
     * @throws XmlException If an error occurs.
     */
    public List<String> getElementIdentifiers(String elementName) throws XmlException {
        String xpath = "//" + elementName;
        List<String> result = new ArrayList<String>();
        try {
            NodeList nodes = XPathAPI.selectNodeList(ctx.getDocument(), xpath);
            for (int i = 0; i < nodes.getLength(); i++) {
                String id = nodes.item(i).getAttributes().getNamedItem("id").getNodeValue();
                if (id != null)
                    result.add(id);
            }
        } catch (Exception e) {
            throw new XmlException(e);
        }
        return result;
    }

    /**
     * Returns elements list
     * @param elementName Element name
     * @return elements list
     * @throws XmlException If an error occurs.
     */
    public List<Map<String, String>> getElements(String elementName) throws XmlException {
        String xpath = "//" + elementName;
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        try {
            NodeList nodes = XPathAPI.selectNodeList(ctx.getDocument(), xpath);
            for (int i = 0; i < nodes.getLength(); i++) {
                Map<String, String> attr_map = new HashMap<String, String>();
                for (int j = 0; j < nodes.item(i).getAttributes().getLength(); j++) {
                    String attr_name = nodes.item(i).getAttributes().item(j).getNodeName();
                    String attr_value = nodes.item(i).getAttributes().item(j).getNodeValue();
                    attr_map.put(attr_name, attr_value);
                }
                if (attr_map != null)
                    result.add(attr_map);
            }
        } catch (Exception e) {
            throw new XmlException(e);
        }
        return result;
    }

    /**
     * Returns element values list
     * @param elementName Element name
     * @return element values list
     * @throws XmlException If an error occurs.
     */
    public List<String> getElementValues(String elementName) throws XmlException {
        String xpath = "//" + elementName;
        List<String> result = new ArrayList<String>();
        try {
            NodeList nodes = XPathAPI.selectNodeList(ctx.getDocument(), xpath);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node child = nodes.item(i).getFirstChild();
                if (child != null) {
                    String value = child.getNodeValue();
                    result.add(value);
                }
            }
        } catch (Exception e) {
            throw new XmlException(e);
        }
        return result;
    }

    /**
     * Returns schema elements
     * @return Schema elements
     * @throws XmlException If an error occurs.
     */
    public List<String> getSchemaElements() throws XmlException {
        String xpath = "//xs:element";
        List<String> result = new ArrayList<String>();
        try {
            NodeList nodes = XPathAPI.selectNodeList(ctx.getDocument(), xpath);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getAttributes() != null && nodes.item(i).getAttributes().getNamedItem("name") != null) {
                    String elemName = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
                    if (elemName != null)
                        result.add(elemName);
                }
            }
        } catch (Exception e) {
            throw new XmlException(e);
        }
        return result;
    }

    /**
     * Returns schema element type
     * @param elementName Element name
     * @return Element type
     * @throws XmlException If an error occurs.
     */
    public String getSchemaElementType(String elementName) throws XmlException {
        String xpath = "//xs:element[@name='" + elementName + "']//xs:restriction";
        String base = null;
        try {
            NodeList nodes = XPathAPI.selectNodeList(ctx.getDocument(), xpath);
            if (nodes.getLength() > 0) {
                if (nodes.item(0).getAttributes() != null && nodes.item(0).getAttributes().getNamedItem("base") != null)
                    base = nodes.item(0).getAttributes().getNamedItem("base").getNodeValue();
            }
        } catch (Exception e) {
            throw new XmlException(e);
        }
        return base;
    }

    /**
     * Returns Schema imports
     * @return Schema imports
     * @throws XmlException If an error occurs.
     */
    public List<String> getSchemaImports() throws XmlException {
        String xpath = "//xs:import";
        List<String> result = new ArrayList<String>();
        try {
            NodeList nodes = XPathAPI.selectNodeList(ctx.getDocument(), xpath);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getAttributes() != null && nodes.item(i).getAttributes().getNamedItem("schemaLocation") != null) {
                    String schemaLocation = nodes.item(i).getAttributes().getNamedItem("schemaLocation").getNodeValue();
                    if (schemaLocation != null)
                        result.add(schemaLocation);
                }
            }
        } catch (Exception e) {
            throw new XmlException(e);
        }
        return result;
    }

    /**
     * Returns schema element without multiple values
     * @return Elements without multiple values
     * @throws XmlException If an error occurs.
     */
    public Map<String, String> getSchemaElementWithMultipleValues() throws XmlException {
        String xpath = "//xs:element[@maxOccurs='unbounded']";
        Map<String, String> elements = new HashMap<String, String>();
        try {
            NodeList nodes = XPathAPI.selectNodeList(ctx.getDocument(), xpath);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getAttributes() != null && nodes.item(i).getAttributes().getNamedItem("ref") != null
                        && nodes.item(i).getAttributes().getNamedItem("dd:multiValueDelim") != null) {
                    String elemName = nodes.item(i).getAttributes().getNamedItem("ref").getNodeValue();
                    String delimiter = nodes.item(i).getAttributes().getNamedItem("dd:multiValueDelim").getNodeValue();
                    if (elemName != null) {
                        // get elem name without namespace
                        if (elemName.contains(":"))
                            elemName = elemName.substring(elemName.indexOf(":") + 1);
                        elements.put(elemName, delimiter);
                    }
                }
            }
        } catch (Exception e) {
            throw new XmlException(e);
        }
        return elements;
    }

}
