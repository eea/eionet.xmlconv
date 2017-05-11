<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%-- include header --%>
<tiles:insertDefinition name="/WEB-INF/tiles/TmpHeader.jsp" />

    <div class="error-msg"><spring:message code="label.error.500" /></div>
<%-- include footer --%>
<tiles:insertDefinition name="/WEB-INF/tiles/TmpFooter.jsp">
    <tiles:putAttribute name="showFooter" type="string" value="true"/>
</tiles:insertDefinition>

