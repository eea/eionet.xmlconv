<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="Upload Schema" level="2"/>
<h1><spring:message code="label.title.uplSchema.add"/></h1>

<%-- include Error display --%>
<tiles:insertDefinition name="Error"/>

<form:form action="/addUplSchema" method="post" enctype="multipart/form-data">
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
        <html:text property="schemaUrl" maxlength="255" style="width:500px" styleId="txtSchemaUrl"/>
      </td>
    </tr>
    <tr>
      <td>
        <label class="question" for="txtDescription">
          <spring:message code="label.schema.description"/>
        </label>
      </td>
      <td>
        <html:textarea property="description" rows="2" cols="30" style="width:500px" styleId="txtDescription"/>
      </td>
    </tr>
    <tr class="zebraeven">
      <td>
        <label class="question" for="txtSchemaFile">
          <spring:message code="label.schema.add.file"/>
        </label>
      </td>
      <td>
        <html:file property="schemaFile" size="50" style="width:500px" styleId="txtSchemaFile"/>
      </td>
    </tr>
    <tr>
      <td>
        <label class="question" for="txtSchemaLang">
          <spring:message code="label.schema.language"/>
        </label>
      </td>
      <td>
        <html:select property="schemaLang" styleId="txtSchemaLang" value="XSD">
          <html:options property="schemaLanguages"/>
        </html:select>
      </td>
    </tr>
    <tr class="zebraeven">
      <td>
        <label class="question" for="txtValidation">
          <spring:message code="label.schema.dovalidation"/>
        </label>
      </td>
      <td>
        <html:checkbox property="doValidation" styleId="txtValidation"/>
      </td>
    </tr>
    <tr>
      <td>
        <label class="question" for="txtBlockerValidation">
          <spring:message code="label.schema.isBlockerValidation"/>
        </label>
      </td>
      <td>
        <html:checkbox property="blockerValidation" styleId="txtBlockerValidation"/>
      </td>
    </tr>
    <tr>
      <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <html:submit styleClass="button">
          <spring:message code="label.schema.save"/>
        </html:submit>
        <html:cancel styleClass="button">
          <spring:message code="label.cancel"/>
        </html:cancel>
      </td>
    </tr>
  </table>
</form:form>
