<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:choose>
  <c:when test="${!empty exceptionMessage}">
    <div class="error-msg">${status} - <c:out value="${exceptionMessage}" /></div>
  </c:when>
  <c:otherwise>
    <div class="error-msg">${status} - <c:out value="${reason}" /></div>
  </c:otherwise>
</c:choose>

