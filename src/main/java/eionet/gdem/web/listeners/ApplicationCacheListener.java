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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import eionet.gdem.cache.CacheManagerUtil;
import net.sf.ehcache.CacheManager;


/**
 * Type to hold all application caches.
 *
 * @author Enriko Käsper, TripleDev
 */
public class ApplicationCacheListener implements ServletContextListener {

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent) {@inheritDoc}
     */
    @Override
    public void contextDestroyed(final ServletContextEvent arg0) {
        CacheManager.getInstance().shutdown();
    }

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent) {@inheritDoc}
     */
    @Override
    public void contextInitialized(final ServletContextEvent arg0) {
        CacheManagerUtil.create();
    }

}
