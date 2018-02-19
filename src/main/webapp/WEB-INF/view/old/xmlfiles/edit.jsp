<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Edit XML File" level="2"/>
<h1><spring:message code="label.title.uplXmlFile.edit"/></h1>

<form:form servletRelativeAction="/xmlFiles" method="post" enctype="multipart/form-data" modelAttribute="form">
  <form:errors path="*" cssClass="error-msg" element="div"/>
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
        <label class="question">
          <spring:message code="label.uplXmlFile.xmlfile"/>
        </label>
      </div>
      <div class="columns small-8">
        <a href="/xmlfile/${form.xmlFileName}" title="${form.xmlFileName}">
            ${form.xmlFileName}
        </a>&#160;&#160;
        (<spring:message code="label.lastmodified"/>:
        <c:choose>
          <c:when test="${!empty form.lastModified}">
            ${form.lastModified}
          </c:when>
          <c:otherwise>
            <span style="color:red"><spring:message code="label.fileNotFound"/></span>
          </c:otherwise>
        </c:choose>)
        <form:hidden path="xmlfileId"/>
        <form:hidden path="xmlFileName"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4"></div>
      <div class="columns small-8">
        <input type="file" name="xmlFile" styleId="fileXml" size="68"/>
      </div>
    </div>
  </fieldset>
  <button class="button" type="submit" name="update">
    <spring:message code="label.ok"/>
  </button>
</form:form>
