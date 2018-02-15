<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Edit Host" level="2"/>

<form:form action="/hosts" method="post" modelAttribute="form">
  <fieldset class="fieldset">
    <legend><spring:message code="label.hosts.edit"/></legend>
    <div class="row">
      <div class="columns small-4">
        <label class="question required" for="txtHost">
          <spring:message code="label.hosts.host"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input path="host" type="url" id="txtHost"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question required" for="txtUsername">
          <spring:message code="label.hosts.username"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input path="username" id="txtUsername"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtPassword">
          <spring:message code="label.hosts.password"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input path="password" id="txtPassword"/>
      </div>
    </div>
    <button type="submit" class="button" name="update" title="Save">
      <spring:message code="label.save"/>
    </button>
  </fieldset>
  <form:hidden path="id" />
</form:form>