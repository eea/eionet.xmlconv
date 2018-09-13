<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>


<div style="width:100%;">
  <tiles:insertDefinition name="AdminTabs">
    <tiles:putAttribute name="selectedTab" value="users"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Users" level="1"/>

  <form:form action="/admin/users" method="post" modelAttribute="form">
    <form:errors path="*" cssClass="error-msg" element="div"/>
      <%--<h3><spring:message code="label.admin.users.title"/></h3>--%>
    <div style="display: flex; flex-wrap: wrap;">
    <%-- justify-content: space-between--%>
      <c:forEach items="${groups}" var="group" varStatus="status">
        <fieldset class="fieldset" style="display: inline-block;">
          <legend>${group.key}</legend>
          <ul>
            <%--<c:forEach items="${group.value.members()}" var="user" varStatus="userIndex">--%>
            <c:forEach items="${group.value}" var="user" varStatus="userIndex">
                <li>${user}</li>
            </c:forEach>
          </ul>
        </fieldset>
      </c:forEach>
    </div>
    <a class="button" href="/admin/users/edit"><spring:message code="label.admin.users.edit"/></a>
  </form:form>

</div>
