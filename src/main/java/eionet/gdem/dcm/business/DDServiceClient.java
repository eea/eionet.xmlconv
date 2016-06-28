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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import eionet.rpcclient.ServiceClientIF;
import eionet.rpcclient.ServiceClients;

import eionet.gdem.Properties;
import eionet.gdem.dto.DDDatasetTable;
import eionet.gdem.web.listeners.ApplicationCache;

/**
 *
 * DD service client class queries data from Data Dictionary over xml-rpc.
 *
 * @author Enriko Käsper
 */
public class DDServiceClient {

    /** DD xml rpc service name. */
    protected String serviceName = null;
    /** DD xml rpc service URL. */
    protected String serviceUrl = null;
    /** UIT client. */
    protected ServiceClientIF client = null;
    /** Dataset for testing the service interface. */
    private static Map<String, String> mockDataset = null;

    /**
     * Load service properties.
     *
     * @throws Exception
     *             if properties are missing in eionet.properties file.
     */
    protected void load() throws Exception {
        if (serviceName == null || serviceName.equals("") || serviceUrl == null || serviceUrl.equals("")) {
            throw new Exception("serviceName or serviceUrl is missing!");
        }
        client = ServiceClients.getServiceClient(serviceName, serviceUrl);
    }

    /**
     * Get property values from Propeties.
     */
    protected void getProps() {
        serviceName = Properties.invServName;
        serviceUrl = Properties.invServUrl;
    }

    /**
     * Execute xml rpc method.
     *
     * @param method
     *            String method name
     * @param params
     *            Vector method parameters
     * @return Return the result from xml rpc method
     * @throws Exception
     *             when call to remote service fails or service is unresponsive.
     */
    protected Object execute(String method, Vector params) throws Exception {
        if (client == null) {
            load();
        }
        return client.getValue(method, params);
    }

    /**
     * Get DD dataset tables data from XMLCONV cache or call getDSTables method from DD.
     *
     * @return the list of DDDatasetTable objects.
     */
    public static List<DDDatasetTable> getDDTables() {
        List<DDDatasetTable> ddTables = ApplicationCache.getDDTables();
        if (ddTables == null || ddTables.size() == 0) {
            ddTables = getDDTablesFromDD();
        }
        return ddTables;
    }

    /**
     * Call getDSTables method from DD.
     *
     * @return the list of DDDatasetTable objects.
     */
    public static List<DDDatasetTable> getDDTablesFromDD() {
        DDServiceClient d = new DDServiceClient();
        List<DDDatasetTable> list = null;
        try {
            d.getProps();
            d.load();
            Object res = d.execute("getDSTables", new Vector<Object>());
            if (res != null && res instanceof List<?>) {
                list = new ArrayList<DDDatasetTable>();
                for (Hashtable<String, String> resultItem : (List<Hashtable<String, String>>) res) {
                    if (resultItem.containsKey("tblId") && resultItem.get("tblId") != null) {
                        DDDatasetTable ddTable = new DDDatasetTable(resultItem.get("tblId"));
                        ddTable.setDataSet(resultItem.get("dataSet"));
                        ddTable.setShortName(resultItem.get("shortName"));
                        ddTable.setDateReleased(resultItem.get("ddMMyy"));
                        list.add(ddTable);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Call getDatasetWithReleaseInfo method from DD.
     * @param type Type
     * @param id Id
     * @return the list of DDDatasetTable objects.
     */
    public static Map<String, String> getDatasetWithReleaseInfo(String type, String id) {
        DDServiceClient d = new DDServiceClient();
        Map result = null;
        try {
            Vector<String> params = new Vector<String>();
            params.add(type);
            params.add(id);
            d.getProps();
            d.load();
            Object res = d.execute("getDatasetWithReleaseInfo", params);
            result = (Map<String, String>) res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Method for testing purposes.
     * TODO check possibility of replacing this.
     * @param type Type
     * @param id Id
     * @return Mock dataset
     */
    public static Map<String, String> getMockDataset(String type, String id) {
        return mockDataset;
    }

    /**
     * Setter method for testing purpoeses.
     * TODO check possibility of replacing this.
     * @param mockDataset
     *            Set fake data.
     */
    public static void setMockDataset(Map<String, String> mockDataset) {
        DDServiceClient.mockDataset = mockDataset;
    }
}
