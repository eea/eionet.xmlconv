<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Login" level="1"/>

<br/>

<table width="100%">
  <tr>
    <td align="center">
      <h1><spring:message code="label.login.message"/></h1>

      <form:form action="login" method="post" focus="username" modelAttribute="loginForm">
        <table class="datatable results" style="width:300px">
          <col style="width:36%"/>
          <col style="width:64%"/>
          <tr>
            <th scope="row" class="scope-row">
              <spring:message code="label.login.username"/>:
            </th>
            <td>
              <form:input path="username" size="15"/>
            </td>
          </tr>
          <tr>
            <th scope="row" class="scope-row">
              <spring:message code="label.login.password"/>:
            </th>
            <td>
              <form:password path="password" size="15"/>
            </td>
          </tr>
          <tr>
            <td colspan="3">&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3" align="center">
              <button type="submit" styleClass="button">
                <spring:message code="label.login.submit"/>
              </input>
            </td>
          </tr>
        </table>
      </form:form>
    </td>
  </tr>
</table>
