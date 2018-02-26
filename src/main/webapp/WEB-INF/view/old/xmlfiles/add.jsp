<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Upload XML File" level="1"/>
<h1><spring:message code="label.title.uplXmlFile.add"/></h1>

<form:form action="/xmlFiles" method="post" enctype="multipart/form-data" modelAttribute="form">
  <form:errors path="*" element="div" cssClass="error-msg"/>
  <fieldset class="fieldset">
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtTitle">
          <spring:message code="label.uplXmlFile.title"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input path="title" style="width:400px" id="txtTitle"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question required" for="fileXml">
          <spring:message code="label.uplXmlFile.xmlfile"/>
        </label>
      </div>
      <div class="columns small-8">
        <input type="file" name="xmlFile" id="fileXml" size="68"/>
      </div>
    </div>
  </fieldset>
  <button type="submit" class="button" name="add">
    <spring:message code="label.uplXmlFile.upload"/>
  </button>
</form:form>
