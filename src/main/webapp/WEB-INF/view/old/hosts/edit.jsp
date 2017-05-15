<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:if test="${user}">
  <bean:define id="username" name="user" scope="session"/>
</c:if>

<ed:breadcrumbs-push label="Host details" level="2"/>
<h1 class="documentFirstHeading">
<c:choose>
  <c:when test="${HostForm.id}">
    <spring:message code="label.hosts.edit"/>
  </c:when>
  <c:otherwise>
    <spring:message code="label.hosts.add_title"/>
  </c:otherwise>
</c:choose>
  </h1>

  <%-- include Error display --%>
  <%--<tiles:insertDefinition name="Error"/>--%>

  <form:form action="/hosts/save" method="post" modelAttribute="form">
    <table class="formtable">
      <col class="labelcol"/>
      <col class="entrycol"/>
      <tr class="zebraeven">
        <td>
          <label class="question required" for="txtHost">
            <spring:message code="label.hosts.host"/>
          </label>
        </td>
        <td align="left">
            <%--size="70"  styleId="txtHost"/>--%>
          <form:input path="${host}" id="txtHost"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question required" for="txtUsername">
            <spring:message code="label.hosts.username"/>
          </label>
        </td>
        <td align="left">
            <%--size="70"  styleId="txtUsername"/>--%>
          <form:input path="${username}" id="txtUsername"/>
        </td>
      </tr>
      <tr class="zebraeven">
        <td>
          <label class="question" for="txtPassword">
            <spring:message code="label.hosts.password"/>
          </label>
        </td>
        <td align="left">
            <%--size="70"  styleId="txtPassword"/>--%>
          <form:input path="${password}" id="txtPassword"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&#160;</td>
      </tr>
      <tr>
        <td>&#160;</td>
        <td>
          <input type="submit" styleClass="button" title="Save"/>
          <spring:message code="label.cancel" var="cancelLabel"/>
          <input type="submit" styleClass="button" value="cancel" title="${cancelLabel}"/>
        </td>
      </tr>
    </table>
  </form:form>