<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Update QA script from original location" level="3"/>

<h1 class="documentFirstHeading">
  <spring:message code="label.syncuplscript.title"/>
</h1>




<form:form action="/syncUplScript" method="post">
  <form:errors path="*" cssClass="error-msg" element="div"/>

  <p>
    Do you want to store the remote script as a local copy?
  </p>
  <div>
    <c:if test="${user}">
      <button type="submit" class="button" name="update">
        <spring:message code="label.uplSchema.updatecopy"/>
      </button>
      <%--<html:cancel styleClass="button">
        <spring:message code="label.cancel"/>
      </html:cancel>--%>
    </c:if>
  </div>
  <p>
    File downloaded from: ${SyncUplScriptForm.url}
  </p>
  <pre>${SyncUplScriptForm.scriptFile}</pre>

  <div style="display:none">
    <form:hidden path="scriptId"/>
    <form:hidden path="scriptFile"/>
    <form:hidden path="fileName"/>
    <form:hidden path="url"/>
  </div>
</form:form>
<div class="visualClear">&nbsp;</div>





