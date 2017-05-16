<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>


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
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title="<spring:message code="label.config.ldap"/>"><bean:message
              key="label.config.ldap"/></span></li>
      <li>
        <a href="/do/basexForm" titleKey="label.config.basexserver" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <spring:message code="label.config.basexserver"/>
        </a>
      </li>
    </ul>
  </div>


  <ed:breadcrumbs-push label="LDAP configuration" level="1"/>
  <h1><spring:message code="label.config.ldap.admin"/></h1>




  <html:form action="/ldap" method="post">
    <table class="formtable">
      <col style="width:26%"/>
      <col style="width:74%"/>
      <tr>
        <td>
          <label class="question" for="url"><spring:message code="label.config.ldap.url"/></label>
        </td>
        <td>
          <form:input property="url" maxlength="255" style="width: 30em;" styleId="url"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="context"><spring:message code="label.config.ldap.context"/></label>
        </td>
        <td>
          <form:input property="context" maxlength="255" style="width: 30em;" styleId="context"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="userDir"><spring:message code="label.config.ldap.userDir"/></label>
        </td>
        <td>
          <form:input property="userDir" maxlength="255" style="width: 30em;" styleId="userDir"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="attrUid"><spring:message code="label.config.ldap.attrUid"/></label>
        </td>
        <td>
          <form:input property="attrUid" maxlength="255" style="width: 30em;" styleId="attrUid"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <html:submit styleClass="button">
            <spring:message code="label.config.ldap.save"/>
          </html:submit>
        </td>
      </tr>
    </table>
  </html:form>
</div>
