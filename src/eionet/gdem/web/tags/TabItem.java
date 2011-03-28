/*
 * Created on 08.04.2008
 */
package eionet.gdem.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * TabItem
 */


public class TabItem extends TagSupport {

    private String id;

    private String title;

    private String href;

    private String selectedTab;


    public TabItem() {
    }

    public int doStartTag() throws JspException {
        try {
            HttpServletRequestWrapper r = (HttpServletRequestWrapper) pageContext
                    .getRequest();
            pageContext.getOut().print("<li");
            String spath = (String) pageContext.getRequest().getAttribute(
                    "ServletPath");
            if (spath == null) {
                spath = r.getServletPath();
            }

            String sel = selectedTab;
            if( sel == null)
                sel = "";
            if (id.equalsIgnoreCase(sel)){
                //selected tab
                pageContext.getOut().print(" id=\"currenttab\"><span>");
            }
            else{
                pageContext.getOut().print(" id=\"");
                pageContext.getOut().print(id);
                pageContext.getOut().print("\"><a href=\"");
                pageContext.getOut().print(r.getContextPath());
                pageContext.getOut().print(href);
                pageContext.getOut().print("\" title=\"");
                pageContext.getOut().print(title);
                pageContext.getOut().print("\">");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() {
        try {
            String sel = selectedTab;
            if( sel == null)
                sel = "";
            if (id.equalsIgnoreCase(sel))
                pageContext.getOut().print("</span>");
            else
                pageContext.getOut().print("</a>");

            pageContext.getOut().print("</li>");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EVAL_PAGE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

}
