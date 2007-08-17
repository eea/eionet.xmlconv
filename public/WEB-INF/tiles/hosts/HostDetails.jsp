<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<logic:present name="user">
	<bean:define id="username" name="user" scope="session"/>
</logic:present>

<html:xhtml/>

		<ed:breadcrumbs-push label="Host details" level="2" />
		<h1 class="documentFirstHeading">
			<logic:empty name="HostForm" property="id">
				<bean:message key="label.hosts.add_title"/>
			</logic:empty>
			<logic:notEmpty name="HostForm" property="id">
				<bean:message key="label.hosts.edit"/>
			</logic:notEmpty>
		</h1>
	
			<html:form action="/hosts/save" method="post">
				<table class="datatable">
				  <col style="width:16%"/>
				  <col style="width:84%"/>
					  <tr>
						<th scope="row" class="scope-row">
							<bean:message key="label.hosts.host"/>
						</th>
						<td align="left">
							<html:text property="host" size="70" />
						</td>
					</tr>		
					<tr>
						<th scope="row" class="scope-row">
							<bean:message key="label.hosts.username"/>
						</th>
						<td align="left">
							<html:text property="username" size="70" />
						</td>
					</tr>		
					<tr>
						<th scope="row" class="scope-row">
							<bean:message key="label.hosts.password"/>
						</th>
						<td align="left">
							<html:text property="password" size="70" />
						</td>
					</tr>		
					<tr><td>&#160;</td><td>&#160;</td></tr>
					<tr>
						<td colspan="2" align="center">
							<html:submit styleClass="button">Save</html:submit>
							<html:cancel styleClass="button">
					        	<bean:message key="label.cancel"/>
					        </html:cancel>
						</td>
					</tr>
				</table>
			</html:form>	

