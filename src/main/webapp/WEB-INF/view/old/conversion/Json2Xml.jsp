<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%--<html:xhtml />--%>
<div style="width: 100%;">
  <tiles:insertDefinition name="ConverterTabs">
    <tiles:putAttribute name="selectedTab" value="json2xml"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Json to Xml" level="1"/>
  <h1>
    <spring:message code="label.conversion.json2xml.title"/>
  </h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <form action="convertJson2Xml" method="get" id="json2XmlForm">
    <div>
      <label for="param">
        <spring:message code="label.conversion.json2xml.source"/>
      </label>
      <textarea name="json" id="param" rows="10" cols="100"></textarea>
      <input type="submit" class="button" value="Convert"/>
    </div>
  </form>
</div>
