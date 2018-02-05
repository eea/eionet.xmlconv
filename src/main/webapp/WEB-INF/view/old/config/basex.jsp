<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <tiles:insertDefinition name="ConfigTabs">
    <tiles:putAttribute name="selectedTab" value="basex"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="BaseX Server configuration" level="1"/>

  <form:form servletRelativeAction="/config/basex" method="post" modelAttribute="form">
    <fieldset class="fieldset">
      <legend><spring:message code="label.config.basexserver.title"/></legend>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="host"><spring:message code="label.config.basexserver.host"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="host" maxlength="255" style="width: 30em;" id="host"/>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="port"><spring:message code="label.config.basexserver.port"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="port" maxlength="255" style="width: 30em;" id="port"/>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="user"><spring:message code="label.config.basexserver.user"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="user" maxlength="255" style="width: 30em;" id="user"/>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="password"><spring:message code="label.config.basexserver.password"/></label>
        </div>
        <div class="columns small-8">
          <form:password path="password" maxlength="255" style="width: 30em;" id="password"/>
        </div>
      </div>
    </fieldset>
    <button class="button" name="action" value="submit">
      <spring:message code="label.config.basexserver.update"/>
    </button>

  </form:form>
</div>
