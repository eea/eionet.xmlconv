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
 * Contributor(s):* Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.web.struts.qascript;

import java.util.List;

public class QAScriptListHolder {
	/**
	 * @author Enriko Käsper, Tieto Estonia
	 * QAScriptListHolder
	 */

	private List qascripts;
	boolean ssiPrm;
	boolean ssdPrm;


	public QAScriptListHolder() {
	}


	public List getQascripts() {
		return qascripts;
	}


	public void setQascripts(List qascripts) {
		this.qascripts = qascripts;
	}


	public boolean isSsiPrm() {
		return ssiPrm;
	}


	public void setSsiPrm(boolean ssiPrm) {
		this.ssiPrm = ssiPrm;
	}


	public boolean isSsdPrm() {
		return ssdPrm;
	}


	public void setSsdPrm(boolean ssdPrm) {
		this.ssdPrm = ssdPrm;
	}

}
