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
 * The Original Code is Content Registry 2.0.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Aleksandr Ivanov, Tieto Eesti
 */
package eionet.gdem.web.listeners;

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import eionet.gdem.dto.DDDatasetTable;

/**
 * Type to hold all application caches.
 *
 * @author Enriko Käsper, TripleDev
 */
public class ApplicationCache implements ServletContextListener {
    /**
     * Application (main) cache name.
     */
    public static final String APPLICATION_CACHE = "ApplicationCache";

    /** Data Dictionary tables data cache name. */
    private static final String DD_TABLES_CACHE = "ddTables";

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent) {@inheritDoc}
     */
    @Override
    public void contextDestroyed(final ServletContextEvent arg0) {
        CacheManager.getInstance().shutdown();
    }

    /**
     * Returns application cache.
     *
     * @return Cache - main application cache
     */
    private static Cache getCache() {
        return CacheManager.getInstance().getCache(APPLICATION_CACHE);
    }

    /**
     * Update Data Dictionary tables data cache.
     *
     * @param ddTables
     *            List of DD info retrieved from xml-rpc method.
     */
    public static void updateDDTablesCache(final List<DDDatasetTable> ddTables) {
        getCache().put(new Element(DD_TABLES_CACHE, ddTables));
    }

    /**
     * Get Data Dictionary dataset tables.
     * @return List of DDDatasetTable
     */
    @SuppressWarnings("unchecked")
    public static List<DDDatasetTable> getDDTables() {

        Element element = getCache() != null ? getCache().get(DD_TABLES_CACHE) : null;

        return element == null || element.getValue() == null ? Collections.EMPTY_LIST : (List<DDDatasetTable>) element.getValue();

    }

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent) {@inheritDoc}
     */
    @Override
    public void contextInitialized(final ServletContextEvent arg0) {
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.addCache(APPLICATION_CACHE);
    }

}
