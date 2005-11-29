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

<div id="stylesheet" class="box"> 
	<div class="boxleft"> 
		<div class="boxtop"><div>&nbsp;</div></div> 
		<ed:breadcrumbs-push label="Host details" level="2" />
		<h4 class="documentFirstHeading">
			<logic:empty name="HostForm" property="id">
				<bean:message key="label.hosts.add_title"/>
			</logic:empty>
			<logic:notEmpty name="HostForm" property="id">
				<bean:message key="label.hosts.edit"/>
			</logic:notEmpty>
		</h4>
	
		<div class="boxcontent" >
			<html:form action="/hosts/save" method="post">
				<div style="padding-left:50px"><br/>
				<table cellspacing="0">
					<tr>
						<td align="right" style="padding-right:5">
							<bean:message key="label.hosts.host"/>
						</td>
						<td align="left">
							<html:text property="host" size="70" />
						</td>
					</tr>		
					<tr>
						<td align="right" style="padding-right:5">
							<bean:message key="label.hosts.username"/>
						</td>
						<td align="left">
							<html:text property="username" size="70" />
						</td>
					</tr>		
					<tr>
						<td align="right" style="padding-right:5">
							<bean:message key="label.hosts.password"/>
						</td>
						<td align="left">
							<html:text property="password" size="70" />
						</td>
					</tr>		
					<tr><td>&#160;</td><td>&#160;</td></tr>
					<tr>
						<td>&#160;</td>
						<td>
							<html:submit styleClass="button">Save</html:submit>
						</td>
					</tr>
				</table>
				</div>
			</html:form>	
		</div>
		<div class="boxbottom"><div>&nbsp;</div></div> 
	</div>
</div>

