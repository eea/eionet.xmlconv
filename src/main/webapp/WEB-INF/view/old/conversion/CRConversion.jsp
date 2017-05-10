<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties" %>--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>

<%--<html:xhtml/>--%>
<div style="width:100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="searchXML"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Search CR for XML files" level="1"/>
  <h1><spring:message code="label.conversion.crconversion.title"/></h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <form:form action="/searchCR" method="get">
    <table class="formtable">
      <tr>
        <th class="scope-col">
          <spring:message code="label.conversion.xmlSchema"/>
        </th>
      </tr>
      <tr>
        <td>
            <%--name="ConversionForm" property="schemaUrl"  size="10">--%>
          <form:select path="schemaUrl">
            <form:option value="">--</form:option>
            <form:options collection="conversion.schemas" property="schema" labelProperty="label"/>
          </form:select>
        </td>
      </tr>
      <tr>
        <td align="center">
          <spring:message code="label.conversion.searchXML" var="searchXMLLabel"/>
          <input type="submit" styleClass="button" title="${searchXMLLabel}"/>
        </td>
      </tr>
    </table>
  </form:form>
  <!--  Show XML files -->
  <c:if test="${ConversionForm.schema}">
    <bean:define id="schema" name="ConversionForm" property="schema"/>
    <bean:size name="schema" id="countfiles" property="crfiles"/>
    <bean:define id="crfiles" name="schema" property="crfiles"/>

    <form:form action="/testConversion" method="post">
      <table class="datatable">
        <tr>
          <th scope="col" class="scope-col">
            <spring:message code="label.conversion.CRxmlfiles"/> (${countfiles})
          </th>
        </tr>

        <bean:define id="selUrl" value="" type="String"/>
        <c:if test="${sessionScope['converted.url']}">
          <bean:define id="selUrl" name="converted.url" scope="session" type="String"/>
        </c:if>


        <c:if test="${countfiles > 0}">
          <tr>
            <td>
                <%--name="ConversionForm" property="url"  size="10">--%>
              <form:select path="${url}">
                <form:option value="">--</form:option>
                <form:options collection="crfiles" property="url" labelProperty="label"/>
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
              <input type="text" name="url" style="width: 30em;" value="${selUrl}"></input>
            </td>
          </tr>
        </c:if>
        <tr>
          <td>
              <%--name="ConversionForm" property="schemaUrl"/>--%>
              <%--name="ConversionForm" property="errorForward" value="errorCR" />--%>
            <form:hidden path="${schemaUrl}"/>
            <form:hidden path="${errorForward}" value="errorCR"/>
          </td>
        </tr>
        <tr>
          <th class="scope-col">
            <spring:message code="label.conversion.selectConversion"/>
          </th>
        </tr>

        <bean:define id="idConv" name="converted.conversionId" scope="session" type="String"/>
        <c:if test="${!idConv}">
          <bean:define id="idConv" name="ConversionForm" property="conversionId" scope="session" type="String"/>
        </c:if>
        <tr>
          <td align="left">
              <%--id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">--%>
            <c:forEach varStatus="index" items="${stylesheets}">
              <%--name="stylesheet" property="convId" value="<%=idConv%>">--%>
              <c:choose>
                <c:when test="${stylesheet == convId}">
                  <input type="radio" checked="checked" name="conversionId"
                         id="r_<bean:write name="stylesheet" property="convId" />"
                         value="<bean:write name="stylesheet" property="convId" />"/>
                </c:when>
                <c:otherwise>
                  <input type="radio" name="conversionId" id="r_<bean:write name="stylesheet" property="convId" />"
                         value="<bean:write name="stylesheet" property="convId" />"/>
                </c:otherwise>
              </c:choose>
              <label for="r_<bean:write name="stylesheet" property="convId" />"><bean:write name="stylesheet"
                                                                                            property="type"/>
                &nbsp;-&nbsp;<bean:write name="stylesheet" property="description"/></label><br/>
            </c:forEach>
          </td>
        </tr>
        <tr>
          <td align="center">
            <bean:size name="schema" id="count" property="stylesheets"/>
            <c:if test="${count > 0}">
              <spring:message code="label.conversion.convert" var="convertLabel"/>
              <input type="submit" styleClass="button" title="${convertLabel}"/>
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
