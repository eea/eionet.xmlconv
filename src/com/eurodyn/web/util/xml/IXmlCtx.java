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

import java.io.InputStream;

import org.w3c.dom.Document;

public interface IXmlCtx {

	public void setWellFormednessChecking() throws XmlException;


	public void setValidationChecking() throws XmlException;


	public void checkFromInputStream(InputStream inputStream) throws XmlException;


	public void checkFromFile(String fullFileName) throws XmlException;


	public void createXMLDocument() throws XmlException;


	public void createXMLDocument(String docTypeName, String systemId) throws XmlException;


	public IXUpdate getManager();


	public IXmlSerializer getSerializer();


	public IXQuery getQueryManager();


	public Document getDocument();


	public void setDocument(Document document);

}
