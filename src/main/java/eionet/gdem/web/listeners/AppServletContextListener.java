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

import eionet.acl.AccessController;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Properties;
import eionet.gdem.configuration.ConfigurationFactory;
import eionet.gdem.dto.ConvType;
import eionet.gdem.qa.XQScript;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;
import eionet.gdem.web.struts.stylesheet.StylesheetListLoader;
import java.lang.reflect.Field;
import static java.time.Clock.system;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@Component
public class AppServletContextListener implements ApplicationListener {

    @Autowired
    private ConfigurationFactory configurationFactory;

    private static final Log LOGGER = LogFactory.getLog(AppServletContextListener.class);

    /**
     * Public constuctor
     */
    public AppServletContextListener() {
    }

    /**
     * Checks if such folders exists, if not, they are created.
     */
    private void checkFolders() {
        String[] folders
                = {Properties.xslFolder, Properties.queriesFolder, Properties.tmpFolder, Properties.xmlfileFolder,
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

    public static List<ConvType> loadConvTypes(String[] types) {

        List<ConvType> l = new ArrayList<ConvType>(types.length);

        for (String type : types) {
            ConvType ct = new ConvType();
            ct.setConvType(type);
            l.add(ct);
        }
        return l;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("Application started !");
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        if (event instanceof ContextClosedEvent) {
            System.out.println("Application terminated !");;
            return;
        }
        if (!(event instanceof ContextRefreshedEvent)) {
            return;
        }
        ContextRefreshedEvent e = (ContextRefreshedEvent) event;
        ApplicationContext appContext = e.getApplicationContext();
        if (!(appContext instanceof WebApplicationContext)) {
            return;
        }
        WebApplicationContext ctx = (WebApplicationContext) e.getApplicationContext();
        ServletContext context = ctx.getServletContext();
        try {

            Properties.metaXSLFolder = context.getRealPath("/dcm");
            Properties.convFile = context.getRealPath("/dcm/conversions.xml");
            Properties.odsFolder = context.getRealPath("/opendoc/ods");
            Properties.appHome = context.getRealPath("/WEB-INF/classes");

            checkFolders();
            context.setAttribute("qascript.resulttypes",
                    loadConvTypes(XQScript.SCRIPT_RESULTTYPES));
            context.setAttribute("qascript.scriptlangs", loadConvTypes(XQScript.SCRIPT_LANGS));
            context.setAttribute(QAScriptListLoader.QASCRIPT_PERMISSIONS_ATTR,
                    QAScriptListLoader.loadQAScriptPermissions(null));
            context.setAttribute(StylesheetListLoader.STYLESHEET_PERMISSIONS_ATTR,
                    StylesheetListLoader.loadStylesheetPermissions(null));

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
