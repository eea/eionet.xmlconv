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
                <html:link page="/do/ldapForm"   titleKey="label.config.ldap" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                    <bean:message key="label.config.ldap" />
                </html:link>
            </li>
            <li id="currenttab"><span style="color: black; text-decoration: none;" title='<bean:message key="label.config.db"/>'><bean:message key="label.config.db" /></span></li>
        </ul>
	</div>
	<p>&nbsp;</p>


		<ed:breadcrumbs-push label="DB configuration" level="1" />
		<h1><bean:message key="label.config.db.admin" /></h1>

			<html:form action="/db" method="post">
			    <table class="datatable">
				  <col style="width:16%"/>
				  <col style="width:84%"/>
					  <tr>
						<th scope="row" class="scope-row">
			            	<bean:message key="label.config.db.url" />:
			            </th>
			            <td>
			            	<html:text property="dbUrl" maxlength="255" style="width: 30em;" />
			            </td>
			        </tr>
			        <tr>
						<th scope="row" class="scope-row">
			            	<bean:message key="label.config.db.user" />:
			            </th>
			            <td>
			            	<html:text property="user" maxlength="255" style="width: 30em;" />
			            </td>
			        </tr>
			        <tr>
						<th scope="row" class="scope-row">
			            	<bean:message key="label.config.db.password" />:
			            </th>
			            <td>
			            	<html:password property="password" maxlength="255" style="width: 30em;" />
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
