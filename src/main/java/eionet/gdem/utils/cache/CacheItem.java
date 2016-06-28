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
 *    Original code: Dusan Popovic (ED)
 */

package eionet.gdem.utils.cache;

import java.util.Date;

/**
 * Cache Item class.
 * @author Unknown
 * @author George Sofianos
 */
public class CacheItem {
    protected String key;

    protected Object content;

    protected long lastAccessed;

    protected long timeToLive = 10 * 60 * 1000; // in ms, 10 minutes

    /**
     * Default private constructor
     */
    private CacheItem() {
    }

    /**
     * Constructs a CacheItem object
     *
     * @param key Key
     * @param timeToLive
     *            ms to keep this in the cache
     * @param content
     *            The content being cached
     */
    public CacheItem(String key, Object content, long timeToLive) {
        this.key = key;
        this.content = content;
        this.timeToLive = timeToLive;
        this.lastAccessed = new Date().getTime();
    }

    /**
     * Set the cache's last accessed stamp
     *
     * @param lastAccessed
     *            the cache's last access stamp
     */
    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    /**
     * Get the cache's lastAccessed stamp
     *
     * @return the cache's last accessed stamp
     */
    public long getLastAccessed() {
        return this.lastAccessed;
    }

    /**
     * Set the content in the cache
     *
     * @param content
     *            the content being cached
     */
    public void setContent(Object content) {
        this.content = content;
    }

    /**
     * Get the content
     *
     * @return the content being cached
     */
    public Object getContent() {
        return this.content;
    }

    /**
     * @return Returns the key.
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            The key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return Returns the timeToLive.
     */
    public long getTimeToLive() {
        return timeToLive;
    }

    /**
     * @param timeToLive
     *            The timeToLive in seconds
     */
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }
}
