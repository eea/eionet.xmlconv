<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>


<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<spring:message code="label.config.db"/>'><bean:message
              key="label.config.db"/></span></li>
      <li>
        <a href="/do/systemForm" titleKey="label.config.system" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.system"/>
        </a>
      </li>
      <li>
        <a href="/do/purgeForm" titleKey="label.config.purge" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.purge"/>
        </a>
      </li>
      <li>
        <a href="/do/ldapForm" titleKey="label.config.ldap" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.ldap"/>
        </a>
      </li>
      <li>
        <a href="/do/basexForm" titleKey="label.config.basexserver" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.basexserver"/>
        </a>
      </li>
    </ul>
  </div>


  <ed:breadcrumbs-push label="DB configuration" level="1"/>
  <h1><spring:message code="label.config.db.admin"/></h1>




  <html:form action="/db" method="post">
    <table class="formtable">
      <col style="width:25%"/>
      <col style="width:75%"/>
      <tr>
        <td>
          <label class="question" for="dbUrl"><spring:message code="label.config.db.url"/></label>
        </td>
        <td>
          <form:input property="dbUrl" maxlength="255" style="width: 30em;" styleId="dbUrl"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="user"><spring:message code="label.config.db.user"/></label>
        </td>
        <td>
          <form:input property="user" maxlength="255" style="width: 30em;" styleId="user"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="password"><spring:message code="label.config.db.password"/></label>
        </td>
        <td>
          <html:password property="password" maxlength="255" style="width: 30em;" styleId="password"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <html:submit styleClass="button"><spring:message code="label.config.db.update"/></html:submit>
        </td>
      </tr>
    </table>
  </html:form>

</div>
