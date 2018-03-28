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

package eionet.gdem.web.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Sets character encoding filter.
 * @author Unknown
 * @author George Sofianos
 */
public class SetCharacterEncodingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetCharacterEncodingFilter.class);
    /**
     * The default character encoding to set for requests that pass through this filter.
     */
    protected String encoding = null;
    /**
     * The filter configuration object we are associated with. If this value is null, this filter instance is not currently
     * configured.
     */
    protected FilterConfig filterConfig = null;
    /**
     * Should a character encoding specified by the client be ignored?
     */
    protected boolean ignore = true;

    /**
     * Take this filter out of service.
     */
    public void destroy() {

        this.encoding = null;
        this.filterConfig = null;

    }

    /**
     * Select and set (if specified) the character encoding to be used to interpret request parameters for this request.
     *
     * @param request
     *            The servlet request we are processing
     * @param response
     *            The servlet response we are creating
     * @param chain
     *            The filter chain we are processing
     *
     * @exception IOException
     *                if an input/output error occurs
     * @exception ServletException
     *                if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // Conditionally select and set the character encoding to be used
        if (ignore || (request.getCharacterEncoding() == null)) {
            String encoding = selectEncoding(request);
            if (encoding != null)
                request.setCharacterEncoding(encoding);
        }

        // Pass control on to the next filter
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            // Logging unhandled exception
            LOGGER.error("Unhandled exception caught", e);
            throw new ServletException(e);
        }

    }

    /**
     * Place this filter into service.
     *
     * @param filterConfig
     *            The filter configuration object
     * @throws ServletException If an error occurs.
     */
    public void init(FilterConfig filterConfig) throws ServletException {

        this.filterConfig = filterConfig;
        this.encoding = filterConfig.getInitParameter("encoding");
        String value = filterConfig.getInitParameter("ignore");
        if (value == null)
            this.ignore = true;
        else if (value.equalsIgnoreCase("true"))
            this.ignore = true;
        else if (value.equalsIgnoreCase("yes"))
            this.ignore = true;
        else
            this.ignore = false;

    }

    /**
     * Select an appropriate character encoding to be used, based on the characteristics of the current request and/or filter
     * initialization parameters. If no character encoding should be set, return <code>null</code>.
     * <p>
     * The default implementation unconditionally returns the value configured by the <strong>encoding</strong> initialization
     * parameter for this filter.
     *
     * @param request
     *            The servlet request we are processing
     */
    protected String selectEncoding(ServletRequest request) {

        return (this.encoding);

    }

}
