<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
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

		<%-- include Error display --%>
		<tiles:insert definition="Error" />
	
			<html:form action="/hosts/save" method="post">
			  <table class="formtable">
				<col class="labelcol"/>
				<col class="entrycol"/>
				<tr class="zebraeven">
						<td>
							<label class="question required" for="txtHost"> 
				    			<bean:message key="label.hosts.host"/>
				    		</label>
						</td>
						<td align="left">
							<html:text property="host" size="70"  styleId="txtHost"/>
						</td>
					</tr>		
					<tr>
						<td>
							<label class="question required" for="txtUsername"> 
								<bean:message key="label.hosts.username"/>
							</label>
						</td>
						<td align="left">
							<html:text property="username" size="70"  styleId="txtUsername"/>
						</td>
					</tr>		
				<tr class="zebraeven">
						<td>
							<label class="question" for="txtPassword"> 
								<bean:message key="label.hosts.password"/>
							</label>
						</td>
						<td align="left">
							<html:text property="password" size="70"  styleId="txtPassword"/>
						</td>
					</tr>		
					<tr><td colspan="2">&#160;</td></tr>
					<tr>
						<td>&#160;</td>
						<td>
							<html:submit styleClass="button">Save</html:submit>
							<html:cancel styleClass="button">
					        	<bean:message key="label.cancel"/>
					        </html:cancel>
						</td>
					</tr>
				</table>
			</html:form>	

