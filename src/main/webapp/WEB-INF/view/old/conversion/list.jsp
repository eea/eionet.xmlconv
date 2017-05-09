<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties" %>--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--<html:xhtml/>--%>
<div style="width:100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="convertXML"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Convert XML" level="1"/>
  <h1><spring:message code="label.conversion.find"/></h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

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

            <form:select name="ConversionForm" property="schemaUrl" size="10">
              <form:option value="">--</form:option>
              <form:options collection="conversion.schemas" property="schema" labelProperty="label"/>
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
        <bean:define id="idConv" name="converted.conversionId" scope="session" type="String"/>
        <c:if test="${!idConv}">
          <bean:define id="idConv" name="ConversionForm" property="conversionId" scope="session" type="String"/>
        </c:if>

        <%--id="schema" name="ConversionForm" scope="session" property="schemas" type="Schema">--%>
        <c:forEach varStatus="index" items="${ConversionForm.schemas}">
          <tr>
            <td align="left">
              <strong><bean:write name="schema" property="schema"/></strong>
              <br/>
                <%--id="stylesheet" name="schema" property="stylesheets" type="Stylesheet">--%>
              <c:forEach varStatus="index" items="${schema.stylesheets}">
                <c:if test="${stylesheet.convId == idConv}">
                  <input type="radio" checked="checked" name="conversionId"
                         id="r_<bean:write name="stylesheet" property="convId" />"
                         value="<bean:write name="stylesheet" property="convId" />"/>
                </c:if>
                <c:if test="${stylesheet.convId != idconv}">
                  <input type="radio" name="conversionId" id="r_<bean:write name="stylesheet" property="convId" />"
                         value="<bean:write name="stylesheet" property="convId" />"/>
                </c:if>
                <label for="r_<bean:write name="stylesheet" property="convId" />"><bean:write name="stylesheet"
                                                                                              property="type"/>
                  &nbsp;-&nbsp;<bean:write name="stylesheet" property="description"/></label><br/>
              </c:forEach>
            </td>
          </tr>
        </c:forEach>
      </c:forEach>
      <tr>
        <td align="center">
          <spring:message code="label.conversion.convert" var="convertLabel"/>
          <input type="submit" styleClass="button" property="convertAction" title="${convertLabel}"/>
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
