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

package eionet.gdem.dcm.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;




/**
 * LDAP Connection test class.
 * @author Unknown
 * @author George Sofianos
 */
public class LdapTest {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapTest.class);

    private String url;

    /**
     * Constructor
     * @param url Connection URL
     */
    public LdapTest(String url) {
        this.url = url;
    }

    /**
     * Gets DirContext
     * TODO check if possible to replace with a modern library like in UNS application.
     * @return DirContext
     * @throws NamingException If an error occurs.
     */
    protected DirContext getDirContext() throws NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        DirContext ctx = new InitialDirContext(env);
        return ctx;
    }

    /**
     * Closes context
     * @param ctx Context
     * @throws NamingException If an error occurs.
     */
    protected void closeContext(DirContext ctx) throws NamingException {
        if (ctx != null) {
            ctx.close();
        }
    }

    /**
     * Tests connection
     * @return True if test completed.
     */
    public boolean test() {
        try {
            DirContext ctx = getDirContext();
            closeContext(ctx);
            return true;
        } catch (Exception e) {
            LOGGER.error("Testing ldap connection failed!", e);
            return false;
        }
    }

}
