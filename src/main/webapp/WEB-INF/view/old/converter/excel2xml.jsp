<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<div style="width:100%;">

  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="excel2xml"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Spreadsheet to XML" level="1"/>
  <h1><spring:message code="label.conversion.excel2xml.heading"/></h1>




  <form:form action="/excel2XmlConversion" method="get" modelAttribute="form">
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
          <form:input id="inpUrl" path="${url}"/>
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
            <%--styleId="split1" value="all" onclick="sheet.disabled=true" />--%>
          <form:radiobutton path="${split}" id="split1" value="all"/>
          <label for="split1"><spring:message code="label.conversion.excel.allsheets"/></label>
        </td>
      </tr>
      <tr>
        <td>
            <%--styleId="split2" value="split" onclick="sheet.disabled=false"/>--%>
          <form:radiobutton path="${split}" id="split2" value="split"/>
          <label for="split2"><spring:message code="label.conversion.excel.sheetname"/></label>
            <%--onfocus="split[1].checked=true"/>--%>
          <form:input path="${sheet}"/>
        </td>
      </tr>
      <tr>
        <td>
            <%--name="ExcelConversionForm" property="showConversionLog" styleId ="chkConversion" />--%>
          <form:checkbox path="${showConversionLog}" id="chkConversion" value="true"/>
          <label for="chkConversion"><spring:message code="label.conversion.excel.showConversionLog"/></label>
        </td>
      </tr>
      <tr>
        <td align="center">
          <input type="submit" styleClass="button">
          <spring:message code="label.conversion.convert"/>
          </input>
        </td>
      </tr>
      <c:choose>
        <c:when test="${ExcelConversionForm.conversionLog}">
          <tr>
            <td><bean:write name="ExcelConversionForm" property="conversionLog" filter="false"/></td>
          </tr>
        </c:when>
        <c:otherwise>
          <tr>
            <td><spring:message code="label.conversion.excel.warning"/>
            </td>
          </tr>
        </c:otherwise>
      </c:choose>
    </table>
    <!--/fieldset-->
  </form:form>
</div>
