<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<tiles:insertDefinition name="ConfigTabs">
  <tiles:putAttribute name="selectedTab" value="database"/>
</tiles:insertDefinition>

<ed:breadcrumbs-push label="DB configuration" level="1"/>

<form:form servletRelativeAction="/config/database" method="post" modelAttribute="form">
  <form:errors path="*" cssClass="error-msg" element="div"/>
  <fieldset class="fieldset">
    <legend><spring:message code="label.config.db.admin"/></legend>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="dbUrl"><spring:message code="label.config.db.url"/></label>
      </div>
      <div class="columns small-8">
        <form:input path="url" maxlength="255" id="dbUrl"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="user"><spring:message code="label.config.db.user"/></label>
      </div>
      <div class="columns small-8">
        <form:input path="user" maxlength="255" style="width: 30em;" styleId="user"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="password"><spring:message code="label.config.db.password"/></label>
      </div>
      <div class="columns small-8">
        <form:password showPassword="false" path="password" maxlength="255" style="width: 30em;" styleId="password"/>
      </div>
    </div>
  </fieldset>
  <button type="submit" class="button">
    <spring:message code="label.config.db.update"/>
  </button>

</form:form>
