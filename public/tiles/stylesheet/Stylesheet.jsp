<%@ page contentType="text/html; charset=UTF-8"  import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>
<html:xhtml/>

<ed:breadcrumbs-push label="Stylesheets" level="1" />

<logic:present name="stylesheet.stylesheetList">
	<h1 class="documentFirstHeading">
		<bean:message key="label.stylesheet.handcoded"/>
	</h1>
	
	<div class="visualClear">&nbsp;</div>

	<div style="width: 97%">
		<table class="sortable" align="center" width="100%">
			<thead>
				<tr>
					<th scope="col"><span title="Action"><bean:message key="label.table.stylesheet.action"/></span></th>
					<th scope="col"><span title="Title"><bean:message key="label.table.stylesheet.title"/></span></th>
					<th scope="col"><span title="Stylesheets"><bean:message key="label.table.stylesheet.stylesheets"/></span></th>				
				</tr>
			</thead>
			<tbody>
				<logic:iterate indexId="index" id="schema" name="stylesheet.stylesheetList" scope="session" property="handCodedStylesheets" type="Schema">
				<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
					<td width="7%" align="center" nowrap="nowrap">
    					<html:link page="/do/schemaStylesheets" paramId="schema" paramName="schema" paramProperty="schema">
							<html:img page="/images/properties.gif" altKey="label.table.stylesheet" title="view stylesheets" />
						</html:link>
						<a href="schemaElemForm?backToConv=yes&amp;schemaId=<bean:write name="schema" property="id" />">		
							<html:img page="/images/info_icon.gif" altKey="label.table.schemainfo" title="view schema info"/>
						</a>
						<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssdPrm" >
							<a href="deleteSchema?schemaId=<bean:write name="schema" property="id" />"
								onclick='return schemaDelete("<bean:write name="schema" property="schema" />");'>
								<html:img page="/images/delete.gif" altKey="label.delete" title="delete schema" />
							</a>
	    				</logic:equal>
					</td>				
					<td width="55%">						
							<bean:write name="schema" property="schema" />
					</td>
					<td width="38%">
						<logic:iterate id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">						
						<a target="blank" href="<bean:write name="webRoot"/>/<bean:write name="stylesheet" property="xsl" />" title="<bean:write name="stylesheet" property="xsl_descr" />">						
							<bean:write name="stylesheet" property="type" />
						</a>&#160;
						</logic:iterate>
					</td>					
				</tr>
				</logic:iterate>
				<tr>
					<td valign="top" colspan="3">
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<div class="visualClear">&nbsp;</div>
	

	<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssiPrm" >
		
	<div class="boxbottombuttons">
	<form action="addStylesheetForm">
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
			<thead>
				<tr>
					<th scope="col"><span title="Action"><bean:message key="label.table.stylesheet.action"/></span></th>
					<th scope="col"><span title="Table"><bean:message key="label.table.stylesheet.table"/></span></th>
					<th scope="col"><span title="Dataset"><bean:message key="label.table.stylesheet.dataset"/></span></th>
					<th scope="col"><span title="Title"><bean:message key="label.table.stylesheet.title"/></span></th>								
					<th scope="col"><span title="Stylesheets"><bean:message key="label.table.stylesheet.stylesheets"/></span></th>
				</tr>
			</thead>
			<tbody>
				<logic:iterate indexId="index" id="schema" name="stylesheet.stylesheetList" scope="session" property="ddStylesheets" type="Schema">				
				<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
					<td width="7%" align="center">
						<html:link action="/schemaStylesheets" paramId="schema" paramName="schema" paramProperty="schema">
							<html:img page="/images/properties.gif" altKey="label.table.stylesheet" title="view stylesheets" />
						</html:link>
					</td>					
					<td width="10%">
							<bean:write name="schema" property="table" />
					</td>
					<td width="20%">
							<bean:write name="schema" property="dataset" />
					</td>
					<td width="15%">							
						<a target="blank" href="<bean:write name="schema" property="schema" />" title="<bean:write name="schema" property="schema" />">						
							<bean:write name="schema" property="id" />
						</a>													
					</td>
					<td width="48%">
						<logic:iterate id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">						
						<a target="blank" href="<bean:write name="stylesheet" property="xsl" />" title="<bean:write name="stylesheet" property="xsl_descr" />">						
							<bean:write name="stylesheet" property="xsl_descr" />
						</a>&#160;
						</logic:iterate>					
					</td>
				</tr>
				</logic:iterate>
				<tr>
					<td valign="top" colspan="5">
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	


</logic:present>



