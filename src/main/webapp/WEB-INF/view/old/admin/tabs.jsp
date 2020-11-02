<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<tiles:importAttribute name="selectedTab"/>
<div id="tabbedmenu">
  <ul>
    <ed:tabItem selectedTab="${selectedTab}" href="/admin/purge" id="purge" title="label.admin.purge">
      <spring:message code="label.admin.purge"/>
    </ed:tabItem>
    <ed:tabItem selectedTab="${selectedTab}" href="/admin/users" id="users" title="label.admin.users">
      <spring:message code="label.admin.users"/>
    </ed:tabItem>
    <ed:tabItem selectedTab="${selectedTab}" href="/admin/generateJWTToken" id="generateJWTToken" title="label.admin.generateJWTToken">
      <spring:message code="label.admin.generateJWTToken"/>
    </ed:tabItem>
  </ul>
</div>
<div id="tabbedmenuend"></div>
