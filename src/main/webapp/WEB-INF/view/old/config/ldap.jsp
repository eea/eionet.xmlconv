<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <tiles:insertDefinition name="ConfigTabs">
    <tiles:putAttribute name="selectedTab" value="ldap"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="LDAP configuration" level="1"/>

  <form:form action="/config/ldap" method="post" modelAttribute="form">
    <fieldset class="fieldset">
      <legend><spring:message code="label.config.ldap.admin"/></legend>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="url"><spring:message code="label.config.ldap.url"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="url" maxlength="255" style="width: 30em;" id="url"/>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="context"><spring:message code="label.config.ldap.context"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="context" maxlength="255" style="width: 30em;" id="context"/>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="userDir"><spring:message code="label.config.ldap.userDir"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="userDir" maxlength="255" style="width: 30em;" id="userDir"/>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label class="question" for="attrUid"><spring:message code="label.config.ldap.attrUid"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="attrUid" maxlength="255" style="width: 30em;" id="attrUid"/>
        </div>
      </div>
    </fieldset>
    <button type="submit" class="button">
      <spring:message code="label.config.ldap.save"/>
    </button>
  </form:form>
</div>
