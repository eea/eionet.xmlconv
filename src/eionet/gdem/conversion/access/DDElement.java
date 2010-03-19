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
 * The Original Code is XMLCONV.
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 * 
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.conversion.access;

/**
 * @author Enriko Käsper, Tieto Estonia
 * DDElement
 */

public class DDElement {
	private String elmIdf=null;

	public DDElement(String elmIdf) {
		this.elmIdf = elmIdf;
	}

	public String getElmIdf() {
		return elmIdf;
	}

	public void setElmIdf(String elmIdf) {
		this.elmIdf = elmIdf;
	}
	public boolean equals(DDElement ddElm){
		if(getElmIdf()!=null && ddElm!=null && ddElm.getElmIdf()!=null){
			return getElmIdf().equalsIgnoreCase(ddElm.getElmIdf());
		}
		return false;
	}

	@Override
	public String toString() {
		return "DDElement [elmIdf=" + elmIdf + "]";
	}
	
}
