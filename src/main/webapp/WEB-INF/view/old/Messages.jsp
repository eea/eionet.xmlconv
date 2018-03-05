<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:forEach items="${errorMessages}" var="error">
  <div class="error-msg" title="${error}">${error}</div>
</c:forEach>

<c:forEach items="${exceptionMessage}" var="error">
  <div class="error-msg" title="${error}">${error}</div>
</c:forEach>

<c:forEach items="${successMessages}" var="message">
  <div class="system-msg" title="${message}">${message}</div>
</c:forEach>

<c:forEach items="${warningMessages}" var="message">
  <div class="warning-msg" title="${message}">${message}</div>
</c:forEach>
