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

import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class XmlQuery implements IXQuery {
	
	private IXmlCtx ctx = null;
	
	public XmlQuery(IXmlCtx ctx) {
		this.ctx=ctx;
	}
	
	public Node findElementByAttrs(String parentId, Map attributes) throws XmlException {
		String xpath = "//*[@id='" + parentId + "']/*[";
		Iterator attrs = attributes.keySet().iterator();
		int i = 0;
		while (attrs.hasNext()) {
			String key = (String) attrs.next();
			if (i == 0) {
				xpath += "@" + key + "='" + attributes.get(key) + "' ";
			} else {
				xpath += "and @" + key + "='" + attributes.get(key) + "' ";
			}
			++i;
		}
		xpath += "]";
		System.out.println(xpath);
		Node result = null;
		try {
			result = XPathAPI.selectSingleNode(ctx.getDocument(), xpath);
		} catch (TransformerException e) {
			throw new XmlException(e);
		}
		return result;
	}
	

	public String getAttributeValue(String elementId, String attribute) throws XmlException {
		String xpath = "//*[@id='"+elementId+"']/@"+attribute;
		Attr el = null;
		String result=null;
		try {
			el =(Attr) XPathAPI.selectSingleNode(ctx.getDocument(), xpath);
			if(el!=null ) {
				result=el.getValue();
			}
		} catch (TransformerException e) {
			throw new XmlException(e);
		}
		return result;
	}
	
	
	public String getElementValue(String parentId, String name) throws XmlException {
		String value = null;
		try {
			String xpath = "//*[@id='" + parentId + "']/" + name + "/text()";
			Node textNode = XPathAPI.selectSingleNode(ctx.getDocument(), xpath);
			if (textNode != null) {
				value = textNode.getNodeValue().trim();
				if (value.equalsIgnoreCase("")) value = null;
			}
		} catch (TransformerException e) {
			throw new XmlException(e);
		}
		return value;
	}


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

}
