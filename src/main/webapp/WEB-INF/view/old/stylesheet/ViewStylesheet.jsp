<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties" %>--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>

<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="View stylesheet" level="3"/>

<div id="operations">
  <ul>
    <li>
      <a href="searchCR?conversionId=<bean:write name="stylesheetForm" property="stylesheetId" />&amp;schemaUrl=<bean:write name="stylesheetForm" property="schema" />">
        <spring:message code="label.stylesheet.run"/>
      </a>
    </li>
    <c:if equal value="true" name="stylesheet.permissions" property="ssdPrm">
      <li>
          <%--paramId="stylesheetId" paramName="stylesheetForm" paramProperty="stylesheetId"--%>
        <html:link page="/old/conversions/${stylesheetId}/edit" title="edit stylesheet">
          <spring:message code="label.stylesheet.edit"/>
        </html:link>
      </li>
      <li>
        <a href="deleteStylesheet?conversionId=<bean:write name="stylesheetForm" property="stylesheetId" />&amp;schema=<bean:write name="stylesheetForm" property="schema" />"
           title="delete stylesheet"
           onclick='return stylesheetDelete("<bean:write name="stylesheetForm" property="xsl"/>");'>
          <spring:message code="label.stylesheet.delete"/>
        </a>
      </li>
    </c:if equal>
  </ul>
</div>


<h1><spring:message code="label.stylesheet.view"/></h1>

<%-- include Error display --%>
<tiles:insertDefinition name="Error"/>

<table class="datatable">
  <col class="labelcol"/>
  <col class="entrycol"/>
  <tr>
    <th scope="row" class="scope-row">
      <spring:message code="label.stylesheet.schema"/>
    </th>
    <td>
      <c:if present name="stylesheetForm" property="schemas">
        <c:if iterate indexId="index" id="relatedSchema" name="stylesheetForm" property="schemas" type="Schema">
          <a href="schemaStylesheets?schema=<bean:write name="relatedSchema" property="schema" />"
             title="view XML Schema stylesheets"><bean:write name="relatedSchema" property="schema"/></a>
          <br/>
        </c:if iterate>
      </c:if present>
    </td>
  </tr>
  <tr>
    <th scope="row" class="scope-row">
      <spring:message code="label.stylesheet.outputtype"/>
    </th>
    <td>
      <bean:write name="stylesheetForm" property="outputtype"/>
    </td>
  </tr>


  <c:if equal name="stylesheetForm" property="showDependsOnInfo" value="true">

    <bean:define id="depOn" name="stylesheetForm" property="dependsOn" scope="request" type="java.lang.String"/>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.stylesheet.dependsOn"/>
      </th>
      <td>
        <c:if iterate id="st" scope="request" name="stylesheetForm" property="existingStylesheets" type="Stylesheet">
          <c:if equal name="st" property="convId" value="<%=depOn %>">
            <a href="stylesheetViewForm?stylesheetId=<bean:write name="st" property="convId" />"
               title="Open depending stylesheet page">
              <bean:write name="st" property="xslFileName"/>
            </a>
          </c:if equal>
        </c:if iterate>
      </td>
    </tr>

  </c:if equal>


  <tr>
    <th scope="row" class="scope-row">
      <spring:message code="label.stylesheet.description"/>
    </th>
    <td>
      <bean:write name="stylesheetForm" property="description"/>
    </td>
  </tr>
  <tr>
    <th scope="row" class="scope-row">
      <spring:message code="label.stylesheet.xslfile"/>
    </th>
    <td>
      <a href="<bean:write name="webRoot"/>/<bean:write property="xsl" name="stylesheetForm"/>"
         title="<bean:write property="xsl" name="stylesheetForm"/>" class="link-xsl">
        <bean:write property="xslFileName" name="stylesheetForm"/>
      </a>
      <span style="margin-left:10px">(<spring:message code="label.lastmodified"/>:
                    <c:if present name="stylesheetForm" property="modified">
                      <bean:write property="modified" name="stylesheetForm"/>
                    </c:if present>
                    <c:if notPresent name="stylesheetForm" property="modified">
                      <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                    </c:if notPresent>
                    )</span>
    </td>
  </tr>
</table>
<c:if present name="stylesheetForm" property="xslFileName">
  <pre><bean:write name="stylesheetForm" property="xslContent"/></pre>
</c:if present>
