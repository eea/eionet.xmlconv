<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Stylesheets" level="1"/>

<c:if test="${!empty conversions}">
  <h1 class="documentFirstHeading">
    <spring:message code="label.stylesheet.generated"/>
  </h1>

  <div class="visualClear">&nbsp;</div>

  <div style="width: 97%">
    <table class="datatable results" width="100%">
      <col style="width:7%"/>
      <col style="width:10%"/>
      <col style="width:20%"/>
      <col style="width:10%"/>
      <col style="width:10%"/>
      <col style="width:43%"/>
      <thead>
      <tr>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.action"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.table"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.dataset"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.datasetReleased"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.xmlschema"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.stylesheets"/></th>
      </tr>
      </thead>
      <tbody>
      <c:forEach varStatus="i" items="${conversions.ddStylesheets}" var="conversion">
        <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
          <td align="center">
            <a href="/conversions/generated?schemaUrl=${conversion.schema}">
              <img src="/images/properties.gif" altKey="label.table.stylesheet" title="view stylesheets"/>
            </a>
          </td>
          <td title="${conversion.table}">
              ${conversion.table}
          </td>
          <td title="${conversion.dataset}">
              ${conversion.dataset}
          </td>
          <td title="${conversion.datasetReleased}">
              <fmt:formatDate value="${conversion.datasetReleased}" pattern="dd MMM yyyy" />
          </td>
          <td>
            <a href="${conversion.schema}" title="${conversion.schema}">${conversion.id}</a>
          </td>
          <td>
            <c:forEach items="${conversion.stylesheets}" var="conv">
              <a href="${conv.xsl}" title="${conv.description}">${conv.description}</a>&#160;
            </c:forEach>
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td valign="top" colspan="5">
        </td>
      </tr>
      </tbody>
    </table>
  </div>

</c:if>



