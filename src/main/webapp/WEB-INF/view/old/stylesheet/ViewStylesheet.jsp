<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties" %>--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="View stylesheet" level="3"/>

<div id="operations">
  <ul>
    <li>
      <a href="searchCR?conversionId=<bean:write name="stylesheetForm" property="stylesheetId" />&amp;schemaUrl=<bean:write name="stylesheetForm" property="schema" />">
        <spring:message code="label.stylesheet.run"/>
      </a>
    </li>
    <c:if test="${stylesheet.permissions == 'ssdPrm'}">
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
    </c:if>
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
      <c:if test="${stylesheetForm.schemas}">
        <%--id="relatedSchema" name="stylesheetForm" property="schemas" type="Schema">--%>
        <c:forEach varStatus="index" items="${stylesheetForm.schemas}">
          <a href="schemaStylesheets?schema=<bean:write name="relatedSchema" property="schema" />"
             title="view XML Schema stylesheets"><bean:write name="relatedSchema" property="schema"/></a>
          <br/>
        </c:forEach>
      </c:if>
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


  <c:if test="${stylesheetForm.showDependsOnInfo == true}">
    <bean:define id="depOn" name="stylesheetForm" property="dependsOn" scope="request" type="java.lang.String"/>
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.stylesheet.dependsOn"/>
      </th>
      <td>
        <%--id="st" scope="request" name="stylesheetForm" property="existingStylesheets" type="Stylesheet">--%>
        <c:forEach items="${stylesheetForm.existingStylesheets}">
          <c:if test="${st.convId = depOn}">
            <a href="stylesheetViewForm?stylesheetId=<bean:write name="st" property="convId" />"
               title="Open depending stylesheet page">
              <bean:write name="st" property="xslFileName"/>
            </a>
          </c:if>
        </c:forEach>
      </td>
    </tr>

  </c:if>


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
          <c:choose>
            <c:when test="${stylesheetForm.modified}">
              <bean:write property="modified" name="stylesheetForm"/>
            </c:when>
            <c:otherwise>
              <span style="color:red"><spring:message code="label.fileNotFound"/></span>
            </c:otherwise>
          </c:choose>)</span>
    </td>
  </tr>
</table>
<c:if test="${stylesheetForm.xslFileName}">
  <pre><bean:write name="stylesheetForm" property="xslContent"/></pre>
</c:if>
