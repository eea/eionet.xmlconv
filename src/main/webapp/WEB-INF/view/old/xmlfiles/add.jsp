<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Upload XML File" level="1"/>
<h1><spring:message code="label.title.uplXmlFile.add"/></h1>


<form:form action="/xmlFiles/add" method="post" enctype="multipart/form-data" modelAttribute="form">
  <table class="formtable">
    <col class="labelcol"/>
    <col class="entrycol"/>
    <tr class="zebraeven">
      <td>
        <label class="question required" for="fileXml">
          <spring:message code="label.uplXmlFile.xmlfile"/>
        </label>
      </td>
      <td>
        <input type="file" name="xmlFile.file" id="fileXml" size="68"/>
      </td>
    </tr>
    <tr>
      <td>
        <label class="question" for="txtTitle">
          <spring:message code="label.uplXmlFile.title"/>
        </label>
      </td>
      <td>
        <form:input path="title" style="width:400px" styleId="txtTitle"/>
      </td>
    </tr>
    <tr>
      <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <button type="submit" class="button" name="action" value="upload">
          <spring:message code="label.uplXmlFile.upload"/>
        </button>
          <%--<html:cancel styleClass="button">
            <spring:message code="label.cancel"/>
          </html:cancel>--%>
      </td>
    </tr>
  </table>
</form:form>
