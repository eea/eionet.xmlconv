<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width: 100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="json2xml"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Json to Xml" level="1"/>

  <form:form servletRelativeAction="/converter/json2xml" method="post" modelAttribute="form">
    <form:errors path="*" cssClass="error-msg" element="div"/>
    <fieldset>
      <legend><spring:message code="label.conversion.json2xml.title"/></legend>
      <div class="row">
        <label for="param"><spring:message code="label.conversion.json2xml.source"/></label>
        <form:textarea path="content" id="param" rows="10" cols="100"></form:textarea>
        <button type="submit" class="button">
          <spring:message code="label.conversion.convert"/>
        </button>
      </div>
    </fieldset>
  </form:form>

  <c:if test="${!empty xml}">
    <pre><c:out value="${xml}"/></pre>
  </c:if>
</div>
