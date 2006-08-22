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

<ed:breadcrumbs-push label="Hosts" level="1" />

<h1 class="documentFirstHeading">
	<bean:message key="label.hosts.title"/>
</h1>

<ed:hasPermission username="username" acl="host" permission="i">
<div id="operations">
	<ul><li><html:link page="/do/hosts/add">Add host</html:link>	</li></ul>
</div>
</ed:hasPermission>

<div class="visualClear">&nbsp;</div>

<logic:present name="hosts.list">
	<div style="width: 80%">
		<table class="datatable" align="center" width="100%">
			<thead>
				<tr>
					<th scope="col"><span title="Action">&nbsp;</span></th>
					<th scope="col"><span title="Host name"><bean:message key="label.hosts.host"/></span></th>
					<th scope="col"><span title="User name"><bean:message key="label.hosts.username"/></span></th>
				</tr>
			</thead>
			<tbody>
				<logic:iterate indexId="index" id="host" name="hosts.list">
					<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
						<td width="5%" class="center">
							<ed:hasPermission username="username" acl="host" permission="d">
								<html:link page="/do/hosts/delete" paramId="id" paramName="host" paramProperty="id" titleKey="label.hosts.delete">
									<html:img page="/images/delete.gif" altKey="label.delete" title="Delete host credentials" />
								</html:link>
							</ed:hasPermission>
						</td>
						<td>
							<ed:hasPermission username="username" acl="host" permission="u">
								<html:link page="/do/hosts/edit" paramId="id" paramName="host" paramProperty="id" titleKey="label.hosts.edit">
									<bean:write name="host" property="hostname" />
								</html:link>
							</ed:hasPermission>
						</td>
						<td>
							<bean:write name="host" property="username" />
						</td>
					</tr>
				</logic:iterate>
			</tbody>
		</table>
	</div>
</logic:present>

<div class="visualClear">&nbsp;</div>

