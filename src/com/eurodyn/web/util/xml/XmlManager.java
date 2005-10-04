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

package com.eurodyn.web.util.xml;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class XmlManager implements IXUpdate {

	protected IXmlCtx ctx = null;


	public XmlManager() {

	}

	public XmlManager(IXmlCtx ctx) {
		this.ctx = ctx;
	}

	public void updateElement(String parentId, String name, String newValue) throws XmlException {
		String xpath = "//*[@id='" + parentId + "']/" + name + "/text()";
		Node textNode = null;
		try {
			textNode = XPathAPI.selectSingleNode(ctx.getDocument(), xpath);
			if (textNode == null) {
				textNode = XPathAPI.selectSingleNode(ctx.getDocument(), "//" + name);
				if (textNode == null)
					throw new XmlException("Node " + name + " can not be found");
				else {
					Text newElement = ctx.getDocument().createTextNode(newValue);
					textNode.appendChild(newElement);
				}
			} else {
				Node parent = textNode.getParentNode();
				Text newElement = ctx.getDocument().createTextNode(newValue);
				parent.replaceChild(newElement, textNode);
			}
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new XmlException("Error while setting value to element " + name + ": " + e.getMessage());
		}
	}
		
	public void deleteElement(String parentId,String name) throws XmlException {
		try {
			String xpath = "//*[@id='" + parentId + "']/" + name;
			Node node = XPathAPI.selectSingleNode(ctx.getDocument(), xpath);
			if (node == null) return;
			Node parent = node.getParentNode();
			((Element) parent).removeChild(node);
		} catch (Exception e) {
			throw new XmlException("Error while removing XML node " + name + ": " + e.getMessage());
		}
	}

	public void insertElement(String parentElementName, String elementName) throws XmlException {
		// TODO Auto-generated method stub

	}

	public void insertAttribute(String parentElementName, String attributeName, String attributeValue) throws XmlException {
		// TODO Auto-generated method stub

	}

}
