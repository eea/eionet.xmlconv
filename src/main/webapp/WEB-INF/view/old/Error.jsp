<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:choose>
  <c:when test="${!empty exceptionMessage}">
    <div class="error-msg">500 - ${exceptionMessage}</div>
  </c:when>
  <c:otherwise>
    <div class="error-msg">${status} - ${reason}</div>
  </c:otherwise>
</c:choose>

