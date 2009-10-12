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
			<logic:equal  value="true"  name="schema.stylesheets" scope="session" property="handcoded" >
			   	<bean:define id="id" name="schema" property="id" />
    			<div id="tabbedmenu">   	
        			<ul>
            			<li> 
                			<html:link page="/do/viewSchemaForm?schemaId=${id}"   titleKey="label.tab.title.schema" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                    			<bean:message key="label.tab.title.schema" />
                			</html:link>
            			</li>
            			<li id="currenttab">
            				<span style="color: black; text-decoration: none;" title='<bean:message key="label.tab.title.xsl"/>'><bean:message key="label.tab.title.xsl" /></span>
            			</li>
            			<li>
                			<html:link page="/queries.jsp?ID=${id}"   titleKey="label.tab.title.scripts" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                    			<bean:message key="label.tab.title.scripts" />
                			</html:link>
            			</li>
        			</ul>
				</div>
				<logic:equal name="ssiPrm" value="true"  name="schema.stylesheets" scope="session" property="ssiPrm" >
					<div id="operations">
	  					<ul>
	   						<li><a href="addStylesheetForm?schema=<bean:write name="schema" property="schema" />"><bean:message key="label.stylesheet.add" /></a></li>
						</ul>
					</div>
				</logic:equal>
			</logic:equal>
			<h1 class="documentFirstHeading">
				<bean:message key="label.schema.stylesheets"/>&nbsp;<bean:write name="schema" property="schema" />
			</h1>
		
	</logic:iterate>

	<%-- include Error display --%>
	<tiles:insert definition="Error" />
			
	<logic:iterate indexId="index" id="schema" name="schema.stylesheets" scope="session" property="handCodedStylesheets" type="Schema">
			<div class="visualClear">&nbsp;</div>

			<logic:present name="stylesheets" name="schema" scope="page" property="stylesheets" >
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
											<html:img page="/images/execute.gif" altKey="label.stylesheet.run" title="run conversion"/>
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
			</logic:present>
			<logic:notPresent name="stylesheets" name="schema" scope="page" property="stylesheets" >
				<div class="success">
					<bean:message key="label.schema.noStylesheets"/>
				</div>
			</logic:notPresent>
	</logic:iterate>

	<div class="visualClear">&nbsp;</div>
</logic:present>



