<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="searchXML"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Search CR for XML files" level="1"/>

  <form:form action="/converter/search" method="post" modelAttribute="form">
    <form:errors path="*" cssClass="error-msg" element="div"/>
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
    <c:set var="countfiles" value="${fn:length(schemas)}"/>
    <c:set var="crfiles" value="${form.schema.crfiles}"/>

    <form:form servletRelativeAction="/converter" method="post" modelAttribute="form">
      <form:errors path="*" cssClass="error-msg" element="div"/>
      <fieldset class="fieldset">
        <div class="row">
          <spring:message code="label.conversion.CRxmlfiles"/> (${countfiles})
        </div>

        <c:choose>
          <c:when test="${countfiles > 0}">
            <div class="row">
              <form:select path="schemaUrl">
                <form:option value="">--</form:option>
                <form:options items="${crfiles}" itemValue="url" itemLabel="url"/>
              </form:select>
            </div>
          </c:when>
          <c:otherwise>
            <div class="row">
              <spring:message code="label.conversion.noCRFiles"/>
            </div>
            <div class="row">
              <form:input type="text" path="schemaUrl" />
            </div>
          </c:otherwise>
        </c:choose>

        <c:choose>
          <c:when test="${!empty form.schema.stylesheets}">
            <div class="row">
              <spring:message code="label.conversion.selectConversion"/>
            </div>

            <div class="row">
              <table class="datatable results">
                <col width="5%"/>
                <col width="40%"/>
                <col width="40%"/>
                <thead>
                <th></th>
                <th>Type</th>
                <th>Description</th>
                </thead>
                <tbody>
                <c:forEach varStatus="i" items="${form.schema.stylesheets}" var="stylesheet">
                <tr>
                  <td>
                    <c:choose>
                      <c:when test="${stylesheet == convId}">
                        <input type="radio" checked="checked" name="conversionId" id="r_${stylesheet.convId}"
                               value="${stylesheet.convId}"/>
                      </c:when>
                      <c:otherwise>
                        <input type="radio" name="conversionId" id="r_${stylesheet.convId}"
                               value="${stylesheet.convId}"/>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>${stylesheet.type}</td>
                  <td>
                    <label for="r_${stylesheet.convId}">${stylesheet.description}</label>
                  </td>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            <div class="row">
              <spring:message code="label.conversion.convert" var="convertLabel"/>
              <button type="submit" class="button" name="convert" title="${convertLabel}">
                  ${convertLabel}
              </button>
            </div>
          </c:when>
          <c:otherwise>
            <p style="color: red; font-weight: bold;"><spring:message code="label.conversion.noconversion"/></p>
          </c:otherwise>
        </c:choose>
      </fieldset>
    </form:form>
  </c:if>
</div>
