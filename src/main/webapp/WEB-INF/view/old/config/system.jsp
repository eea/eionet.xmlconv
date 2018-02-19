<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <tiles:insertDefinition name="ConfigTabs">
    <tiles:putAttribute name="selectedTab" value="system"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="System configuration" level="1"/>

  <form:form servletRelativeAction="/config/system" method="post" modelAttribute="form">
    <form:errors path="*" cssClass="error-msg" element="div"/>
    <fieldset class="fieldset">
      <legend><spring:message code="label.config.system.admin"/></legend>
      <div class="row">
        <div class="columns small-4">
          <label for="qaTimeout" class="question"><spring:message code="label.config.system.qa.timeout"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="qaTimeout" maxlength="20" style="width: 30em;" id="qaTimeout"/>
        </div>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label for="cmdXGawk" class="question"><spring:message code="label.config.system.qa.xgawk"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="cmdXGawk" maxlength="255" style="width: 30em;" id="cmdXGawk"/>
        </div>
      </div>
    </fieldset>
    <button type="submit" class="button">
      <spring:message code="label.config.system.save"/>
    </button>

  </form:form>

</div>
