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

	<%-- include Error display --%>
	<tiles:insert definition="Error" />

	<div class="visualClear">&nbsp;</div>

	<logic:present name="handCodedStylesheets" name="stylesheet.stylesheetList" scope="session" property="handCodedStylesheets" >
		<div style="width: 97%">
			<table class="datatable" width="100%">
				<col style="width:10%"/>
				<col style="width:52%"/>
				<col style="width:38%"/>
				<thead>
					<tr>
						<th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.action"/></th>
						<th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.title"/></th>
						<th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.stylesheets"/></th>
					</tr>
				</thead>
				<tbody>
					<logic:iterate indexId="index" id="schema" name="stylesheet.stylesheetList" scope="session" property="handCodedStylesheets" type="Schema">
					<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
						<td align="center">
	    					<html:link page="/do/schemaStylesheets" paramId="schema" paramName="schema" paramProperty="schema">
								<html:img page="/images/properties.gif" altKey="label.table.stylesheet" title="view stylesheets" />
							</html:link>
							<a href="viewSchemaForm?backToConv=yes&amp;schemaId=<bean:write name="schema" property="id" />">
								<html:img page="/images/info_icon.gif" altKey="label.table.schemainfo" title="view schema info"/></a>
							<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssdPrm" >
								<a href="deleteSchema?schemaId=<bean:write name="schema" property="id" />" onclick='return schemaDelete("<bean:write name="schema" property="schema" />");'>
									<html:img page="/images/delete.gif" altKey="label.delete" title="delete schema" /></a>
		    				</logic:equal>
						</td>
						<td title="<bean:write name="schema" property="schema"/>">
							<bean:write name="schema" property="schema" />
						</td>
						<td>
							<logic:iterate id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">
							<a  href="<bean:write name="webRoot"/>/<bean:write name="stylesheet" property="xsl" />" title="<bean:write name="stylesheet" property="xsl_descr" />">
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
	</logic:present>
	<logic:notPresent name="handCodedStylesheets" name="stylesheet.stylesheetList" scope="session" property="handCodedStylesheets" >
		<div class="success">
			<bean:message key="label.stylesheet.noHandCodedConversions"/>
		</div>
	</logic:notPresent>

	<div class="visualClear">&nbsp;</div>


	<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssiPrm" >

	<div class="boxbottombuttons">
	<form action="addStylesheetForm">
		<div>
			<input class="button" type="submit" value="<bean:message key="label.stylesheet.add" />"/>
		</div>
	</form>
	</div>

	</logic:equal>

</logic:present>



