<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<%--classname="java.lang.String"--%>
<tiles:importAttribute name="selectedTab"/>
<div id="tabbedmenu">
  <ul>
    <ed:tabItem href="/converter" title="Convert XML" id="convertXML" selectedTab="${selectedTab}">
      <spring:message code="label.conversion.tab.converters"/>
    </ed:tabItem>
    <ed:tabItem href="/converter/search" title="Search CR for XML files" id="searchXML"
                selectedTab="${selectedTab}">
      <spring:message code="label.conversion.tab.crconversion"/>
    </ed:tabItem>
    <ed:tabItem href="/converter/excel2xml" title="Convert spreadsheet to DD XML" id="excel2xml"
                selectedTab="${selectedTab}">
      <spring:message code="label.conversion.tab.excel2xml"/>
    </ed:tabItem>
    <ed:tabItem href="/converter/json2xml" title="Convert JSON to XML" id="json2xml" selectedTab="${selectedTab}">
      <spring:message code="label.conversion.tab.json2xml"/>
    </ed:tabItem>
  </ul>
</div>
<div id="tabbedmenuend"></div>
