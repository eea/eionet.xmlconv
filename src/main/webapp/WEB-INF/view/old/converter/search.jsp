<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="searchXML"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Search CR for XML files" level="1"/>

  <form:form action="/converter/search" method="post" modelAttribute="form">
    <fieldset class="fieldset">
      <legend><spring:message code="label.conversion.crconversion.title"/></legend>

    <div class="row">
      <spring:message code="label.conversion.xmlSchema"/>
    </div>
    <div class="row">
      <form:select path="schemaUrl">
        <form:option value="">--</form:option>
        <form:options items="${schemas}" itemValue="schema" itemLabel="schema"/>
      </form:select>
    </div>
    <div class="row">
      <spring:message code="label.conversion.searchXML" var="searchXMLLabel"/>
      <button type="submit" class="button" title="${searchXMLLabel}">
          ${searchXMLLabel}
      </button>
    </div>
    </fieldset>
  </form:form>
  <!--  Show XML files -->
  <c:if test="${!empty form.schema}">
<%--    <bean:define id="schema" name="form" property="schema"/>
    <bean:size name="schema" id="countfiles" property="crfiles"/>
    <bean:define id="crfiles" name="schema" property="crfiles"/>--%>
    <c:set var="countfiles" value="${fn:length(form.schemas)}" />

    <form:form action="/testConversion" method="post" modelAttribute="form">
      <fieldset class="fieldset">
      <div class="row">
        <spring:message code="label.conversion.CRxmlfiles"/> (${countfiles})
      </div>

        <%--<bean:define id="selUrl" value="" type="String"/>--%>
        <%--<bean:define id="selUrl" name="converted.url" scope="session" type="String"/>--%>
        <c:if test="${sessionScope['converted.url']}">
          ${sessionScope['converted.url']}
        </c:if>

        <c:if test="${countfiles > 0}">
          <div class="row">
              <%--name="form" property="url"  size="10">--%>
            <form:select path="url">
              <form:option value="">--</form:option>
              <form:options items="crfiles" property="url" itemLabel="url"/>
            </form:select>
          </div>
        </c:if>
        <c:if test="${countfiles > 0}">
          <div class="row">
            <spring:message code="label.conversion.noCRFiles"/>
          </div>
          <div class="row">
            <form:input type="text" path="cRurl" style="width: 30em;" value="${selUrl}" />
          </div>
        </c:if>
      <%--todo remove?--%>
        <div class="row">
            <%--name="form" property="schemaUrl"/>--%>
            <%--name="form" property="errorForward" value="errorCR" />--%>
          <form:hidden path="schemaUrl"/>
            <%--<form:hidden path="errorForward" value="errorCR"/>--%>
        </div>
        <div class="row">
          <spring:message code="label.conversion.selectConversion"/>
        </div>

        <%--<bean:define id="idConv" name="converted.conversionId" scope="session" type="String"/>--%>
        <%--<c:if test="${!idConv}">
          <bean:define id="idConv" name="form" property="conversionId" scope="session" type="String"/>
        </c:if>--%>
      <div class="row">
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
      </div>
      <div class="row">
        <c:if test="${fn:length(form.schema.stylesheets) > 0}">
          <spring:message code="label.conversion.convert" var="convertLabel"/>
          <button type="submit" class="button" title="${convertLabel}">
              ${convertLabel}
          </button>
        </c:if>
        <c:if test="${count > 0}">
          <p style="color: red; font-weight: bold;"><spring:message code="label.conversion.noconversion"/></p>
        </c:if>
      </div>
      </fieldset>
    </form:form>
  </c:if>
</div>
