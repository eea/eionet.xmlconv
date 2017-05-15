<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="View stylesheet" level="3"/>

<div id="operations">
  <ul>
    <li>
      <a href="searchCR?conversionId=${stylesheetForm.stylesheetId}&amp;schemaUrl=${stylesheetForm.schema}">
        <spring:message code="label.stylesheet.run"/>
      </a>
    </li>
    <c:if test="${stylesheet.ssdPrm}">
      <li>
          <%--paramId="stylesheetId" paramName="stylesheetForm" paramProperty="stylesheetId"--%>
        <html:link page="/conversions/${stylesheetId}/edit" title="edit stylesheet">
          <spring:message code="label.stylesheet.edit"/>
        </html:link>
      </li>
      <li>
        <a href="deleteStylesheet?conversionId=${stylesheetForm.stylesheetId}&amp;schema=${stylesheetForm.schema} title="delete stylesheet">
          <spring:message code="label.stylesheet.delete"/>
        </a>
      </li>
    </c:if>
  </ul>
</div>


<h1><spring:message code="label.stylesheet.view"/></h1>




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
        <c:forEach varStatus="index" items="${stylesheetForm.schemas}" var="relatedSchema">
          <a href="schemaStylesheets?schema=${relatedSchema.schema}" title="view XML Schema stylesheets">
            ${relatedSchema.schema}
          </a>
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
      ${stylesheetForm.outputtype}
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
            <a href="stylesheetViewForm?stylesheetId=${st.convId}" title="Open depending stylesheet page">
              ${st.xslFileName}
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
      ${stylesheetForm.description}
    </td>
  </tr>
  <tr>
    <th scope="row" class="scope-row">
      <spring:message code="label.stylesheet.xslfile"/>
    </th>
    <td>
      <a href="${webRoot}/${stylesheetForm.xsl}" title="${stylesheetForm.xsl}" class="link-xsl">
        ${stylesheetForm.xslFileName}
      </a>
      <span style="margin-left:10px">(<spring:message code="label.lastmodified"/>:
          <c:choose>
            <c:when test="${stylesheetForm.modified}">
              ${stylesheetForm.modified}
            </c:when>
            <c:otherwise>
              <span style="color:red"><spring:message code="label.fileNotFound"/></span>
            </c:otherwise>
          </c:choose>)</span>
    </td>
  </tr>
</table>
<c:if test="${stylesheetForm.xslFileName}">
  <pre>${stylesheetForm.xslContent}</pre>
</c:if>
