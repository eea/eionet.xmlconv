<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Upload Schema" level="2"/>
<%--<h1><spring:message code="label.title.uplSchema.add"/></h1>--%>

<form:form servletRelativeAction="/schemas/add" method="post" enctype="multipart/form-data" modelAttribute="schemaForm">
  <fieldset class="fieldset">
    <legend><spring:message code="label.title.uplSchema.add"/></legend>
    <div class="row">
      <div class="columns small-4">
        <label class="question required" for="txtSchemaUrl">
          <spring:message code="label.schema.url"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:input path="schemaUrl" maxlength="255" style="width:500px" id="txtSchemaUrl"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtDescription">
          <spring:message code="label.schema.description"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:textarea path="description" rows="2" cols="30" style="width:500px" id="txtDescription"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtSchemaFile">
          <spring:message code="label.schema.add.file"/>
        </label>
      </div>
      <div class="columns small-8">
        <input type="file" name="schemaFile" size="50" style="width:500px" id="txtSchemaFile"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtSchemaLang">
          <spring:message code="label.schema.language"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:select path="schemaLang" styleId="txtSchemaLang" value="XSD">
          <form:options items="${schemaForm.schemaLanguages}"/>
        </form:select>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtValidation">
          <spring:message code="label.schema.dovalidation"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:checkbox path="doValidation" id="txtValidation"/>
      </div>
    </div>
    <div class="row">
      <div class="columns small-4">
        <label class="question" for="txtBlockerValidation">
          <spring:message code="label.schema.isBlockerValidation"/>
        </label>
      </div>
      <div class="columns small-8">
        <form:checkbox path="blockerValidation" id="txtBlockerValidation"/>
      </div>
    </div>
  </fieldset>
  <button type="submit" class="button" name="add">
    <spring:message code="label.schema.save"/>
  </button>
</form:form>
