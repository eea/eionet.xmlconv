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
					<table class="sortable" align="center" width="100%">
						<thead>
							<tr>
								<th scope="col"><span title="Action"><bean:message key="label.table.stylesheet.action"/></span></th>			
								<th scope="col"><span title="Type"><bean:message key="label.table.stylesheet.type"/></span></th>
								<th scope="col"><span title="Description"><bean:message key="label.table.stylesheet.description"/></span></th>				
								<th scope="col"><span title="Stylesheet"><bean:message key="label.table.stylesheet.stylesheet"/></span></th>
								<th scope="col"><span title="Modified"><bean:message key="label.table.stylesheet.modified"/></span></th>								
							</tr>
						</thead>
						<tbody>
							<logic:iterate indexId="index" id="stylesheet" name="schema" scope="page" property="stylesheets" type="Stylesheet">										
								<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "" %>>
									<td width="10%" align="center" nowrap="nowrap">
										<a href="testConversionForm?schemaUrl=<bean:write name="schema" property="schema" />&amp;conversionId=<bean:write name="stylesheet" property="convId" />"  >
											<html:img page="/images/run.png" altKey="label.stylesheet.run" title="run conversion"/></a>			
										<logic:equal name="ssdPrm" value="false"  name="stylesheet"  property="ddConv" >
											<logic:equal name="ssdPrm" value="true"  name="schema.stylesheets" scope="session" property="ssdPrm" >						
												&nbsp;
												<a href="stylesheetEditForm?stylesheetId=<bean:write name="stylesheet" property="convId" />">
													<html:img page="/images/edit.gif" altKey="label.stylesheet.edit" title="edit stylesheet"/></a>						
												&nbsp;
												<a href="deleteStylesheet?stylesheetId=<bean:write name="stylesheet" property="convId" />&amp;schema=<bean:write name="schema" property="schema"/>"
													onclick='return stylesheetDelete("<bean:write name="stylesheet" property="xsl" />");'>
													<html:img page="/images/delete.gif" altKey="label.delete" title="delete stylesheet"/></a>	
											</logic:equal>								
										</logic:equal>		
									</td>
									<td width="5%" align="center">
										<bean:write name="stylesheet" property="type" />
									</td>
									<td width="20%">
										<bean:write name="stylesheet" property="xsl_descr" />
									</td>
									<td width="45%">
										<logic:notEqual name="ssdPrm" value="false"  name="stylesheet"  property="ddConv" >
											<a target="blank" href="<bean:write name="stylesheet" property="xsl" />">						
												<bean:write name="stylesheet" property="xsl" />
											</a>&#160;												
										</logic:notEqual>
										<logic:equal name="ssdPrm" value="false"  name="stylesheet"  property="ddConv" >																
											<a target="blank" href="<bean:write name="webRoot"/>/<bean:write name="stylesheet" property="xsl" />">						
												<bean:write name="webRoot"/>/<bean:write name="stylesheet" property="xsl" />
											</a>&#160;												
										</logic:equal>						
									</td>
									<td width="20%" align="center">
										<bean:write name="stylesheet" property="modified" />															
									</td>
								</tr>
							</logic:iterate>				
							<tr>
								<td valign="top" colspan="2">
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
			<table align="center">
				<tr>
					<td>
						<form action="addStylesheetForm">
							<logic:present name="schema" scope="request">
							    <input type="hidden" name="schema" value="<bean:write name="schema" scope="request"/>" />
							</logic:present>
							<input class="button" type="submit" value="<bean:message key="label.stylesheet.add" />"/>						
						</form>
					</td>
					<td>
						<form action="stylesheetList">
							<input type="submit" class="button" value="<bean:message key="label.ok"/>" />		
						</form>	
					</td>
				</tr>
			</table>
		</logic:equal>
		<logic:notEqual name="ssiPrm" value="true"  name="schema.stylesheets" scope="session" property="ssiPrm" >		
			<form action="stylesheetList">
				<input type="submit" class="button" value="<bean:message key="label.ok"/>" />		
			</form>
		</logic:notEqual>
	</div>	
</logic:present>



