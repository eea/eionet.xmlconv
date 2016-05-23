package eionet.gdem.web.tags;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import eionet.gdem.utils.SecurityUtil;

/**
 * Has Permission class.
 * @author Unknown
 * @author George Sofianos
 */
public class HasPermission extends ConditionalTagSupport {

    private String username;
    private String permission;
    private String acl;

    /**
     * Default constructor.
     */
    public HasPermission() {
    }

    /**
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username
     * @param strUri username
     */
    public void setUsername(String strUri) {
        username = strUri;

    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getAcl() {
        return acl;
    }

    public void setAcl(String acl) {
        this.acl = acl;
    }

    /**
     * allow or not to display jsp content;depends on user's roles (Principals).
     * 
     * @return true if tag displays content when user has got the specified role(principal); false otherwise
     * @see javax.servlet.jsp.jstl.core.ConditionalTagSupport#condition()
     * @throws JspTagException If an error occurs.
     */
    protected boolean condition() throws JspTagException {
        String u = (String) pageContext.getAttribute(username);
        boolean result = false;
        try {
            result = u != null && SecurityUtil.hasPerm(u, "/" + acl, permission);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
