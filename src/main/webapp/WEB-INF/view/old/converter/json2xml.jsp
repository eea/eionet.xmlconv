<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width: 100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="json2xml"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Json to Xml" level="1"/>
  <h1>
    <spring:message code="label.conversion.json2xml.title"/>
  </h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <form:form servletRelativeAction="json2xml" method="post">
    <div>
      <label for="param">
        <spring:message code="label.conversion.json2xml.source"/>
      </label>
      <textarea name="json" id="param" rows="10" cols="100"></textarea>
      <input type="submit" class="button" value="Convert"/>
    </div>
  </form:form>
</div>
