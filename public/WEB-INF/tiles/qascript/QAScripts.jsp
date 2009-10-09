<%@ page contentType="text/html; charset=UTF-8"  import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>
<html:xhtml/>

<ed:breadcrumbs-push label="QA Scripts" level="1" />


	<logic:equal name="ssdPrm" value="true"  name="qascript.qascriptList" scope="session" property="ssiPrm" >
		<div id="operations">
			<ul>
		   		<li>
 					<html:link page="/add_query.jsp"><bean:message key="label.qascript.add" /></html:link>
		   		</li>
			</ul>
		</div>
	</logic:equal>

	<h1 class="documentFirstHeading">
		<bean:message key="label.qascript.title"/>
	</h1>

	<%-- include Error display --%>
	<tiles:insert definition="Error" />

	<div class="visualClear">&nbsp;</div>

	<logic:present name="qascripts" name="qascript.qascriptList" scope="session" property="qascripts" >
		<div style="width: 97%">
			<table class="datatable" width="100%">
				<col style="width:10%"/>
				<col style="width:52%"/>
				<col style="width:38%"/>
				<thead>
					<tr>
						<th scope="col" class="scope-col"><bean:message key="label.table.qascript.action"/></th>
						<th scope="col" class="scope-col"><bean:message key="label.table.qascript.xmlschema"/></th>
						<th scope="col" class="scope-col"><bean:message key="label.table.qascript.qascripts"/></th>
					</tr>
				</thead>
				<tbody>
					<logic:iterate indexId="index" id="schema" name="qascript.qascriptList" scope="session" property="qascripts" type="Schema">
					<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
						<td align="center">
	    					<html:link page="/queries.jsp" paramId="ID" paramName="schema" paramProperty="id">
								<html:img page="/images/properties.gif" altKey="label.table.qascript.qascripts" title="view QA scripts for this XML Schema" />
							</html:link>
							<a href="viewSchemaForm?backToConv=yes&amp;schemaId=<bean:write name="schema" property="id" />">
								<html:img page="/images/info_icon.gif" altKey="label.table.schemainfo" title="view schema info"/></a>
						</td>
						<td title="<bean:write name="schema" property="schema"/>">
							<bean:write name="schema" property="schema" />
						</td>
						<td>
							<logic:iterate id="qascript" name="schema" scope="page" property="qascripts" type="QAScript">
							<a  href="<bean:write name="webRoot"/>/<bean:write name="qascript" property="fileName" />" title="<bean:write name="qascript" property="description" />">
								<bean:write name="qascript" property="shortName" />
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

</logic:present>



