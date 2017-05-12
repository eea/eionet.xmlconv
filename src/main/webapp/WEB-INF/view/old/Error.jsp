<%--<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java" %>
<%@ page import="eionet.gdem.utils.Utils,java.util.Date" %>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<%--<c:if test="${errors}">--%>
<c:forEach items="${errors}" varStatus="index" var="i">
  <spring:message code="${i}" var="${tmpLabel}" />
  <div class="system-msg" title="${tmpLabel}" />
</c:forEach>

<%--<c:if test="${dcm.messages}">--%>
<c:forEach items="${dcm.messages}" varStatus="index" var="i">
  <spring:message code="${i}" var="${tmpLabel}" />
  <div class="system-msg" title="${tmpLabel}" />
</c:forEach>

<%--<c:if test="${dcm.errors}">--%>
<c:forEach items="${dcm.errors}" varStatus="index" var="i">
  <spring:message code="${i}" var="${tmpLabel}" />
  <div class="error-msg" title="${tmpLabel}" />
</c:forEach>
<%--</c:if>--%>

<%--<c:if test="${errors}">--%>
<div><form:errors cssClass="error-msg"/></div>
<%--</c:if>--%>
