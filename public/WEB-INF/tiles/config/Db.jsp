<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>

<html:xhtml />
<div style="width:100%;">
    <div id="tabbedmenu">
        <ul>
            <li>
                <html:link page="/do/systemForm"   titleKey="label.config.system" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                    <bean:message key="label.config.system" />
                </html:link>
            </li>
            <li id="currenttab"><span style="color: black; text-decoration: none;" title='<bean:message key="label.config.db"/>'><bean:message key="label.config.db" /></span></li>
            <li>
                <html:link page="/do/ldapForm"   titleKey="label.config.ldap" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                    <bean:message key="label.config.ldap" />
                </html:link>
            </li>
        </ul>
	</div>
	<p>&nbsp;</p>


		<ed:breadcrumbs-push label="DB configuration" level="1" />
		<h1><bean:message key="label.config.db.admin" /></h1>

		<%-- include Error display --%>
		<tiles:insert definition="Error" />

			<html:form action="/db" method="post">
			    <table class="formtable">
				  <col style="width:25%"/>
				  <col style="width:75%"/>
					  <tr>
						<td>
			            	<label class="question" for="dbUrl"><bean:message key="label.config.db.url" /></label>
			            </td>
			            <td>
			            	<html:text property="dbUrl" maxlength="255" style="width: 30em;" styleId="dbUrl"/>
			            </td>
			        </tr>
			        <tr>
						<td>
			            	<label class="question" for="user"><bean:message key="label.config.db.user" /></label>
			            </td>
			            <td>
			            	<html:text property="user" maxlength="255" style="width: 30em;" styleId="user"/>
			            </td>
			        </tr>
			        <tr>
						<td>
			            	<label class="question" for="password"><bean:message key="label.config.db.password" /></label>
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
			                <html:submit styleClass="button"><bean:message key="label.config.db.update" /></html:submit>
			            </td>
			        </tr>
			    </table>
			</html:form>

</div>
