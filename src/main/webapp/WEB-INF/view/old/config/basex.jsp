<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<%--<html:xhtml />--%>
<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li>
        <a href="/do/dbForm" titleKey="label.config.db" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.db"/>
        </a>
      </li>
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
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title='<spring:message code="label.config.basexserver"/>'><bean:message
              key="label.config.basexserver"/></span></li>
    </ul>
  </div>

  <ed:breadcrumbs-push label="BaseX Server configuration" level="1"/>
  <h1><spring:message code="label.config.basexserver.title"/></h1>



  <form:form servletRelativeAction="basex" method="post">
    <table class="formtable">
      <col style="width:25%"/>
      <col style="width:75%"/>
      <tr>
        <td>
          <label class="question" for="host"><spring:message code="label.config.basexserver.host"/></label>
        </td>
        <td>
          <form:input property="host" maxlength="255" style="width: 30em;" styleId="host"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="port"><spring:message code="label.config.basexserver.port"/></label>
        </td>
        <td>
          <form:input property="port" maxlength="255" style="width: 30em;" styleId="port"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="user"><spring:message code="label.config.basexserver.user"/></label>
        </td>
        <td>
          <form:input property="user" maxlength="255" style="width: 30em;" styleId="user"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="password"><spring:message code="label.config.basexserver.password"/></label>
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
          <button class="button" name="action" value="submit"><bean:message
                  key="label.config.basexserver.update"/></button>
        </td>
      </tr>
    </table>
  </form:form>
</div>
