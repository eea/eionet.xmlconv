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
	        <li id="currenttab"><span style="color: black; text-decoration: none;" title="<bean:message key="label.config.ldap"/>"><bean:message key="label.config.ldap" /></span></li>
	        <li>
	            <html:link page="/do/dbForm" titleKey="label.config.db" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
	                <bean:message key="label.config.db" />
	            </html:link>
	        </li>
	    </ul>
	</div>
	<p>&nbsp;</p>
	
			
			<ed:breadcrumbs-push label="LDAP configuration" level="1" />
			<h1><bean:message key="label.config.ldap.admin" /></h1>
			
			<%-- include Error display --%>
			<tiles:insert definition="Error" />

				<html:form action="/ldap" method="post">
				    <table class="datatable">
					  <col style="width:26%"/>
					  <col style="width:74%"/>
					  <tr>
							<th scope="row" class="scope-row">
				            	<bean:message key="label.config.ldap.url" />:
				            </th>
				            <td>
				            	<html:text property="url" maxlength="255" style="width: 30em;" />
				            </td>
						</tr>
						<tr>
							<th scope="row" class="scope-row">
				            	<bean:message key="label.config.ldap.context" />:
				            </th>
				            <td>
				            	<html:text property="context" maxlength="255" style="width: 30em;" />
				            </td>
						</tr>
						<tr>
							<th scope="row" class="scope-row">
				            	<bean:message key="label.config.ldap.userDir" />:
				            </th>
				            <td>
				            	<html:text property="userDir" maxlength="255" style="width: 30em;" />
				            </td>
						</tr>
						<tr>
							<th scope="row" class="scope-row">
				            	<bean:message key="label.config.ldap.attrUid" />:
				            </th>
				            <td>
				            	<html:text property="attrUid" maxlength="255" style="width: 30em;" />
				            </td>
				        </tr>
				        <tr>
				            <td colspan="2">&nbsp;</td>
				        </tr>
				        <tr>
				            <td colspan="2" align="center">
				                <html:submit styleClass="button">
				                    <bean:message key="label.config.ldap.save" />
				                </html:submit>
				            </td>
				        </tr>
				    </table>
				</html:form>
</div>
