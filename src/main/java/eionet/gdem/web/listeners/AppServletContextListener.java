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
import eionet.gdem.Properties;
import eionet.gdem.dto.ConvType;
import eionet.gdem.qa.XQScript;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;
import eionet.gdem.web.struts.stylesheet.StylesheetListLoader;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Application Context Listener.
 * @author Unknown
 * @author George Sofianos
 */
@Component
public class AppServletContextListener implements ApplicationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppServletContextListener.class);

    /**
     * Default Constructor
     */
    public AppServletContextListener() {
    }

    /**
     * Checks if such folders exists, if not, they are created.
     */
    private void checkFolders() {
        String[] folders
                = {Properties.xslFolder, Properties.getXslFolder(), Properties.getTmpFolder(), Properties.getXmlfileFolder(),
                    Properties.schemaFolder, Properties.tmpfileDir, Properties.CACHE_TEMP_DIR};

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
     * Clears directories from left-over files
     *
     */
    private void cleanDirectories() {
       String[] directories = {Properties.tmpfileDir};
       for (String directory : directories) {
           File f = new File(directory);
           try {
               FileUtils.cleanDirectory(f);
           } catch (Exception e) {
               LOGGER.error("Could not remove directory: " + f.getAbsolutePath());
           }
       }
    }

    /**
     * Gets conversion types
     * @param types Conversion types
     * @return List of conversion types
     */
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
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        if (event instanceof ContextClosedEvent) {
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
            Properties.contextPath = context.getContextPath();
            cleanDirectories();
            checkFolders();
            context.setAttribute("qascript.resulttypes",
                    loadConvTypes(XQScript.SCRIPT_RESULTTYPES));
            context.setAttribute("qascript.scriptlangs", loadConvTypes(XQScript.SCRIPT_LANGS));
            context.setAttribute(QAScriptListLoader.QASCRIPT_PERMISSIONS_ATTR,
                    QAScriptListLoader.loadQAScriptPermissions(null));
            context.setAttribute(StylesheetListLoader.STYLESHEET_PERMISSIONS_ATTR,
                    StylesheetListLoader.loadStylesheetPermissions(null));

        } catch (Exception e1) {
            LOGGER.error("An exception occured while creating context" + e1);
        }
    }
}
