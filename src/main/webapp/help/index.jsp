<%--
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Frameset//EN" "http://www.w3.org/TR/REC-html40/frame.dtd">
<html>
    <head>
        <title>Data Conversion Module Online Help</title>
    </head>
    <frameset frameborder="0" framespacing="0" border="0" cols="*" rows="32,*">
        <frame marginwidth="0" marginheight="0" src="heading.html" name="heading" noresize scrolling="no">
        <frameset frameborder="0" framespacing="0" border="0" cols="25%,*" rows="*">
            <logic:present name="user">
                <frame marginwidth="5" marginheight="5" src="menu-admin.html" name="menu" noresize frameborder="0">
            </logic:present>
            <logic:notPresent name="user">
                <frame marginwidth="5" marginheight="5" src="menu-user.html" name="menu" noresize frameborder="0">
            </logic:notPresent>
            <frame marginwidth="5" marginheight="5" src="docs/index.html" name="text" noresize>
        </frameset>
        <noframes>
        <p>The <code>NOFRAMES</code> element is to be used to give useful content to people with browsers that cannot display frames. One example is Lynx, a text-based browser.</p>
        </noframes>
    </frameset>
</html>
--%>
