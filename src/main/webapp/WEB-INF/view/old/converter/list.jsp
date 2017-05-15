<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="convertXML"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Convert XML" level="1"/>
  <h1><spring:message code="label.conversion.find"/></h1>




  <form:form action="/listConversionsByXML" method="get">
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
            <%--<html:text property="url"  style="width: 40em;" />--%>
            ${url}
        </td>
      </tr>
      <c:if test="${ConversionForm.showSchemaSelection == true}">
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

            <form:select name="ConversionForm" path="schemaUrl" size="10">
              <form:option value="">--</form:option>
              <form:options items="conversion.schemas" property="schema" labelProperty="label"/>
            </form:select>
          </td>
        </tr>
      </c:if>
      <tr>
        <td align="center">
          <spring:message code="label.conversion.list" var="conversionListLabel"/>
          <input type="submit" value="searchAction" styleClass="button" property="searchAction"
                 title="${conversionListLabel}"/>
        </td>
      </tr>
    </table>
    <c:if test="${ConversionForm.action}">
      <table class="datatable">
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.selectConversion"/>
        </th>
      </tr>
      <c:forEach items="${ConversionForm.schemas}">
        <%--<bean:define id="idConv" name="converted.conversionId" scope="session" type="String"/>--%>
        <%--<bean:define id="idConv" name="ConversionForm" property="conversionId" scope="session" type="String"/>--%>

        <%--id="schema" name="ConversionForm" scope="session" property="schemas" type="Schema">--%>
        <c:forEach varStatus="i" items="${ConversionForm.schemas}" var="schema">
          <tr>
            <td align="left">
              <strong>${schema.schema}</strong>
              <br/>
                <%--id="stylesheet" name="schema" property="stylesheets" type="Stylesheet">--%>
              <c:forEach varStatus="v" items="${schema.stylesheets}" var="stylesheet">
                <c:if test="${stylesheet.convId == ConversionForm.conversionId}">
                  <input type="radio" checked="checked" name="conversionId"
                         id="r_${stylesheet.convId}"
                         value="${stylesheet.convId}"/>
                </c:if>
                <c:if test="${stylesheet.convId != ConversionForm.conversionId}">
                  <input type="radio" name="conversionId" id="r_${stylesheet.convId}" value="${stylesheet.convId}"/>
                </c:if>
                <label for="r_${stylesheet.convId}">${stylesheet.type}&nbsp;-&nbsp;${stylesheet.description}</label><br/>
              </c:forEach>
            </td>
          </tr>
        </c:forEach>
      </c:forEach>
      <tr>
        <td align="center">
          <spring:message code="label.conversion.convert" var="convertLabel"/>
          <button type="submit" class="button" name="action" value="convertAction" title="${convertLabel}"/>
        </td>
      </tr>
    </c:if>
    <c:if test="${!ConversionForm.schemas}">
      <tr>
        <td>
          <spring:message code="label.conversion.noconversion"/>
        </td>
      </tr>
    </c:if>
    </table>
  </form:form>
</div>
