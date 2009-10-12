<%@ page contentType="text/html; charset=UTF-8"  import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>
<html:xhtml/>

<ed:breadcrumbs-push label="Stylesheets" level="1" />

<logic:present name="stylesheet.stylesheetList">

	<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssiPrm" >
		<div id="operations">
			<ul>
				<li><a href="addStylesheetForm"><bean:message key="label.stylesheet.add" /></a></li>
			</ul>
		</div>
	</logic:equal>

	<h1 class="documentFirstHeading">
		<bean:message key="label.stylesheet.handcoded"/>
	</h1>

	<%-- include Error display --%>
	<tiles:insert definition="Error" />

	<div class="visualClear">&nbsp;</div>


	<logic:present name="handCodedStylesheets" name="stylesheet.stylesheetList" scope="session" property="handCodedStylesheets" >
		<html:form action="/deleteSchema" method="post">

			<table class="datatable" width="100%">
				<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssdPrm" >
					<col style="width:5%"/>
				</logic:equal>
				<col/>
				<col/>
				<thead>
					<tr>
						<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssdPrm" >
							<th scope="col" class="scope-col"></th>
						</logic:equal>
						<th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.title"/></th>
						<th scope="col" class="scope-col"><bean:message key="label.table.stylesheet.stylesheets"/></th>
					</tr>
				</thead>
				<tbody>
					<logic:iterate indexId="index" id="schema" name="stylesheet.stylesheetList" scope="session" property="handCodedStylesheets" type="Schema">
					<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
						<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssdPrm" >
							<td align="center">
								<bean:define id="schemaId" name="schema" property="id" />
								<html:radio property="schemaId" value="${schemaId}" />
							</td>
						</logic:equal> 
						<td title="<bean:write name="schema" property="schema"/>">
	    					<html:link page="/do/schemaStylesheets" paramId="schema" paramName="schema" paramProperty="schema"  title="view schema stylesheets" >
								<bean:write name="schema" property="schema" />
							</html:link>
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
			<logic:equal name="ssdPrm" value="true"  name="stylesheet.stylesheetList" scope="session" property="ssdPrm" >
				<div class="boxbottombuttons">
   					<input type="button"  class="button" value="<bean:message key="label.schema.delete"/>" onclick="return submitAction(1,'deleteSchema');" />
   				</div>
   			</logic:equal>
		</html:form>
	</logic:present>
	<logic:notPresent name="handCodedStylesheets" name="stylesheet.stylesheetList" scope="session" property="handCodedStylesheets" >
		<div class="success">
			<bean:message key="label.stylesheet.noHandCodedConversions"/>
		</div>
	</logic:notPresent>

	<div class="visualClear">&nbsp;</div>



</logic:present>



