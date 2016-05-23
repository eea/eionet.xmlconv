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
 *                          Nedeljko Pavlovic (ED)
 */

package eionet.gdem.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Menu Item class.
 * @author Unknown
 * @author George Sofianos
 */
public class MenuItem extends TagSupport {

    private String title;

    private String action;

    private String selectedPrefix;

    private String onclick;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Default constructor.
     */
    public MenuItem() {
    }

    /**
     * Starts tag
     * @return Int
     * @throws JspException If an error occurs.
     */
    public int doStartTag() throws JspException {
        try {
            HttpServletRequestWrapper r = (HttpServletRequestWrapper) pageContext.getRequest();
            pageContext.getOut().print("<li");
            String spath = (String) pageContext.getRequest().getAttribute("ServletPath");
            if (spath == null) {
                spath = r.getServletPath();
            }

            String prefix = selectedPrefix;
            if (prefix == null)
                prefix = action;
            if (spath != null && spath.startsWith(prefix))
                pageContext.getOut().print(" class=\"selected\"");

            pageContext.getOut().print("><a href=\"");

            Object root = r.getContextPath();
            if (root != null && onclick == null)
                pageContext.getOut().print(root.toString());
            if (onclick == null) {
                pageContext.getOut().print(action);
            } else {
                pageContext.getOut().print(onclick);
            }
            pageContext.getOut().print("\" title=\"");
            pageContext.getOut().print(title);
            pageContext.getOut().print("\">");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EVAL_BODY_INCLUDE;
    }

    /**
     * Does end tag
     * @return Int
     */
    public int doEndTag() {
        try {
            pageContext.getOut().print("</a></li>");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EVAL_PAGE;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    /**
     * Gets path
     * @param fullPath Full path
     * @return Processed path
     */
    private String getPath(String fullPath) {
        int i = fullPath.lastIndexOf('/');
        return fullPath.substring(0, i + 1);
    }

    public String getSelectedPrefix() {
        return selectedPrefix;
    }

    public void setSelectedPrefix(String selectedPrefix) {
        this.selectedPrefix = selectedPrefix;
    }

}
