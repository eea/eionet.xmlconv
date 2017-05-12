<%--<%@ page contentType="text/html; charset=UTF-8"%>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<html:xhtml/>
<div style="width:100%;">
  <div id="tabbedmenu">
    <ul>
      <li>
        <html:link page="/do/dbForm" titleKey="label.config.db" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <bean:message key="label.config.db"/>
        </html:link>
      </li>
      <li>
        <html:link page="/do/systemForm" titleKey="label.config.system" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <bean:message key="label.config.system"/>
        </html:link>
      </li>
      <li>
        <html:link page="/do/purgeForm" titleKey="label.config.purge" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <bean:message key="label.config.purge"/>
        </html:link>
      </li>
      <li id="currenttab"><span style="color: black; text-decoration: none;"
                                title="<bean:message key="label.config.ldap"/>"><bean:message
              key="label.config.ldap"/></span></li>
      <li>
        <html:link page="/do/basexForm" titleKey="label.config.basexserver" onclick="return submitTab(this);"
                   style="color: black; text-decoration: none;">
          <bean:message key="label.config.basexserver"/>
        </html:link>
      </li>
    </ul>
  </div>


  <ed:breadcrumbs-push label="LDAP configuration" level="1"/>
  <h1><bean:message key="label.config.ldap.admin"/></h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <html:form action="/ldap" method="post">
    <table class="formtable">
      <col style="width:26%"/>
      <col style="width:74%"/>
      <tr>
        <td>
          <label class="question" for="url"><bean:message key="label.config.ldap.url"/></label>
        </td>
        <td>
          <html:text property="url" maxlength="255" style="width: 30em;" styleId="url"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="context"><bean:message key="label.config.ldap.context"/></label>
        </td>
        <td>
          <html:text property="context" maxlength="255" style="width: 30em;" styleId="context"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="userDir"><bean:message key="label.config.ldap.userDir"/></label>
        </td>
        <td>
          <html:text property="userDir" maxlength="255" style="width: 30em;" styleId="userDir"/>
        </td>
      </tr>
      <tr>
        <td>
          <label class="question" for="attrUid"><bean:message key="label.config.ldap.attrUid"/></label>
        </td>
        <td>
          <html:text property="attrUid" maxlength="255" style="width: 30em;" styleId="attrUid"/>
        </td>
      </tr>
      <tr>
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="2" align="center">
          <html:submit styleClass="button">
            <bean:message key="label.config.ldap.save"/>
          </html:submit>
        </td>
      </tr>
    </table>
  </html:form>
</div>
