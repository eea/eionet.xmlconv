<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>

<%--<html:xhtml/>--%>
<ed:breadcrumbs-push label="Edit XML File" level="2"/>
<h1><spring:message code="label.title.uplXmlFile.edit"/></h1>

<%-- include Error display --%>
<tiles:insertDefinition name="Error"/>

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
        <a href="<bean:write name="EditUplXmlFileForm" property="xmlFilePath" /><bean:write name="EditUplXmlFileForm" property="xmlFileName" />"
           title="<bean:write name="EditUplXmlFileForm" property="xmlFileName" />">
          <bean:write name="EditUplXmlFileForm" property="xmlFileName"/>
        </a>&#160;&#160;
        (<spring:message code="label.lastmodified"/>:
        <c:if present name="EditUplXmlFileForm" property="lastModified">
          <bean:write property="lastModified" name="EditUplXmlFileForm"/>
        </c:if present>
        <c:if equal value="" name="EditUplXmlFileForm" property="lastModified">
          <span style="color:red"><spring:message code="label.fileNotFound"/></span>
        </c:if equal>
        )
        <html:hidden property="xmlfileId"/>
        <html:hidden property="xmlFileName"/>
      </td>
    </tr>
    <tr>
      <td>
      </td>
      <td>
        <html:file property="xmlFile" styleId="fileXml" size="68"/>
      </td>
    </tr>
    <tr>
      <td>
        <label class="question" for="txtTitle">
          <spring:message code="label.uplXmlFile.title"/>
        </label>
      </td>
      <td>
        <html:text property="title" style="width:400px" styleId="txtTitle"/>
      </td>
    </tr>
    <tr>
      <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <html:submit styleClass="button">
          <spring:message code="label.ok"/>
        </html:submit>
        <html:cancel styleClass="button">
          <spring:message code="label.cancel"/>
        </html:cancel>
      </td>
    </tr>
  </table>
</form:form>
