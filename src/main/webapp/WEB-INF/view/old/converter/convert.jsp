<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="convertXML"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Convert XML" level="1"/>

  <form:form servletRelativeAction="/converter" method="post" modelAttribute="form">
    <form:errors path="*" cssClass="error-msg" element="div"/>
    <fieldset class="fieldset">
      <legend><spring:message code="label.conversion.find"/></legend>
      <div class="row">
        <spring:message code="label.conversion.url"/>
      </div>
      <div class="row">
        <spring:message code="label.conversion.insertURL"/>
      </div>
      <div class="row">
        <form:input path="url"/>
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
            <form:options items="schemasList" path="schema" labelProperty="label"/>
          </form:select>
        </div>
      </c:if>
      <div class="row">
        <button type="submit" name="find" class="button">
          <spring:message code="label.conversion.list"/>
        </button>
      </div>

      <c:if test="${!empty form.action}">
        <c:forEach items="${form.schemas}" var="schema">
          <c:choose>
            <c:when test="${!empty schema.stylesheets}">
              <div class="row">
                <spring:message code="label.conversion.selectConversion"/>
              </div>
              <div class="row">
                <table class="datatable results">
                  <col width="5%"/>
                  <col width="25%" />
                  <col width="40%" />
                  <col width="20%" />
                  <caption><strong>${schema.schema}</strong></caption>
                  <thead>
                    <th></th>
                    <th>Type</th>
                    <th>Description</th>
                    <th>Stylesheet</th>
                  </thead>
                  <tbody>
                  <c:forEach varStatus="v" items="${schema.stylesheets}" var="stylesheet">
                    <tr>
                      <td>
                        <c:choose>
                          <c:when test="${v.index == 1}">
                            <input type="radio" name="conversionId" id="r_${stylesheet.convId}" value="${stylesheet.convId}" checked="checked" />
                          </c:when>
                          <c:otherwise>
                            <input type="radio" name="conversionId" id="r_${stylesheet.convId}" value="${stylesheet.convId}"/>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>${stylesheet.type}</td>
                      <td>
                        <label for="r_${stylesheet.convId}">${stylesheet.description}</label><br/>
                      </td>
                      <td><a href="/xsl/${stylesheet.xslFileName}">${stylesheet.xslFileName}</a></td>
                    </tr>
                  </c:forEach>
                  </tbody>
                </table>
              </div>
            </c:when>
            <c:otherwise>
              <div class="row">
                <spring:message code="label.conversion.noconversion"/>
              </div>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <c:if test="${!empty schema.stylesheets}">
          <div class="row">
            <button type="submit" class="button" name="convert">
              <spring:message code="label.conversion.convert"/>
            </button>
          </div>
        </c:if>
      </c:if>
    </fieldset>
  </form:form>
</div>
