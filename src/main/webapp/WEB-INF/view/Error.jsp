<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ page import="eionet.gdem.utils.Utils,java.util.Date" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>

<logic:present name="org.apache.struts.action.ACTION_MESSAGE">
    <div class="system-msg">
        <html:messages id="message" message="true">
            <bean:write name="message" filter="false"/>	(<%=Utils.getDateTime(new Date())%>)
        </html:messages>
    </div>
</logic:present>

<logic:present name="dcm.messages">
    <div class="system-msg">
        <html:messages id="message" name="dcm.messages">
            <bean:write name="message" filter="false"/>	(<%=Utils.getDateTime(new Date())%>)<br/>
        </html:messages>

    </div>
</logic:present>

<logic:present name="dcm.errors">
    <div class="error-msg">
        <html:messages id="message" name="dcm.errors">
            <bean:write name="message" filter="false"/>
        </html:messages>
    </div>
</logic:present>

<logic:present name="org.apache.struts.action.ERROR">
    <div class="error-msg"><html:errors/></div>
</logic:present>
