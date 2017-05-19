<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:set var="username" value="${sessionScope['user']}" scope="page" />

<ed:breadcrumbs-push label="Hosts" level="1"/>
<ed:hasPermission username="username" acl="host" permission="i">
  <div id="operations">
    <ul>
      <li><a href="/hosts/add">Add host</a></li>
    </ul>
  </div>
</ed:hasPermission>

<h1 class="documentFirstHeading">
  <spring:message code="label.hosts.title"/>
</h1>

<div class="visualClear">&nbsp;</div>

<c:if test="${!empty hosts}">
  <form:form servletRelativeAction="/hosts/delete" method="post" modelAttribute="form">
    <div style="width:80%">
      <table class="datatable" width="100%">
        <col style="width:5%"/>
        <col style="width:47%"/>
        <col style="width:47%"/>
        <thead>
        <tr>
          <ed:hasPermission username="username" acl="host" permission="d">
            <th scope="col">&nbsp;</th>
          </ed:hasPermission>
          <th scope="col"><spring:message code="label.hosts.host"/></th>
          <th scope="col"><spring:message code="label.hosts.username"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach varStatus="i" items="${hosts}" var="host">
          <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
            <ed:hasPermission username="username" acl="host" permission="d">
              <td>
                <form:radiobutton path="id" name="id" value="${host.id}"/>
              </td>
            </ed:hasPermission>
            <td>
              <ed:hasPermission username="username" acl="host" permission="u">
                <a href="/hosts/${host.id}/edit" title="label.hosts.edit">
                  ${host.hostname}
                </a>
              </ed:hasPermission>
            </td>
            <td>
              ${host.username}
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
      <div class="boxbottombuttons">
        <ed:hasPermission username="username" acl="host" permission="d">
          <button type="submit" class="button" name="action" value="delete">
            <spring:message code="label.delete"/>
          </button>
        </ed:hasPermission>
      </div>
    </div>
  </form:form>
</c:if>

<div class="visualClear">&nbsp;</div>
