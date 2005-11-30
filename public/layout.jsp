<%@ page buffer="100kb" pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
%>

<%-- include header --%>
<tiles:insert attribute="header"/>

<%-- include Error display --%>
<tiles:insert definition="Error" />
    
<%-- include body --%>
<tiles:insert attribute="body"/>
    
<tiles:useAttribute id="showFooter" name="showFooter"/>
<%-- include footer --%>
<tiles:insert attribute="footer">
	<tiles:put name="showFooter" beanName="showFooter" />
</tiles:insert>
