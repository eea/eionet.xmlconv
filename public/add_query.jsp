<%@page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ page import="java.util.HashMap, java.util.Vector, java.util.Hashtable, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names" %>
<%@ page import="eionet.gdem.qa.XQScript"%>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
%>

<%
	//get schema from parameter
	String id = request.getParameter("ID");
    id = (id == null ? "" : id);
	
    eionet.gdem.services.db.dao.ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
    eionet.gdem.services.db.dao.IConvTypeDao  convTypeDao = GDEMServices.getDaoService().getConvTypeDao();
	
	String schema_name=null;

	if (!id.equals("")){
		Vector list = schemaDao.getSchemas(id);
		if (list==null) list=new Vector();
		if (list.size()==0){%>
			<b>Couldn't find XML schema!</b> <%
			return;
		}

		HashMap schema = (HashMap)list.get(0);
		schema_name = (String)schema.get("xml_schema");
	}

	Vector convTypes = convTypeDao.getConvTypes();
	if (convTypes==null) convTypes = new Vector();

%>
<ed:breadcrumbs-push label="Add query" level="2" />
<tiles:insert definition="TmpHeader">
	<tiles:put name="title" value="Add a new query"/>
</tiles:insert>


<%@ include file="menu.jsp" %>


		<h1>Add QA Script</h1>

		<% if (err!= null) { %>
			<div class="error-msg"><%=err%></div>
		<% } %>

		<form id="Upload" action="main?ACTION=<%=Names.QUERY_ADD_ACTION%>" method="post" enctype="multipart/form-data">
			<table class="formtable">
				<col style="width:20%"/>
				<col style="width:80%"/>
				<tr>
					<td>
						<label class="question" for="schemafield">XML Schema</label>
					</td>
					<td align="left">
						<input type="text" id="schemafield" size="64" name="SCHEMA" value="<% if (schema_name != null) %><%=schema_name%><%;%>" />
					</td>
				</tr>
				<tr valign="top">
					<td>
						<label class="question" for="shortnamefield">Short Name</label>
					</td>
					<td align="left">
						<input type="text" id="shortnamefield" class="textfield" size="64" name="SHORT_NAME" value="" />
					</td>
				</tr>
				<tr valign="top">
					<td>
						<label class="question" for="descriptionfield">Description</label>
					</td>
					<td align="left">
						<textarea class="small" rows="2" cols="55" name="DESCRIPTION" id="descriptionfield" style="width: 98%;"></textarea>
					</td>
				</tr>
				<tr valign="top">
					<td>
						<label class="question" for="contenttypefield">Output type</label>
					</td>
					<td align="left">
						<select class="small" name="CONTENT_TYPE" id="contenttypefield" title="content type of resilt file">
							<option value="HTML">HTML</option>
							<option value="XML">XML</option>
							<option value="TEXT">TEXT</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<label class="question" for="scripttype">Script type</label>
					</td>
					<td>
						<select  class="small" name="SCRIPT_TYPE" id="scriptType">
						<%
							for (int i=0;i<XQScript.SCRIPT_LANGS.length;i++){
							%>
								<option value="<%=XQScript.SCRIPT_LANGS[i] %>"><%=XQScript.SCRIPT_LANGS[i] %></option>
							<%
							}
							%>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<label class="question" for="filefield">Script File</label>
					</td>
					<td align="left">
						<input type="file" class="textfield" name="FILE_INPUT" id="filefield" size="53"/>
					</td>
				</tr>
				<tr><td colspan="2"></td></tr>
				<tr>
					<td></td>
					<td align="left">
						<input name="SUBMIT" type="submit" class="button" value="Upload"/>&#160;&#160;
						<input name="RESET" type="reset" class="button" value="Clear" />
					</td>
				</tr>
			</table>
			<div>
				<input type="hidden" name="ACTION" value="<%=Names.QUERY_ADD_ACTION%>"/>
			</div>
		</form>

<form id="f" action="main" method="post">
	<div>
		<input type="hidden" name="ACTION" value=""/>
		<input type="hidden" name="PARAM" value=""/>
	</div>
</form>
<tiles:insert definition="TmpFooter"/>
