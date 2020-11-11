<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="XML Schemas" level="1"/>

<c:if test="${schemas.ssiPrm}">
  <div id="operations">
    <ul>
      <li><a href="/schemas/add"><spring:message code="label.uplSchema.add"/></a></li>
    </ul>
  </div>
</c:if>

<c:if test="${!empty schemas}">

  <h1 class="documentFirstHeading">
    <spring:message code="label.schemas.uploaded"/>
  </h1>

  <c:if test="${!empty schemas.schemas}">
    <form:form servletRelativeAction="/schemas" method="post" modelAttribute="form">
      <form:errors path="*" cssClass="error-msg" element="div"/>
      <table class="datatable results" width="100%">
        <c:if test="${schemas.ssdPrm}">
          <col style="width:5%"/>
        </c:if>
        <col/>
        <col/>
        <col style="width:20px"/>
        <col style="width:20px"/>
        <col style="width:20px"/>
        <thead>
        <tr>
          <c:if test="${schemas.ssdPrm}">
            <th scope="col"></th>
          </c:if>
          <th scope="col"><span title="Schema"><spring:message code="label.table.uplSchema.schema"/></span></th>
          <th scope="col"><span title="Description"><spring:message code="label.table.uplSchema.description"/></span></th>
          <th scope="col" title="Uploaded schemas">XSD</th>
          <th scope="col" title="Stylesheets">XSL</th>
          <th scope="col" title="QA scripts">QA</th>
          <th scope="col" title="Max execution time">MAX EXECUTION TIME (ms)</th>
        </tr>
        </thead>
        <tbody>
          <%--indexId="index" id="schema" name="schemas.uploaded" property="schemas" type="Schema">--%>
        <c:forEach items="${schemas.schemas}" var="schema" varStatus="i">
          <tr class="${i.index % 2 == 1 ? "class=\"zebraeven\"" : "class=\"zebraodd\""}">
            <c:if test="${schemas.ssdPrm}">
              <td align="center">
                <form:radiobutton path="schemaId" value="${schema.id}"/>
              </td>
            </c:if>
            <td>
              <a href="/schemas/${schema.id}" title="view XML Schema properties">
                  ${schema.schema}
              </a>
            </td>
            <td>
                ${schema.description}
            </td>
            <td align="center">
              <c:if test="${!empty schema.uplSchemaFileName}">
                <%--${webRoot}--%>
                <a href="/schema/${schema.uplSchemaFileName}" class="link-xsd"
                   title="Open uploaded schema file"></a>
              </c:if>
            </td>
            <td>
              <c:if test="${schema.countStylesheets > 0}">
                <a href="/schemas/${schema.id}/conversions"
                   title="View schema stylesheets (${schema.countStylesheets})"
                   class="link-xsl"></a>
              </c:if>
            </td>
            <td>
                <%--<bean:write name="schema" property="id" />" title="View schema QA scripts (<bean:write name="schema" property="countQaScripts" />)"--%>
              <c:if test="${schema.countQaScripts > 0}">
                <a href="/schemas/${schema.id}/scripts" class="link-xquery"></a>
              </c:if>
            </td>
            <td>
                ${schema.maxExecutionTime}
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>

      <div class="row">
        <c:if test="${schemas.ssdPrm}">
          <button type="submit" class="button" name="delete">
            <spring:message code="label.schema.delete"/>
          </button>
        </c:if>
      </div>

    </form:form>
  </c:if>

  <c:if test="${empty schemas.schemas}">
    <div class="advice-msg">
      <spring:message code="label.uplSchema.noSchemas"/>
    </div>
  </c:if>

</c:if>

