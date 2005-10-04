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
 *    Original code: Nenad Popovic (ED)
 */

package eionet.gdem.web.struts.uimanage;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class ImageManagerForm extends ActionForm {
	protected FormFile picFile;
	protected String deletePic;
	/**
	 * @return Returns the deletePic.
	 */
	public String getDeletePic() {
		return deletePic;
	}

	/**
	 * @param deletePic The deletePic to set.
	 */
	public void setDeletePic(String deletePic) {
		this.deletePic = deletePic;
	}

	/**
	 * @return Returns the picFile.
	 */
	public FormFile getPicFile() {
		return picFile;
	}

	/**
	 * @param picFile The picFile to set.
	 */
	public void setPicFile(FormFile picFile) {
		this.picFile = picFile;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.picFile = null;
		this.deletePic=null;
	}
}
