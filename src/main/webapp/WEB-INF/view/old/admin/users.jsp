<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>


<div style="width:100%;">
  <tiles:insertDefinition name="AdminTabs">
    <tiles:putAttribute name="selectedTab" value="users"/>
  </tiles:insertDefinition>

  <ed:breadcrumbs-push label="Users" level="1"/>

  <form:form action="/admin/users" method="post" modelAttribute="form">

    <form:errors path="*" cssClass="error-msg" element="div"/>
    <fieldset class="fieldset">
      <legend><spring:message code="label.admin.users.title"/></legend>
      <div class="row">
          <c:forEach items="${groups}" var="group" varStatus="status">
            <fieldset class="fieldset">
              <legend>${group.key}</legend>
              <c:forEach items="${group.value.members()}" var="user" varStatus="userIndex">
                <div class="row">
                  <div class="columns small-4">
                    <input type="checkbox" name="user">${user}</div>
                  </div>
                  <%--<div class="columns small-8">--%>
                      <%--${user}--%>
                  <%--</div>--%>
              </c:forEach>
            </fieldset>
          </c:forEach>
        </div>
    </fieldset>
    <button type="submit" class="button" name="edit">
      <spring:message code="label.admin.users.edit"/>
    </button>
    <button type="submit" class="button" name="delete">
      <spring:message code="label.admin.users.delete"/>
    </button>

  </form:form>

</div>
