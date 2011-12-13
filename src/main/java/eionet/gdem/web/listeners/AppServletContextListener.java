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

package eionet.gdem.web.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Properties;
import eionet.gdem.dto.ConvType;
import eionet.gdem.qa.XQScript;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;
import eionet.gdem.web.struts.stylesheet.StylesheetListLoader;

public class AppServletContextListener implements ServletContextListener {

    private static final Log LOGGER = LogFactory.getLog(AppServletContextListener.class);

    /**
     * Public constuctor
     */
    public AppServletContextListener() {
    }

    /**
     * Method that is triggered once on start of application (context initialization):
     *
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Application started !");
        try {

            Properties.metaXSLFolder = servletContextEvent.getServletContext().getRealPath("/dcm");
            Properties.convFile = servletContextEvent.getServletContext().getRealPath("/dcm/conversions.xml");
            Properties.odsFolder = servletContextEvent.getServletContext().getRealPath("/opendoc/ods");
            Properties.appHome = servletContextEvent.getServletContext().getRealPath("/WEB-INF/classes");

            checkFolders();

            servletContextEvent.getServletContext().setAttribute("qascript.resulttypes",
                    loadConvTypes(XQScript.SCRIPT_RESULTTYPES));
            servletContextEvent.getServletContext().setAttribute("qascript.scriptlangs", loadConvTypes(XQScript.SCRIPT_LANGS));
            servletContextEvent.getServletContext().setAttribute(QAScriptListLoader.QASCRIPT_PERMISSIONS_ATTR,
                    QAScriptListLoader.loadQAScriptPermissions(null));
            servletContextEvent.getServletContext().setAttribute(StylesheetListLoader.STYLESHEET_PERMISSIONS_ATTR,
                    StylesheetListLoader.loadStylesheetPermissions(null));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Checks if such folders exists, if not, they are created.
     */
    private void checkFolders() {
        String[] folders =
                {Properties.xslFolder, Properties.queriesFolder, Properties.tmpFolder, Properties.xmlfileFolder,
                        Properties.schemaFolder};

        for (String folder : folders) {
            File f = new File(folder);
            if (!f.isDirectory()) {

                if (!f.mkdirs()) {
                    LOGGER.warn("Could not create folder: " + f.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Method that is triggered once on destroy of servlet context
     *
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Application terminated !");
    }

    public static List<ConvType> loadConvTypes(String[] types) {

        List<ConvType> l = new ArrayList<ConvType>(types.length);

        for (String type : types) {
            ConvType ct = new ConvType();
            ct.setConvType(type);
            l.add(ct);
        }
        return l;
    }
}
