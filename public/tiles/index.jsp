<%@ page contentType="text/html; charset=UTF-8" 
  import="java.util.List"
  import="java.util.Set"
  import="java.util.Iterator"
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>


<ed:breadcrumbs-push label="Visible Channels" level="1"/>

<html:xhtml/>
	<div style="width:100%;">
		<div id="tabbedmenu">
			<ul>
				<li><a onclick="return submitTab(this);" style="color: black; text-decoration: none;" title="<bean:message key="label.profile.edit"/>" href="<bean:write name="webRoot" />/profile/edit.do"><bean:message key="label.profile.edit"/></a></li>
				<li class="currenttab"><span style="color: black; text-decoration: none;" title="<bean:message key="label.profile.channels.title"/>"><bean:message key="label.profile.channels.title"/></span></li>
			</ul>
		</div>
		<p>&nbsp;</p>
				
		<logic:present name="profile.channels">
		<logic:notEmpty name="profile.channels">
			<html:form action="/profile/save.do" method="post">
			  <table width="90%" cellpadding="0" cellspacing="0" border="0" align="left">
			    <tr>
			      <td width="100%" colspan="3">
			        <bean:message key="label.profile.channels.select"/>:
					<table class="sortable" align="center" width="97%">
						<tr>
							<th scope="col"><span title="Visible"><bean:message key="label.table.channel.visible"/></span></th>
							<th scope="col"><span title="Title"><bean:message key="label.table.channel.title"/></span></th>
							<th scope="col"><span title="Description"><bean:message key="label.table.channel.description"/></span></th>
						</tr>
							<logic:iterate indexId="index" id="ch" name="profile.channels" type="Channel">
								<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
									<td align="center" valign="top" width="2%">
									    <html:multibox property="channels" onclick="setIgnoreChannels('false');">
											<bean:write name="ch" property="id" />
									    </html:multibox>
									</td>
									<td valign="top" width="38%">
										<bean:write name="ch" property="title" />
									</td>
									<td valign="top" width="60%">
										<bean:write name="ch" property="description" />
									</td>
								</tr>
							</logic:iterate>
					</table>
			      </td>
			    </tr>
			    <tr>
			      <td colspan="3">&nbsp;</td>
			    </tr>
			    <tr>
			      <td colspan="3" align="center">
			      	<html:hidden property="ignoreChannels"/>
			        <html:submit styleClass="button">
			        	<bean:message key="label.profile.save"/>
			        </html:submit>
			        <html:cancel styleClass="button">
			        	<bean:message key="label.cancel"/>
			        </html:cancel>
			      </td>
			    </tr>
			  </table>
			</html:form>
		</logic:notEmpty>
		</logic:present>
		
</div>