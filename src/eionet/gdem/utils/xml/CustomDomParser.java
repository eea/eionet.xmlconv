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

import org.apache.xerces.parsers.*;
import org.apache.xerces.util.*;
import org.apache.xerces.xni.*;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public class CustomDomParser extends DOMParser {
	String _mimeEncoding = "UTF-8";
	
	public CustomDomParser() {
	      super();
	}
	
	public CustomDomParser(XMLParserConfiguration config) {
	      super(config);
	   }


	private void setMimeEncoding(String encoding) {
		_mimeEncoding = encoding;
	}


	private String getMimeEncoding() {
		return (_mimeEncoding);
	}


	public String getJavaEncoding() {
		String javaEncoding = null;
		String mimeEncoding = getMimeEncoding();
		if (mimeEncoding != null) {
			if (mimeEncoding.equals("DEFAULT"))
				javaEncoding = "UTF8";
			else if (mimeEncoding.equalsIgnoreCase("UTF-16"))
				javaEncoding = "Unicode";
			else
				javaEncoding = EncodingMap.getIANA2JavaMapping(mimeEncoding);
		}
		if (javaEncoding == null) javaEncoding = "UTF8";
		return (javaEncoding);
	}//getJavaEncoding()


	public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
		if (encoding != null) {
			setMimeEncoding(encoding);
		}
		super.startGeneralEntity(name, identifier, encoding, augs);
	}//startGeneralEntity
}