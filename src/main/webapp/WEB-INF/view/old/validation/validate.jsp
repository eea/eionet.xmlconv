<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">

  <ed:breadcrumbs-push label="Validate XML" level="1"/>
  <h1><spring:message code="label.conversion.validate.title"/></h1>

  <c:if test="${requestScope['conversion.valid']}">

    <c:if test="${fn:length(conversion.valid) > 0}">
      <c:if test="${conversion.originalSchema}">
        <div class="ok-msg">The file is valid XML
          <p><spring:message code="label.conversion.originalSchema"/>&#160; <a
                  href="${conversion.originalSchema}">${conversion.originalSchema}</a></p>
          <c:if test="${conversion.validatedSchema}">
            <p><spring:message code="label.conversion.validatedSchema"/>&#160;
              <a href="${conversion.validatedSchema}">${conversion.validatedSchema}</a></p>
          </c:if></div>
      </c:if>
      <c:if test="${conversion.originalSchema}">
        <div class="error-msg">Could not validate XML.
          <p><spring:message code="label.conversion.schema.not.found"/></p>
        </div>
      </c:if>
    </c:if>
    <c:if test="${countErrors != 0}">
      <div class="error-msg">The file is not valid XML
        <c:choose>
          <c:when test="${conversion.originalSchema}">
            <p><spring:message code="label.conversion.originalSchema"/>&#160; <a
                    href="${conversion.originalSchema}">
              ${conversion.originalSchema}</a></p>
            <c:if test="${conversion.validatedSchema}">
              <p><spring:message code="label.conversion.validatedSchema"/>&#160;
                <a href="${conversion.validatedSchema}">${conversion.validatedSchema}</a></p>
            </c:if>
          </c:when>
          <c:otherwise>
            <p><spring:message code="label.conversion.schema.not.found"/></p>
          </c:otherwise>
        </c:choose>
      </div>
    </c:if>
    <c:if test="${conversion.warningMessage}">
      <div class="error-msg">
        ${conversion.warningMessage}
      </div>
    </c:if>
  </c:if>

  <form:form servletRelativeAction="/validation" method="post" modelAttribute="form">
    <table class="datatable">
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.url"/>
        </th>
      </tr>
      <tr>
        <td>
          <form:input path="xmlUrl" type="text" style="width: 40em;"/>
        </td>
      </tr>
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.xmlSchema.optional"/>
        </th>
      </tr>
      <tr>
        <td>
          <spring:message code="label.conversion.validate.note"/>
        </td>
      </tr>
      <tr>
        <td>
          <input type="text" property="schemaUrl" style="width: 40em;"/>
        </td>
      </tr>
        <%--<c:if equal name="form" property="showSchemaSelection" value="true">--%>
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.xmlSchema"/>
        </th>
      </tr>
      <tr>
        <td>
          <spring:message code="label.conversion.selectSchema"/>
        </td>
      </tr>
      <tr>
        <td>

            <%--                    <form:select path="showSchemaSelection" name="showSchemaSelection" property="schemaUrl"  size="10">
                                    <form:option value="">--</form:option>
                                    <form:options collection="conversion.schemas" property="schema" labelProperty="label" />
                                </form:select>--%>
        </td>
      </tr>
        <%--</c:if equal>--%>
      <tr>
        <td align="center">
          <button type="submit" class="button">
            <spring:message code="label.conversion.validate"/>
          </button>
        </td>
      </tr>
    </table>
  </form:form>
  <c:if test="${requestScope['conversion.valid']}">

    <c:if test="${fn:length(conversion.valid) != 0}">
      <table class="datatable" align="center" width="100%">
        <col style="width:8%"/>
        <col style="width:8%"/>
        <col style="width:8%"/>
        <col/>
        <thead>
        <tr>
          <th scope="col"><span title="Error"><spring:message code="label.table.conversion.type"/></span></th>
          <th scope="col"><span title="PositionLine"><spring:message code="label.table.conversion.line"/></span></th>
          <th scope="col"><span title="PositionCol"><spring:message code="label.table.conversion.col"/></span></th>
          <th scope="col"><span title="Message"><spring:message code="label.table.conversion.message"/></span></th>
        </tr>
        </thead>
        <tbody>
          <%--id="valid" name="conversion.valid" scope="request" type="ValidateDto">--%>
        <c:forEach varStatus="index" items="${conversion.valid}" var="valid">
          <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
            <td>
              ${valid.type}
            </td>
            <td>
              ${valid.line}
            </td>
            <td>
              ${valid.column}
            </td>
            <td>
              ${valid.description}
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:if>
  </c:if>
</div>
