<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<ed:breadcrumbs-push label="Uploaded XML files" level="1" />

<logic:present name="xmlfiles.uploaded">
	<h1 class="documentFirstHeading">
		<bean:message key="label.xmlfiles.uploaded"/>
	</h1>

	<%-- include Error display --%>
	<tiles:insert definition="Error" />

	<div class="visualClear">&nbsp;</div>

	<logic:present name="xmlfiles" name="xmlfiles.uploaded" scope="session" property="xmlfiles" >
		<div style="width: 97%">
			<table class="datatable" width="80%">
				<col style="width:8%"/>				<col style="width:30%"/>
				<col style="width:37%"/>
				<col style="width:25%"/>
				<thead>
					<tr>
						<th scope="col">
							<logic:equal name="ssuPrm" value="true"  name="xmlfiles.uploaded" scope="session" property="ssuPrm" >
								<span title="Action"><bean:message key="label.table.uplXmlFile.action"/></span>
							</logic:equal>
						</th>
						<th scope="col"><span title="XML File"><bean:message key="label.table.uplXmlFile.xmlfile"/></span></th>
						<th scope="col"><span title="Title"><bean:message key="label.table.uplXmlFile.title"/></span></th>
						<th scope="col"><span title="Last Modified"><bean:message key="label.lastmodified"/></span></th>
					</tr>
				</thead>
				<tbody>
					<logic:iterate indexId="index" id="xmlfile" name="xmlfiles.uploaded" scope="session" property="xmlfiles" type="UplXmlFile">
						<tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
							<td align="center" >
								<logic:equal name="ssuPrm" value="true"  name="xmlfiles.uploaded" scope="session" property="ssuPrm" >
									<a href="editUplXmlFileForm?xmlfileId=<bean:write name="xmlfile" property="id" />">
										<img src="<bean:write name="webRoot"/>/images/edit.gif" alt="<bean:message key="label.edit" />" title="edit XML file" /></a>
								</logic:equal>
								<logic:equal name="ssdPrm" value="true"  name="xmlfiles.uploaded" scope="session" property="ssdPrm" >
									<a href="deleteUplXmlFile?xmlfileId=<bean:write name="xmlfile" property="id" />"
									onclick='return xmlfileDelete("<bean:write name="xmlfile" property="fileName" />");'>
										<img src="<bean:write name="webRoot"/>/images/delete.gif" alt="<bean:message key="label.delete" />" title="delete XML file" /></a>
								</logic:equal>
							</td>
							<td>
								<a  href='<bean:write name="webRoot"/>/<bean:write name="xmlfiles.uploaded" property="xmlfileFolder" />/<bean:write name="xmlfile" property="fileName" />' title="<bean:write name="xmlfile" property="fileName" />">
									<bean:write name="xmlfile" property="fileName" />
								</a>
							</td>
							<td>
									<bean:write name="xmlfile" property="title" />
							</td>
							<td>
								<logic:notEqual name="fileExists" value=""  name="xmlfile" property="lastModified" >
									<bean:write name="xmlfile" property="lastModified" />
								</logic:notEqual>
								<logic:equal name="fileNotExists" value=""  name="xmlfile" property="lastModified" >
									<bean:message key="label.fileNotFound"/>
								</logic:equal>
							</td>
						</tr>
					</logic:iterate>
					<tr>
						<td valign="top" colspan="4">
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</logic:present>
	<logic:notPresent name="xmlfiles" name="xmlfiles.uploaded" scope="session" property="xmlfiles" >
		<div class="success">
			<bean:message key="label.uplXmlFile.noXmlFiles"/>
		</div>
	</logic:notPresent>
	<div class="visualClear">&nbsp;</div>


	<logic:equal name="ssiPrm" value="true"  name="xmlfiles.uploaded" scope="session" property="ssiPrm" >
		<div class="boxbottombuttons">
			<form action="addUplXmlFileForm">
				<div>
					<input class="button" type="submit" value="<bean:message key="label.uplXmlFile.add" />"/>
				</div>
			</form>
		</div>
	</logic:equal>


</logic:present>



