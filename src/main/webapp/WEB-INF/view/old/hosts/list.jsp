<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<%--<c:if test="${user}">
  <bean:define id="username" name="user" scope="session"/>
</c:if>--%>



<ed:breadcrumbs-push label="Hosts" level="1"/>
<ed:hasPermission username="${sessionScope['user']}" acl="host" permission="i">
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

<c:if test="${hosts.list}">
  <form:form action="/hosts/delete" method="post">
    <div style="width:80%">
      <table class="datatable" width="100%">
        <col style="width:5%"/>
        <col style="width:47%"/>
        <col style="width:47%"/>
        <thead>
        <tr>
          <ed:hasPermission username="${sessionScope['user']}" acl="host" permission="d">
            <th scope="col">&nbsp;</th>
          </ed:hasPermission>
          <th scope="col"><spring:message code="label.hosts.host"/></th>
          <th scope="col"><spring:message code="label.hosts.username"/></th>
        </tr>
        </thead>
        <tbody>
    <%--id="host" name="hosts.list">--%>
        <c:forEach varStatus="index" items="${hosts.list}" var="host">
          <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
            <ed:hasPermission username="${sessionScope['user']}" acl="host" permission="d">
              <td>
                <%--<bean:define id="hostId" name="host" property="id"/>--%>
                <input type="radio" name="id" value="${host.id}"/>
              </td>
            </ed:hasPermission>
            <td>
                <%--paramId="id" paramName="host" paramProperty="id" titleKey="label.hosts.edit"--%>
              <ed:hasPermission username="${sessionScope['user']}" acl="host" permission="u">
                <a href="/hosts/edit">
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
          <!--input type="button" class="button" value="<spring:message code="label.delete"/>" onclick="return submitAction(1,'/do/hosts/delete');" /-->
        </ed:hasPermission>
      </div>
    </div>
  </form:form>
</c:if>

<div class="visualClear">&nbsp;</div>
