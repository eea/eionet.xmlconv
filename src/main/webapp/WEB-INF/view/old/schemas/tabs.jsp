<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<tiles:importAttribute name="selectedTab"/>
<div id="tabbedmenu">
  <ul>
    <spring:message code="label.tab.title.schema" var="title"/>
    <ed:tabItem id="schema" title="${title}" href="/schemas/${schemaId}" selectedTab="${selectedTab}">
      <spring:message code="label.tab.title.schema"/>
    </ed:tabItem>

    <spring:message code="label.tab.title.xsl" var="title"/>
    <ed:tabItem id="conversions" title="${title}" href="/schemas/${schemaId}/conversions" selectedTab="${selectedTab}">
      <spring:message code="label.tab.title.xsl"/>
    </ed:tabItem>

    <spring:message code="label.tab.title.scripts" var="title"/>
    <ed:tabItem id="scripts" title="${title}" href="/schemas/${schemaId}/scripts" selectedTab="${selectedTab}">
      <spring:message code="label.tab.title.scripts"/>
    </ed:tabItem>
  </ul>
</div>