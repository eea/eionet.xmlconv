<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:set var="username" value="${sessionScope['user']}" scope="page" />

<ed:breadcrumbs-push label="Hosts" level="1"/>

<ed:hasPermission username="username" acl="host" permission="i">
  <div id="operations">
    <ul>
      <li><a href="/hosts/add"><spring:message code="label.hosts.add" /></a></li>
    </ul>
  </div>

</ed:hasPermission>

<h1 class="documentFirstHeading">
  <spring:message code="label.hosts.title"/>
</h1>

<c:choose>
  <c:when test="${!empty hosts}">
    <form:form servletRelativeAction="/hosts" method="post" modelAttribute="form">
      <form:errors path="*" cssClass="error-msg" element="div"/>
      <div>
        <table class="datatable results" width="100%">
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
                  <form:radiobutton path="id" value="${host.id}"/>
                </td>
              </ed:hasPermission>
              <td>
                <spring:message code="label.hosts.edit" var="title"/>
                <ed:hasPermission username="username" acl="host" permission="u">
                  <a href="/hosts/${host.id}/edit" title="${title}">
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
            <button type="submit" class="button" name="delete">
              <spring:message code="label.delete"/>
            </button>
          </ed:hasPermission>
        </div>
      </div>
    </form:form>
  </c:when>
  <c:otherwise>
    <p><spring:message code="label.hosts.missing"/></p>
  </c:otherwise>
</c:choose>
<c:if test="">

</c:if>

<div class="visualClear">&nbsp;</div>
