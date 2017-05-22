<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="searchXML"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Search CR for XML files" level="1"/>
  <h1><spring:message code="label.conversion.crconversion.title"/></h1>

  <form:form action="/converter/search" method="post" modelAttribute="conversionForm">
    <table class="formtable">
      <tr>
        <th class="scope-col">
          <spring:message code="label.conversion.xmlSchema"/>
        </th>
      </tr>
      <tr>
        <td>
          <form:select path="schemaUrl">
            <form:option value="">--</form:option>
            <form:options items="${schemas}" itemValue="schema" itemLabel="schema"/>
          </form:select>
        </td>
      </tr>
      <tr>
        <td align="center">
          <spring:message code="label.conversion.searchXML" var="searchXMLLabel"/>
          <button type="submit" class="button" title="${searchXMLLabel}">
              ${searchXMLLabel}
          </button>
        </td>
      </tr>
    </table>
  </form:form>
  <!--  Show XML files -->
  <c:if test="${!empty conversionForm.schema}">
<%--    <bean:define id="schema" name="conversionForm" property="schema"/>
    <bean:size name="schema" id="countfiles" property="crfiles"/>
    <bean:define id="crfiles" name="schema" property="crfiles"/>--%>
    <c:set var="countfiles" value="${fn:length(conversionForm.schemas)}" />

    <form:form action="/testConversion" method="post" modelAttribute="conversionForm">
      <table class="datatable">
        <tr>
          <th scope="col" class="scope-col">
            <spring:message code="label.conversion.CRxmlfiles"/> (${countfiles})
          </th>
        </tr>

        <%--<bean:define id="selUrl" value="" type="String"/>--%>
        <%--<bean:define id="selUrl" name="converted.url" scope="session" type="String"/>--%>
        <c:if test="${sessionScope['converted.url']}">
          ${sessionScope['converted.url']}
        </c:if>

        <c:if test="${countfiles > 0}">
          <tr>
            <td>
                <%--name="conversionForm" property="url"  size="10">--%>
              <form:select path="url">
                <form:option value="">--</form:option>
                <form:options items="crfiles" property="url" itemLabel="url"/>
              </form:select>
            </td>
          </tr>
        </c:if>
        <c:if test="${countfiles > 0}">
          <tr>
            <td>
              <spring:message code="label.conversion.noCRFiles"/>
            </td>
          </tr>
          <tr>
            <td>
              <form:input type="text" path="cRurl" style="width: 30em;" value="${selUrl}" />
            </td>
          </tr>
        </c:if>
        <tr>
          <td>
              <%--name="conversionForm" property="schemaUrl"/>--%>
              <%--name="conversionForm" property="errorForward" value="errorCR" />--%>
            <form:hidden path="schemaUrl"/>
            <%--<form:hidden path="errorForward" value="errorCR"/>--%>
          </td>
        </tr>
        <tr>
          <th class="scope-col">
            <spring:message code="label.conversion.selectConversion"/>
          </th>
        </tr>

        <%--<bean:define id="idConv" name="converted.conversionId" scope="session" type="String"/>--%>
        <%--<c:if test="${!idConv}">
          <bean:define id="idConv" name="conversionForm" property="conversionId" scope="session" type="String"/>
        </c:if>--%>
        <tr>
          <td align="left">
              <%--id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">--%>
            <c:forEach varStatus="i" items="${schema.stylesheets}" var="stylesheet">
              <%--name="stylesheet" property="convId" value="<%=idConv%>">--%>
              <c:choose>
                <c:when test="${stylesheet == convId}">
                  <input type="radio" checked="checked" name="conversionId"
                         id="r_${stylesheet.convId}" value="${stylesheet.convId}"/>
                </c:when>
                <c:otherwise>
                  <input type="radio" name="conversionId" id="r_${stylesheet.convId}"
                         value="${stylesheet.convId}"/>
                </c:otherwise>
              </c:choose>
              <label for="r_${stylesheet.convId}">${stylesheet.type}
                &nbsp;-&nbsp;${stylesheet.description}</label><br/>
            </c:forEach>
          </td>
        </tr>
        <tr>
          <td align="center">
            <c:if test="${fn:length(conversionForm.schema.stylesheets) > 0}">
              <spring:message code="label.conversion.convert" var="convertLabel"/>
              <button type="submit" class="button" title="${convertLabel}">
                  ${convertLabel}
              </button>
            </c:if>
            <c:if test="${count > 0}">
              <p style="color: red; font-weight: bold;"><spring:message code="label.conversion.noconversion"/></p>
            </c:if>
          </td>
        </tr>
      </table>
    </form:form>
  </c:if>
</div>
