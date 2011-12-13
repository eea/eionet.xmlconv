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
 *    Original code: Istvan Alfeldi (ED)
 */

package eionet.gdem.dcm.business;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.tee.uit.client.ServiceClientIF;
import com.tee.uit.client.ServiceClients;

import eionet.gdem.Properties;

public class DDServiceClient {

    protected String serviceName = null;
    protected String serviceUrl = null;
    protected ServiceClientIF client = null;
    private static Map<String, String> mockDataset = null;

    protected void load() throws Exception {
        if (serviceName == null || serviceName.equals("") || serviceUrl == null || serviceUrl.equals(""))
            throw new Exception("serviceName or serviceUrl is missing!");
        client = ServiceClients.getServiceClient(serviceName, serviceUrl);
    }

    protected void getProps() {
        serviceName = Properties.invServName;
        serviceUrl = Properties.invServUrl;
    }

    protected Object execute(String method, Vector params) throws Exception {
        if (client == null)
            load();
        return client.getValue(method, params);
    }

    public void execute(HttpServletRequest req) throws Exception {
    }

    public static List getDDTables() {
        DDServiceClient d = new DDServiceClient();
        List list = null;
        try {
            Vector b = new Vector();
            d.getProps();
            d.load();
            Object res = d.execute("getDSTables", b);
            list = (List) res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Map<String, String> getDatasetWithReleaseInfo(String type, String id) {
        DDServiceClient d = new DDServiceClient();
        Map result = null;
        try {
            Vector b = new Vector();
            b.add(type);
            b.add(id);
            d.getProps();
            d.load();
            Object res = d.execute("getDatasetWithReleaseInfo", b);
            result = (Map<String, String>) res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * The testing purposes
     * 
     * @return
     */
    public static Map<String, String> getMockDataset(String type, String id) {
        return mockDataset;
    }

    public static void setMockDataset(Map<String, String> mockDataset) {
        DDServiceClient.mockDataset = mockDataset;
    }
}
