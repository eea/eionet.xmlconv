<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="View stylesheet" level="3"/>

<c:set var="permissions" scope="page" value="${sessionScope['stylesheet.permissions']}"/>

<div id="operations">
  <ul>
    <li>
      <a href="/converter/search?${form.schema}">
        <spring:message code="label.stylesheet.run"/>
      </a>
    </li>
    <c:if test="${permissions.ssdPrm}">
      <li>
        <a href="/conversions/${form.stylesheetId}/edit" title="edit stylesheet">
          <spring:message code="label.stylesheet.edit"/>
        </a>
      </li>
      <li>
          <%--&amp;schema=${form.schema}--%>
        <a href="/conversions/${form.stylesheetId}/delete" title="delete stylesheet">
          <spring:message code="label.stylesheet.delete"/>
        </a>
      </li>
    </c:if>
  </ul>
</div>

<h1><spring:message code="label.stylesheet.view"/></h1>

<table class="datatable results">
  <col class="labelcol"/>
  <col class="entrycol"/>
  <tr>
    <th scope="row" class="scope-row">
      <spring:message code="label.stylesheet.schema"/>
    </th>
    <td>
      <c:if test="${!empty form.schemas}">
        <c:forEach varStatus="i" items="${form.schemas}" var="relatedSchema">
          <a href="/schemas/${relatedSchema.id}/conversions" title="view XML Schema stylesheets">
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
      ${form.outputtype}
    </td>
  </tr>

  <c:if test="${form.showDependsOnInfo}">
    <tr>
      <th scope="row" class="scope-row">
        <spring:message code="label.stylesheet.dependsOn"/>
      </th>
      <td>
        <c:forEach items="${form.existingStylesheets}" var="st">
          <c:if test="${st.convId = form.dependsOn}">
            <a href="/conversions/${st.convId}" title="Open depending stylesheet page">
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
      ${form.description}
    </td>
  </tr>
  <tr>
    <th scope="row" class="scope-row">
      <spring:message code="label.stylesheet.xslfile"/>
    </th>
    <td>
      <a href="/${form.xsl}" title="${form.xsl}" class="link-xsl">
        ${form.xslFileName}
      </a>
      <span style="margin-left:10px">(<spring:message code="label.lastmodified"/>:
          <c:choose>
            <c:when test="${!empty form.modified}">
              ${form.modified}
            </c:when>
            <c:otherwise>
              <span style="color:red"><spring:message code="label.fileNotFound"/></span>
            </c:otherwise>
          </c:choose>)</span>
    </td>
  </tr>
</table>
<c:if test="${!empty form.xslFileName}">
  <pre><c:out value="${form.xslContent}"/></pre>
</c:if>
