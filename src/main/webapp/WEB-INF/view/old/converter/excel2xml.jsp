<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">

  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="excel2xml"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Spreadsheet to XML" level="1"/>
  <h1><spring:message code="label.conversion.excel2xml.heading"/></h1>

  <form:form servletRelativeAction="/converter/excel2xml" method="post" modelAttribute="form">
    <table class="datatable">
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.url"/>
        </th>
      </tr>
      <tr>
        <td>
          <label for="inpUrl"><spring:message code="label.conversion.insertExcelUrl"/></label>
        </td>
      </tr>
      <tr>
        <td>
            <%--name="ExcelConversionForm" property="url" style="width: 45em;" styleId="inpUrl" size="200"/>--%>
          <form:input id="inpUrl" path="url"/>
        </td>
      </tr>
      <tr>
        <th scope="col" class="scope-col">
          <spring:message code="label.conversion.sheets"/>
        </th>
      </tr>
      <tr>
        <td>
          <spring:message code="label.conversion.excel.format"/>
        </td>
      </tr>
      <tr>
        <td>
          <form:radiobutton path="split" id="split1" value="all"/>
          <label for="split1"><spring:message code="label.conversion.excel.allsheets"/></label>
        </td>
      </tr>
      <tr>
        <td>
          <form:radiobutton path="split" id="split2" value="split"/>
          <label for="split2"><spring:message code="label.conversion.excel.sheetname"/></label>
          <form:input path="sheet"/>
        </td>
      </tr>
      <tr>
        <td align="center">
          <button type="submit" class="button">
          <spring:message code="label.conversion.convert"/>
          </button>
        </td>
      </tr>

      <c:if test="${!empty conversionLinks}">
        <tr>
          <th>Num</th>
          <th>Link</th>
        </tr>
        <c:forEach items="${conversionLinks}" var="conversionLink" varStatus="i">
          <tr>
            <td>${i.index}</td>
            <td><a href="${conversionLink}" target="_blank">${conversionLink}</a></td>
          </tr>
        </c:forEach>
      </c:if>

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
    </table>
    <!--/fieldset-->
  </form:form>
</div>
