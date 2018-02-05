<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">

  <ed:breadcrumbs-push label="Validate XML" level="1"/>

  <c:if test="${requestScope['validationErrors'] != null}">
    <c:if test="${warningMessage}">
      <div class="error-msg">
          ${warningMessage}
      </div>
    </c:if>
    <c:choose>
      <c:when test="${fn:length(validationErrors) eq 0 and !empty originalSchema}">
        <div class="ok-msg">The file is valid XML
          <c:choose>
            <c:when test="${!empty validatedSchema}">
              <p>
                <spring:message code="label.conversion.validatedSchema"/>&#160;
                <a href="${validatedSchema}">${validatedSchema}</a></p>
            </c:when>
            <c:otherwise>
              <p>
                <spring:message code="label.conversion.originalSchema"/>&#160;
                <a href="${originalSchema}">${originalSchema}</a>
              </p>
            </c:otherwise>
          </c:choose>
        </div>
      </c:when>
      <c:otherwise>
        <div class="error-msg">The file is not valid XML
          <c:choose>
            <c:when test="${!empty originalSchema}">
              <c:choose>
                <c:when test="${!empty validatedSchema}">
                  <p><spring:message code="label.conversion.validatedSchema"/>&#160;
                    <a href="${validatedSchema}">${validatedSchema}</a></p>
                </c:when>
                <c:otherwise>
                  <p>
                    <spring:message code="label.conversion.originalSchema"/>&#160;
                    <a href="${originalSchema}">${originalSchema}</a>
                  </p>
                </c:otherwise>
              </c:choose>
            </c:when>
            <c:otherwise>
              <div class="error-msg">Could not validate XML.
                <p><spring:message code="label.conversion.schema.not.found"/></p>
              </div>
            </c:otherwise>
          </c:choose>
        </div>
      </c:otherwise>
    </c:choose>
  </c:if>

  <form:form servletRelativeAction="/validation" method="post" modelAttribute="form">
    <fieldset class="fieldset">
      <legend><spring:message code="label.conversion.validate.title"/></legend>
      <div class="row">
        <div class="columns small-4">
          <label><spring:message code="label.conversion.url"/></label>
        </div>
        <div class="columns small-8">
          <form:input path="xmlUrl" type="text" />
        </div>
      </div>
      <div class="row">
        <div class="columns small-12">
          <label><spring:message code="label.conversion.xmlSchema.optional"/></label>
        </div>
      </div>
      <div class="row">
        <div class="columns small-12">
          <label><spring:message code="label.conversion.validate.note"/></label>
        </div>
      </div>
      <div class="row">
        <div class="columns small-12">
          <input type="text" property="schemaUrl" />
        </div>
      </div>
<%--      <div class="row">
        <div class="columns small-12">
          <label><spring:message code="label.conversion.xmlSchema"/></label>
        </div>
      </div>--%>
      <%--<c:if test="${form.showSchemaSelection}">--%>
        <%--<div class="row">--%>
          <%--<div class="columns small-12">--%>
            <%--<label><spring:message code="label.conversion.selectSchema"/></label>--%>
          <%--</div>--%>
        <%--</div>--%>
        <%--<div class="row">--%>
          <%--<form:select path="showSchemaSelection" name="showSchemaSelection" property="schemaUrl"  size="10">--%>
            <%--<form:option value="">--</form:option>--%>
            <%--<form:options collection="conversion.schemas" property="schema" labelProperty="label" />--%>
          <%--</form:select>--%>
        <%--</div>--%>
      <%--</c:if>--%>
    </fieldset>
    <div class="row">
      <div class="columns small-4">
        <button type="submit" class="button">
          <spring:message code="label.conversion.validate"/>
        </button>
      </div>
    </div>
  </form:form>
  <c:if test="${!empty validationErrors}">
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
      <c:forEach varStatus="index" items="${validationErrors}" var="error">
        <tr>
          <td>${error.type}</td>
          <td>${error.line}</td>
          <td>${error.column}</td>
          <td>${error.description}</td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </c:if>
</div>
