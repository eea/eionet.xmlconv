<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="convertXML"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Convert XML" level="1"/>

  <form:form servletRelativeAction="/converter" method="post" modelAttribute="form">
    <fieldset class="fieldset">
      <legend><spring:message code="label.conversion.find"/></legend>
      <div class="row">
        <spring:message code="label.conversion.url"/>
      </div>
      <div class="row">
        <spring:message code="label.conversion.insertURL"/>
      </div>
      <div class="row">
        <form:input path="url" />
      </div>

      <c:if test="${form.showSchemaSelection}">
        <div class="row">
          <spring:message code="label.conversion.xmlSchema"/>
        </div>
        <div class="row">
          <spring:message code="label.conversion.selectSchema"/>
        </div>
        <div class="row">
          <form:select path="schemaUrl" size="10">
            <form:option value="">--</form:option>
            <form:options items="conversion.schemas" path="schema" labelProperty="label"/>
          </form:select>
        </div>
      </c:if>
      <div class="row">
        <button type="submit" name="search" class="button">
          <spring:message code="label.conversion.list"/>
        </button>
      </div>
    <c:if test="${!empty form.action}">
      <div class="row">
        <spring:message code="label.conversion.selectConversion"/>
      </div>

      <%--id="idConv"--%>
      <%--<c:set var="conversionUrl" value="${sessionscope['conversionUrl']}"/>--%>
      <%--<c:set var="conversionId" value="${sessionscope['conversionId']}"/>--%>

      <%--id="schema" name="conversionForm" scope="session" property="schemas" type="Schema">--%>
      <c:forEach items="${form.schemas}" var="schema">
        <div class="row">
          <strong>${schema.schema}</strong>
          <br/>

            <%--id="stylesheet" name="schema" property="stylesheets" type="Stylesheet">--%>
          <c:forEach varStatus="v" items="${schema.stylesheets}" var="stylesheet">
            <c:if test="${stylesheet.convId == form.conversionId}">
              <input type="radio" checked="checked" name="conversionId"
                     id="r_${stylesheet.convId}"
                     value="${stylesheet.convId}"/>
            </c:if>
            <c:if test="${stylesheet.convId != form.conversionId}">
              <input type="radio" name="conversionId" id="r_${stylesheet.convId}" value="${stylesheet.convId}"/>
            </c:if>
            <label for="r_${stylesheet.convId}">${stylesheet.type}&nbsp;-&nbsp;${stylesheet.description}</label><br/>
          </c:forEach>

        </div>
      </c:forEach>
      <div class="row">
        <button type="submit" class="button" name="convert">
          <spring:message code="label.conversion.convert"/>
        </button>
      </div>
      <c:if test="${empty form.schemas}">
        <div class="row">
          <spring:message code="label.conversion.noconversion"/>
        </div>
      </c:if>
    </c:if>
    </fieldset>
  </form:form>
</div>
