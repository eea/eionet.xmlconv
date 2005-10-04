<%@ page contentType="text/html; charset=UTF-8" 
  import="java.util.List"
  import="java.util.Iterator"
  import="eionet.gdem.dto.*"
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>

<ed:breadcrumbs-push label="Stylesheets" level="1" />

<logic:present name="stylesheet.stylesheetList">
	<h1 class="documentFirstHeading">
		<bean:message key="label.stylesheet.handcoded"/>
	</h1>
	
	<div class="visualClear">&nbsp;</div>

	<div style="width: 97%">
		<table class="sortable" align="center" width="100%">
			<tr>
				<th scope="col"><span title="Action"><bean:message key="label.table.stylesheet.action"/></span></th>
				<th scope="col"><span title="Title"><bean:message key="label.table.stylesheet.title"/></span></th>
				<th scope="col"><span title="Stylesheets"><bean:message key="label.table.stylesheet.stylesheets"/></span></th>				
			</tr>
				<logic:iterate indexId="index" id="schema" name="stylesheet.stylesheetList" scope="session" property="handCodedStylesheets" type="Schema">
				<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
					<td width="7%" align="center">
						<a href="schemaStylesheets.do?schema=<bean:write name="schema" property="schema" />">							
							<img src="<bean:write name="webRoot"/>/images/properties.gif" alt="<bean:message key="label.table.stylesheet" />" title="view stylesheets" width="16" height="16"/>
						</a>
						<a href="schemaElemForm.do?schemaId=<bean:write name="schema" property="id" />">							
							<img src="<bean:write name="webRoot"/>/images/info_icon.gif" alt="<bean:message key="label.table.schemainfo" />" title="view schema info" width="16" height="16"/>
						</a>
						<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssdPrm" >
							<a href="deleteSchema.do?schemaId=<bean:write name="schema" property="id" />"
								onclick='return schemaDelete("<bean:write name="schema" property="schema" />");'>
									<img src="<bean:write name="webRoot"/>/images/delete.gif" alt="<bean:message key="label.delete" />" title="delete schema" width="15" height="15"/>
							</a>
	    				</logic:equal>
					</td>				
					<td width="55%">						
							<bean:write name="schema" property="schema" />
					</td>
					<td width="38%">
						<logic:iterate id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">						
						<a target="blank" href="<bean:write name="stylesheet" property="xsl" />" title="<bean:write name="stylesheet" property="xsl_descr" />">						
							<bean:write name="stylesheet" property="type" />
						</a>&#160;
						</logic:iterate>
					</td>					
				</tr>
				</logic:iterate>
				<tr>
					<td valign="top" colspan="2">
					</td>
				</tr>
		</table>
	</div>
	
	<div class="visualClear">&nbsp;</div>
	

	<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssiPrm" >
		
	<div class="boxbottombuttons">
	<form action="addStylesheetForm.do">
		<input class="button" type="submit" value="<bean:message key="label.stylesheet.add" />"/>
	</form>
	</div>
	
	</logic:equal>

	<h1 class="documentFirstHeading">
		<bean:message key="label.stylesheet.generated"/>
	</h1>
	
	<div class="visualClear">&nbsp;</div>

	<div style="width: 97%">
		<table class="sortable" align="center" width="100%">
			<tr>
				<th scope="col"><span title="Action"><bean:message key="label.table.stylesheet.action"/></span></th>
				<th scope="col"><span title="Table"><bean:message key="label.table.stylesheet.table"/></span></th>
				<th scope="col"><span title="Dataset"><bean:message key="label.table.stylesheet.dataset"/></span></th>
				<th scope="col"><span title="Title"><bean:message key="label.table.stylesheet.title"/></span></th>								
				<th scope="col"><span title="Stylesheets"><bean:message key="label.table.stylesheet.stylesheets"/></span></th>
			</tr>
				<logic:iterate indexId="index" id="schema" name="stylesheet.stylesheetList" scope="session" property="ddStylesheets" type="Schema">				
				<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
					<td width="7%" align="center">
						<a href="schemaStylesheets.do?schema=<bean:write name="schema" property="schema" />">							
							<img src="<bean:write name="webRoot"/>/images/properties.gif" alt="<bean:message key="label.table.stylesheet" />" title="view stylesheets" width="16" height="16"/>
						</a>
					</td>					
					<td width="10%">
							<bean:write name="schema" property="table" />
					</td>
					<td width="20%">
							<bean:write name="schema" property="dataset" />
					</td>
					<td width="35%">
							<bean:write name="schema" property="schema" /> 
					</td>
					<td width="28%">
						<logic:iterate id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">						
						<a target="blank" href="<bean:write name="stylesheet" property="xsl" />" title="<bean:write name="stylesheet" property="xsl_descr" />">						
							<bean:write name="stylesheet" property="xsl_descr" />
						</a>&#160;
						</logic:iterate>					
					</td>
				</tr>
				</logic:iterate>
				<tr>
					<td valign="top" colspan="2">
					</td>
				</tr>
		</table>
	</div>
	


</logic:present>



