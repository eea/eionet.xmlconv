<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<tiles:importAttribute name="selectedTab"/>
<div id="tabbedmenu">
  <ul>
    <ed:tabItem selectedTab="${selectedTab}" href="/admin/purge" id="purge" title="label.admin.purge">
      <spring:message code="label.admin.purge"/>
    </ed:tabItem>
  </ul>
</div>
<div id="tabbedmenuend"></div>
