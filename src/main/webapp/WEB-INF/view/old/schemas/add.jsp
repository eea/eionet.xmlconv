<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Upload Schema" level="2"/>
<h1><spring:message code="label.title.uplSchema.add"/></h1>

<form:form servletRelativeAction="/schemas/add" method="post" enctype="multipart/form-data" modelAttribute="schemaForm">
  <table class="formtable">
    <col class="labelcol"/>
    <col class="entrycol"/>
    <tr class="zebraeven">
      <td>
        <label class="question required" for="txtSchemaUrl">
          <spring:message code="label.schema.url"/>
        </label>
      </td>
      <td>
        <form:input path="schemaUrl" maxlength="255" style="width:500px" id="txtSchemaUrl"/>
      </td>
    </tr>
    <tr>
      <td>
        <label class="question" for="txtDescription">
          <spring:message code="label.schema.description"/>
        </label>
      </td>
      <td>
        <form:textarea path="description" rows="2" cols="30" style="width:500px" id="txtDescription"/>
      </td>
    </tr>
    <tr class="zebraeven">
      <td>
        <label class="question" for="txtSchemaFile">
          <spring:message code="label.schema.add.file"/>
        </label>
      </td>
      <td>
        <input type="file" name="schemaFile" size="50" style="width:500px" id="txtSchemaFile"/>
      </td>
    </tr>
    <tr>
      <td>
        <label class="question" for="txtSchemaLang">
          <spring:message code="label.schema.language"/>
        </label>
      </td>
      <td>
        <form:select path="schemaLang" styleId="txtSchemaLang" value="XSD">
          <form:options items="${schemaForm.schemaLanguages}"/>
        </form:select>
      </td>
    </tr>
    <tr class="zebraeven">
      <td>
        <label class="question" for="txtValidation">
          <spring:message code="label.schema.dovalidation"/>
        </label>
      </td>
      <td>
        <form:checkbox path="doValidation" id="txtValidation"/>
      </td>
    </tr>
    <tr>
      <td>
        <label class="question" for="txtBlockerValidation">
          <spring:message code="label.schema.isBlockerValidation"/>
        </label>
      </td>
      <td>
        <form:checkbox path="blockerValidation" id="txtBlockerValidation"/>
      </td>
    </tr>
    <tr>
      <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <button type="submit" class="button" value="submit">
          <spring:message code="label.schema.save"/>
        </button>
        <button type="submit" class="button" value="cancel">
          <spring:message code="label.cancel"/>
        </button>
      </td>
    </tr>
  </table>
</form:form>
