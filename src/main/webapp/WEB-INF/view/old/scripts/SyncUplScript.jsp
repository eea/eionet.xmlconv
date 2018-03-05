<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Update QA script from original location" level="3"/>

<h1 class="documentFirstHeading">
  <spring:message code="label.syncuplscript.title"/>
</h1>

<form:form servletRelativeAction="/scripts/sync" method="post" modelAttribute="form">
  <form:errors path="*" cssClass="error-msg" element="div"/>

  <p>Do you want to store the remote script as a local copy?</p>
  <div>
    <button type="submit" class="button" name="action" value="update">
      <spring:message code="label.uplSchema.updatecopy"/>
    </button>
    <button type="submit" class="button" name="action" value="cancel">
      <spring:message code="label.cancel"/>
    </button>
  </div>
  <p>File downloaded from: ${form.url}</p>
  <pre>${form.scriptFile}</pre>

  <form:hidden path="scriptId"/>
  <form:hidden path="scriptFile"/>
  <form:hidden path="fileName"/>
  <form:hidden path="url"/>
</form:form>






