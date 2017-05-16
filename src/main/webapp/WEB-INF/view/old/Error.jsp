<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:forEach items="${errorMessages}" var="error">
  <%--<spring:message code="${error}" var="${tmpLabel}" />--%>
  <div class="error-msg" title="${error}">${error}</div>
</c:forEach>

<c:forEach items="${exceptionMessage}" var="error">
  <%--<spring:message code="${message}" var="${tmpLabel}" />--%>
  <div class="error-msg" title="${error}">${error}</div>
</c:forEach>

<c:forEach items="${successMessages}" var="message">
  <%--<spring:message code="${message}" var="${tmpLabel}" />--%>
  <div class="system-msg" title="${message}">${message}</div>
</c:forEach>

<%--<div><form:errors cssClass="error-msg"/></div>--%>
