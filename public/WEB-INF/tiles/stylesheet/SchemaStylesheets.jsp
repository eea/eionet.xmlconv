<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed"%>
<html:xhtml/>

<ed:breadcrumbs-push label="Schema stylesheets" level="2" />

<logic:present name="schema.stylesheets">

	<logic:iterate indexId="index" id="schema" name="schema.stylesheets" scope="session" property="handCodedStylesheets" type="Schema">
			<h1 class="documentFirstHeading">
				<bean:message key="label.schema.stylesheets"/>&nbsp;<bean:write name="schema" property="schema" />
			</h1>
		
	</logic:iterate>

	<%-- include Error display --%>
	<tiles:insert definition="Error" />
			
	<logic:iterate indexId="index" id="schema" name="schema.stylesheets" scope="session" property="handCodedStylesheets" type="Schema">
			<div class="visualClear">&nbsp;</div>
			<logic:equal  value="true"  name="schema.stylesheets" scope="session" property="handcoded" >
				<div>
					<a href="schemaElemForm?schemaId=<bean:write name="schema" property="id"/>">
						<bean:message key="label.schema.info"/>
					</a>
				</div>
				<div class="visualClear">&nbsp;</div>
			</logic:equal>

			<logic:present name="stylesheets" name="schema" scope="page" property="stylesheets" >
				<div style="width: 97%">
					<table class="datatable" width="100%">
						<col style="width:4%"/>
						<col style="width:3%"/>
						<col style="width:3%"/>
						<col style="width:10%"/>
						<col style="width:20%"/>
						<col style="width:40%"/>
						<col style="width:20%"/>
						<thead>
							<tr>
								<th scope="col" colspan="3"><bean:message key="label.table.stylesheet.action"/></th>
								<th scope="col"><bean:message key="label.table.stylesheet.type"/></th>
								<th scope="col"><bean:message key="label.table.stylesheet.description"/></th>
								<th scope="col"><bean:message key="label.table.stylesheet.stylesheet"/></th>
								<th scope="col"><bean:message key="label.table.stylesheet.modified"/></th>
							</tr>
						</thead>
						<tbody>
							<logic:iterate indexId="index" id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">
								<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
									<td align="center">
										<a href="searchCR?schemaUrl=<bean:write name="schema" property="schema" />&amp;conversionId=<bean:write name="stylesheet" property="convId" />"  >
											<html:img page="/images/run.gif" altKey="label.stylesheet.run" title="run conversion"/>
										</a>
									</td>
										<logic:equal name="ssdPrm" value="false"  name="stylesheet"  property="ddConv" >
											<logic:equal name="ssdPrm" value="true"  name="schema.stylesheets" scope="session" property="ssdPrm" >
											<td align="center">
												<a href="stylesheetEditForm?stylesheetId=<bean:write name="stylesheet" property="convId" />">
													<html:img page="/images/edit.gif" altKey="label.stylesheet.edit" title="edit stylesheet"/></a>
											</td>
											<td align="center">
												<a href="deleteStylesheet?stylesheetId=<bean:write name="stylesheet" property="convId" />&amp;schema=<bean:write name="schema" property="schema"/>"
													onclick='return stylesheetDelete("<bean:write name="stylesheet" property="xsl" />");'>
													<html:img page="/images/delete.gif" altKey="label.delete" title="delete stylesheet"/></a>
											</td>
											</logic:equal>
											<logic:notEqual name="ssdPrm" value="true"  name="schema.stylesheets" scope="session" property="ssdPrm" >
												<td colspan="2"/>
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="ssdPrm" value="false"  name="stylesheet"  property="ddConv" >
											<td colspan="2"/>
										</logic:notEqual>
									<td align="center">
										<bean:write name="stylesheet" property="type" />
									</td>
									<td>
										<bean:write name="stylesheet" property="xsl_descr" />
									</td>
									<td>
										<logic:notEqual name="ssdPrm" value="false"  name="stylesheet"  property="ddConv" >
											<a  href="<bean:write name="stylesheet" property="xsl" />">
												<bean:write name="stylesheet" property="xsl" />
											</a>&#160;
										</logic:notEqual>
										<logic:equal name="ssdPrm" value="false"  name="stylesheet"  property="ddConv" >
											<a  href="<bean:write name="webRoot"/>/<bean:write name="stylesheet" property="xsl" />">
												<bean:write name="webRoot"/>/<bean:write name="stylesheet" property="xsl" />
											</a>&#160;
										</logic:equal>
									</td>
									<td align="center">
										<bean:write name="stylesheet" property="modified" />
									</td>
								</tr>
							</logic:iterate>
							<tr>
								<td valign="top" colspan="7">
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</logic:present>
			<logic:notPresent name="stylesheets" name="schema" scope="page" property="stylesheets" >
				<div class="success">
					<bean:message key="label.schema.noStylesheets"/>
				</div>
			</logic:notPresent>
	</logic:iterate>

	<div class="visualClear">&nbsp;</div>
	<div class="boxbottombuttons">
		<logic:equal name="ssiPrm" value="true"  name="schema.stylesheets" scope="session" property="ssiPrm" >
			<table width="100%">
				<col width="50%"/>
				<col width="50%"/>
				<tr>
					<td align="right">
						<form action="addStylesheetForm">
							<div>
								<logic:present name="schema" scope="request">
								    <input type="hidden" name="schema" value="<bean:write name="schema" scope="request"/>" />
								</logic:present>
								<input class="button" type="submit" value="<bean:message key="label.stylesheet.add" />"/>
							</div>
						</form>
					</td>
					<td align="left"> 
			<logic:present name="backList" scope="request">
				<logic:equal name="backList" scope="request" value="generated">
					<form action="generatedStylesheetList">
						<div>
							<input type="submit" class="button" value="<bean:message key="label.ok"/>" />
						</div>				
					</form>
				</logic:equal>
				<logic:equal name="backList" scope="request" value="uplschemas">
					<form action="uplSchemas">
						<div>
							<input type="submit" class="button" value="<bean:message key="label.ok"/>" />
						</div>				
					</form>
				</logic:equal>
				<logic:equal name="backList" scope="request" value="">
					<form action="stylesheetList">
						<div>
							<input type="submit" class="button" value="<bean:message key="label.ok"/>" />
						</div>				
					</form>
				</logic:equal>
			</logic:present>
			<logic:notPresent name="backList" scope="request">
				<form action="stylesheetList">
					<div>
						<input type="submit" class="button" value="<bean:message key="label.ok"/>" />
					</div>				
				</form>
			</logic:notPresent>					</td>
				</tr>
			</table>
		</logic:equal>
		<logic:notEqual name="ssiPrm" value="true"  name="schema.stylesheets" scope="session" property="ssiPrm" >
			<logic:present name="backList" scope="request">
				<logic:equal name="backList" scope="request" value="generated">
					<form action="generatedStylesheetList">
						<div>
							<input type="submit" class="button" value="<bean:message key="label.ok"/>" />
						</div>				
					</form>
				</logic:equal>
				<logic:equal name="backList" scope="request" value="uplschemas">
					<form action="uplSchemas">
						<div>
							<input type="submit" class="button" value="<bean:message key="label.ok"/>" />
						</div>				
					</form>
				</logic:equal>
				<logic:equal name="backList" scope="request" value="">
					<form action="stylesheetList">
						<div>
							<input type="submit" class="button" value="<bean:message key="label.ok"/>" />
						</div>				
					</form>
				</logic:equal>
			</logic:present>
			<logic:notPresent name="backList" scope="request">
				<form action="stylesheetList">
					<div>
						<input type="submit" class="button" value="<bean:message key="label.ok"/>" />
					</div>				
				</form>
			</logic:notPresent>
		</logic:notEqual>
	</div>
</logic:present>



