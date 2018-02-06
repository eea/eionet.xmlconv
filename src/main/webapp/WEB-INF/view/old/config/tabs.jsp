<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<tiles:importAttribute name="selectedTab"/>
<div id="tabbedmenu">
  <ul>
    <ed:tabItem selectedTab="${selectedTab}" href="/config/database" id="database" title="label.config.db">
      <spring:message code="label.config.db"/>
    </ed:tabItem>
    <ed:tabItem selectedTab="${selectedTab}" href="/config/system" id="system" title="label.config.system">
      <spring:message code="label.config.system"/>
    </ed:tabItem>
    <ed:tabItem selectedTab="${selectedTab}" href="/config/purge" id="purge" title="label.config.purge">
      <spring:message code="label.config.purge"/>
    </ed:tabItem>
    <ed:tabItem selectedTab="${selectedTab}" href="/config/ldap" id="ldap" title="label.config.ldap">
      <spring:message code="label.config.ldap"/>
    </ed:tabItem>
    <ed:tabItem selectedTab="${selectedTab}" href="/config/basex" id="basex" title="label.config.basex">
      <spring:message code="label.config.basexserver"/>
    </ed:tabItem>
  </ul>
</div>
<div id="tabbedmenuend"></div>
