<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<%--<html:xhtml/>--%>
<ed:breadcrumbs-push label="Edit XML File" level="2"/>
<h1><spring:message code="label.title.uplXmlFile.edit"/></h1>




<form:form action="/editUplXmlFile" method="post" enctype="multipart/form-data">
  <table class="formtable">
    <col class="labelcol"/>
    <col class="entrycol"/>
    <tr class="zebraeven">
      <td>
        <label class="question">
          <spring:message code="label.uplXmlFile.xmlfile"/>
        </label>
      </td>
      <td>
        <a href="${EditUplXmlFileForm.xmlFilePath}${EditUplXmlFileForm.xmlFileName}" title="${EditUplXmlFileForm.xmlFileName}">
          ${EditUplXmlFileForm.xmlFileName}
        </a>&#160;&#160;
        (<spring:message code="label.lastmodified"/>:
        <c:choose>
          <c:when test="${EditUplXmlFileForm.lastModified}">
            ${EditUplXmlFileForm.lastModified}
          </c:when>
          <c:otherwise>
            <span style="color:red"><spring:message code="label.fileNotFound"/></span>
          </c:otherwise>
        </c:choose>)
        <form:hidden path="xmlfileId"/>
        <form:hidden path="xmlFileName"/>
      </td>
    </tr>
    <tr>
      <td>
      </td>
      <td>
        <input type="file" name="xmlFile" styleId="fileXml" size="68"/>
      </td>
    </tr>
    <tr>
      <td>
        <label class="question" for="txtTitle">
          <spring:message code="label.uplXmlFile.title"/>
        </label>
      </td>
      <td>
        <form:input path="title" style="width:400px" id="txtTitle"/>
      </td>
    </tr>
    <tr>
      <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <button type="submit" name="action" value="ok">
          <spring:message code="label.ok"/>
        </button>
        <%--<html:cancel styleClass="button">
          <spring:message code="label.cancel"/>
        </html:cancel>--%>
      </td>
    </tr>
  </table>
</form:form>
