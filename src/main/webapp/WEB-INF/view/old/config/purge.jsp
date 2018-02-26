<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>


<div style="width:100%;">
  <tiles:insertDefinition name="ConfigTabs">
    <tiles:putAttribute name="selectedTab" value="purge"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Purge" level="1"/>

  <form:form action="/config/purge" method="post" modelAttribute="form">
    <form:errors path="*" cssClass="error-msg" element="div"/>
    <fieldset class="fieldset">
      <legend><spring:message code="label.config.purge.title"/></legend>
      <div class="row">
        <div class="columns small-4">
          <label for="nofDays" class="question"><spring:message code="label.config.purge.nofdays"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="nofDays" maxlength="10" style="width: 10em;" id="nofDays"/>
        </div>
      </div>
    </fieldset>
    <button type="submit" class="button">
      <spring:message code="label.config.purge.submit"/>
    </button>

  </form:form>

</div>
