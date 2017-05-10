<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<c:if present name="user">
  <bean:define id="username" name="user" scope="session"/>
</c:if present>

<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="Hosts" level="1"/>
<ed:hasPermission username="username" acl="host" permission="i">
  <div id="operations">
    <ul>
      <li><a href="/old/hosts/add">Add host</a></li>
    </ul>
  </div>
</ed:hasPermission>

<h1 class="documentFirstHeading">
  <spring:message code="label.hosts.title"/>
</h1>

<%-- include Error display --%>
<tiles:insertDefinition name="Error"/>

<div class="visualClear">&nbsp;</div>

<c:if present name="hosts.list">
  <form:form action="/hosts/delete" method="post">
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
        <c:if iterate indexId="index" id="host" name="hosts.list">
          <tr <%=(index.intValue() % 2 == 1) ? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
            <ed:hasPermission username="username" acl="host" permission="d">
              <td>
                <bean:define id="hostId" name="host" property="id"/>
                <input type="radio" name="id" value="${hostId}"/>
              </td>
            </ed:hasPermission>
            <td>
                <%--paramId="id" paramName="host" paramProperty="id" titleKey="label.hosts.edit"--%>
              <ed:hasPermission username="username" acl="host" permission="u">
                <a href="old/hosts/edit">
                  <bean:write name="host" property="hostname"/>
                </a>
              </ed:hasPermission>
            </td>
            <td>
              <bean:write name="host" property="username"/>
            </td>
          </tr>
        </c:if iterate>
        </tbody>
      </table>
      <div class="boxbottombuttons">
        <ed:hasPermission username="username" acl="host" permission="d">
          <html:submit styleClass="button" property="action">
            <spring:message code="label.delete"/>
          </html:submit>
          <!--input type="button" class="button" value="<spring:message
                code="label.delete"/>" onclick="return submitAction(1,'/do/hosts/delete');" /-->
        </ed:hasPermission>
      </div>
    </div>
  </form:form>
</c:if present>

<div class="visualClear">&nbsp;</div>
