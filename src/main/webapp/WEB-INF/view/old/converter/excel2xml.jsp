<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">

  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="excel2xml"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Spreadsheet to XML" level="1"/>

  <form:form servletRelativeAction="/converter/excel2xml" method="post" modelAttribute="form">
    <form:errors path="*" cssClass="error-msg" element="div"/>
    <fieldset class="fieldset">
      <legend><spring:message code="label.conversion.excel2xml.heading"/></legend>
      <div class="row">
        <spring:message code="label.conversion.url"/>
      </div>
      <div class="row">
        <label for="inpUrl"><spring:message code="label.conversion.insertExcelUrl"/></label>
      </div>
      <div class="row">
        <form:input id="inpUrl" path="url"/>
      </div>
      <div class="row">
        <spring:message code="label.conversion.sheets"/>
      </div>
      <div class="row">
        <spring:message code="label.conversion.excel.format"/>
      </div>
      <div class="row">
        <form:radiobutton path="split" id="split1" value="all"/>
        <label for="split1"><spring:message code="label.conversion.excel.allsheets"/></label>
      </div>
      <div class="row">
        <div class="columns small-4">
          <label for="split2"><spring:message code="label.conversion.excel.sheetname"/></label>
          <form:radiobutton path="split" id="split2" value="split"/>
        </div>
        <div class="columns small-8">
          <form:input path="sheet"/>
        </div>
      </div>
      <div class="row">
        <button type="submit" class="button">
          <spring:message code="label.conversion.convert"/>
        </button>
      </div>

      <c:if test="${!empty conversionLinks}">
        <table class="datatable results">
          <thead>
          <th>Num</th>
          <th>Link</th>
          </thead>
          <tbody>
          <c:forEach items="${conversionLinks}" var="conversionLink" varStatus="i">
            <tr>
              <td>${i.index + 1}</td>
              <td><a href="${conversionLink}" target="_blank">${conversionLink}</a></td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
        <c:choose>
          <c:when test="${form.conversionLog}">
            <tr>
              <td>${conversionLog}</td>
            </tr>
          </c:when>
          <c:otherwise>
            <tr>
              <td><spring:message code="label.conversion.excel.warning"/></td>
            </tr>
          </c:otherwise>
        </c:choose>
      </c:if>
    </fieldset>
  </form:form>
</div>
