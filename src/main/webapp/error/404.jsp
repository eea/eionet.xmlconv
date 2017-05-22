<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%-- include header --%>
<tiles:insertDefinition name="/WEB-INF/tiles/TmpHeader.jsp" />

    <div class="error-msg">
        <c:set var="messageArg"><c:out value="${requestScope['javax.servlet.forward.request_uri']}" /></c:set>
        <spring:message code="label.error.404" arguments="${messageArg}" />
    </div>

<%-- include footer --%>
<tiles:insertDefinition name="/WEB-INF/tiles/TmpFooter.jsp">
    <tiles:putAttribute name="showFooter" type="string" value="true"/>
</tiles:insertDefinition>

