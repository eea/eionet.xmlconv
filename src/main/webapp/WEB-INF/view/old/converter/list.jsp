<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="convertXML"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Convert XML" level="1"/>
  <h1><spring:message code="label.conversion.find"/></h1>

  <form:form servletRelativeAction="/converter" method="post" modelAttribute="conversionForm">
    <table class="datatable">
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.url"/>
        </th>
      </tr>
      <tr>
        <td>
          <spring:message code="label.conversion.insertURL"/>
        </td>
      </tr>
      <tr>
        <td>
          <form:input path="url" style="width: 40em;"/>
        </td>
      </tr>
      <c:if test="${conversionForm.showSchemaSelection}">
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
            <form:select path="schemaUrl" size="10">
              <form:option value="">--</form:option>
              <form:options items="conversion.schemas" path="schema" labelProperty="label"/>
            </form:select>
          </td>
        </tr>
      </c:if>
      <tr>
        <td align="center">
          <spring:message code="label.conversion.list" var="conversionListLabel"/>
          <button type="submit" name="action" value="searchAction" class="button" title="${conversionListLabel}">
            ${conversionListLabel}
          </button>
        </td>
      </tr>
    </table>
    <c:if test="${!empty conversionForm.action}">
      <table class="datatable">
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.selectConversion"/>
        </th>
      </tr>

        <%--<bean:define id="idConv" name="converted.conversionId" scope="session" type="String"/>--%>
        <%--<bean:define id="idConv" name="conversionForm" property="conversionId" scope="session" type="String"/>--%>

        <%--id="schema" name="conversionForm" scope="session" property="schemas" type="Schema">--%>
        <c:forEach items="${conversionForm.schemas}" var="schema">
          <tr>
            <td align="left">
              <strong>${schema.schema}</strong>
              <br/>
                <%--id="stylesheet" name="schema" property="stylesheets" type="Stylesheet">--%>
              <c:forEach varStatus="v" items="${schema.stylesheets}" var="stylesheet">
                <c:if test="${stylesheet.convId == conversionForm.conversionId}">
                  <input type="radio" checked="checked" name="conversionId"
                         id="r_${stylesheet.convId}"
                         value="${stylesheet.convId}"/>
                </c:if>
                <c:if test="${stylesheet.convId != conversionForm.conversionId}">
                  <input type="radio" name="conversionId" id="r_${stylesheet.convId}" value="${stylesheet.convId}"/>
                </c:if>
                <label for="r_${stylesheet.convId}">${stylesheet.type}&nbsp;-&nbsp;${stylesheet.description}</label><br/>
              </c:forEach>
            </td>
          </tr>
        </c:forEach>
      <tr>
        <td align="center">
          <spring:message code="label.conversion.convert" var="convertLabel"/>
          <button type="submit" class="button" name="action" value="convertAction" title="${convertLabel}">
              ${convertLabel}
          </button>
        </td>
      </tr>
    </c:if>
    <c:if test="${empty conversionForm.schemas}">
      <tr>
        <td>
          <spring:message code="label.conversion.noconversion"/>
        </td>
      </tr>
    </c:if>
    </table>
  </form:form>
</div>
