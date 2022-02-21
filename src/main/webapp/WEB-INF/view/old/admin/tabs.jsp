<%@ taglib prefix="th" uri="/WEB-INF/eurodyn.tld" %>
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
    <ed:tabItem selectedTab="${selectedTab}" href="/admin/jobExecutorInstancesView" id="jobExecutorInstancesView" title="label.admin.jobExecutorInstancesView">
      <spring:message code="label.admin.jobExecutorInstancesView"/>
    </ed:tabItem>
    <ed:tabItem selectedTab="${selectedTab}" href="/new/admin/viewAndEditProperties" id="viewAndEditProperties" title="label.admin.properties">
      <spring:message code="label.admin.properties"/>
    </ed:tabItem>
    <ed:tabItem selectedTab="${selectedTab}" href="/new/admin/alerts" id="alerts" title="label.admin.alerts">
      <spring:message code="label.admin.alerts"/>
    </ed:tabItem>
  </ul>
</div>
<div id="tabbedmenuend"></div>
