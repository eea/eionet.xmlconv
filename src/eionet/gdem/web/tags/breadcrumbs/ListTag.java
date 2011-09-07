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

package eionet.gdem.web.tags.breadcrumbs;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class ListTag extends TagSupport {

    private String var;

    private String delimiter = "&nbsp";

    private String htmlid;

    private String classStyle;

    private String classStyleEnd;

    public ListTag() {
    }

    public String getVar() {
        return this.var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public int doEndTag() throws JspException {
        BreadCrumbs breadcrumbs = JspUtils.getBreadCrumbs(pageContext);

        StringBuffer buffer = new StringBuffer();
        buffer.append("<script type=\"text/javascript\">\n");
        buffer.append("//<!--\n");
        buffer.append("var bc = document.getElementById(\"");
        buffer.append(htmlid);
        buffer.append("\");\n");
        buffer.append("if(bc != null){\n");
        buffer.append("\tbc.innerHTML += '");
        Iterator itr = breadcrumbs.iterateTrail();
        while (itr.hasNext()) {

            BreadCrumb breadcrumb = (BreadCrumb) itr.next();
            Object label = breadcrumb.getLabel();
            Object url = breadcrumb.getUrl();

            if (itr.hasNext()) {
                buffer.append("<div");
                if (classStyle != null && classStyle.length() > 0)
                    buffer.append(" class=\"" + classStyle + "\"");
                buffer.append("><a href=\"");
                buffer.append(url);
                buffer.append("\">");
                buffer.append(label);
                buffer.append("<\\/a>");
                buffer.append("<\\/div>");
            } else {
                buffer.append("<div");
                if (classStyleEnd != null && classStyleEnd.length() > 0)
                    buffer.append(" class=\"" + classStyleEnd + "\"");
                buffer.append(">");
                buffer.append(label);
                buffer.append("<\\/div>");
            }
            buffer.append(this.delimiter);
        }
        buffer.append("';\n");
        buffer.append("}\n");
        buffer.append("//-->\n");
        buffer.append("</script>\n");

        if (this.var == null) {
            JspWriter writer = pageContext.getOut();
            try {
                writer.print(buffer);
            } catch (IOException ioe) {
                throw new JspException(ioe.toString());
            }
        } else {
            pageContext.setAttribute(this.var, buffer);
        }
        return EVAL_PAGE;
    }

    public String getClassStyle() {
        return classStyle;
    }

    public void setClassStyle(String classStyle) {
        this.classStyle = classStyle;
    }

    public String getClassStyleEnd() {
        return classStyleEnd;
    }

    public void setClassStyleEnd(String classStyleEnd) {
        this.classStyleEnd = classStyleEnd;
    }

    public String getHtmlid() {
        return htmlid;
    }

    public void setHtmlid(String htmlid) {
        this.htmlid = htmlid;
    }

}
