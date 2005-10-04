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

public class ErrorStorage {

	private String errorMessage = "";
	private String waringMessage = "";
	private String fatalErrorMessage = "";


	public ErrorStorage() {
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		if (errorMessage.length() > 128)
			errorMessage = errorMessage.substring(0, 128) + "...";
		if (this.errorMessage.length() < 128 && this.errorMessage.indexOf(errorMessage) < 0)
			this.errorMessage += " - " + errorMessage + "  ";
	}

	public String getFatalErrorMessage() {
		return fatalErrorMessage;
	}

	public void setFatalErrorMessage(String fatalErrorMessage) {
		if (fatalErrorMessage.length() > 128)
			fatalErrorMessage = fatalErrorMessage.substring(0, 128) + "...";
		this.fatalErrorMessage += " - " + fatalErrorMessage + "  ";
	}

	public String getWaringMessage() {
		return waringMessage;
	}

	public void setWaringMessage(String waringMessage) {
		if (waringMessage.length() > 128)
			waringMessage = waringMessage.substring(0, 128) + "...";
		this.waringMessage += waringMessage + "  ";
	}

	public boolean isEmpty() {
		if (errorMessage.equalsIgnoreCase("") && fatalErrorMessage.equalsIgnoreCase("")) {
			return true;
		} else {
			return false;
		}
	}


	public String getErrors() {
		StringBuffer sb = new StringBuffer();
		if (errorMessage != null) sb.append(errorMessage);
		if (fatalErrorMessage != null) sb.append(fatalErrorMessage);
		return sb.toString();
	}

}