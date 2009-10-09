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
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA).
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 * Created on 27.06.2006
 */

package eionet.gdem.dcm.business;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.tee.uit.client.ServiceClientIF;
import com.tee.uit.client.ServiceClients;

import eionet.gdem.Properties;
import eionet.gdem.utils.Utils;

public class CDRServiceClient {

	protected String serviceUrl = null;
	protected String serviceName = "";
	protected ServiceClientIF client = null;


	protected void load() throws Exception {
		if ( serviceUrl == null || serviceUrl.equals("")) throw new Exception("serviceUrl is missing!");
		client = ServiceClients.getServiceClient(serviceName, serviceUrl);
	}


	protected void getProps() {
		serviceUrl = Properties.cdrServUrl;
	}


	protected Object execute(String method, Vector params) throws Exception {
		if (client == null) load();
		return client.getValue(method, params);
	}


	public void execute(HttpServletRequest req) throws Exception {
	}


	public static List searchXMLFiles(String schemaURL) throws Exception{
		if ( Utils.isNullStr(schemaURL)) throw new Exception("schemaURL is missing!");

		CDRServiceClient d = new CDRServiceClient();
		List list = null;
		try {
			Vector b = new Vector();
			b.add(schemaURL);
			d.getProps();
			d.load();
			Object res = d.execute("searchxmlfiles", b);
			list = (List) res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}


	public static void main(String args[]) {
		String schemaURL = "http://waste.eionet.eu.int/schemas/dir200053ec/schema.xsd";
		CDRServiceClient d = new CDRServiceClient();
		try {
			Vector b = new Vector();
			b.add(schemaURL);
			d.getProps();
			d.load();
			Object res = d.execute("searchxmlfiles", b);
			Vector list = (Vector) res;
			for (int i = 0; i < list.size(); i++) {
				Object o = list.get(i);
				System.out.println(i + " - " + o);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
