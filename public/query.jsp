<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.HashMap, java.util.Vector, java.util.Hashtable, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, eionet.gdem.utils.Utils" %>



<%
	//get schema from parameter
	String id = request.getParameter("query_id");

	id = (id == null ? "" : id);
	
	

	DbModuleIF dbM= GDEMServices.getDbModule();
	HashMap query =null;
	String mode="view";
	String xml_schema = "";
	String short_name= "";
	String description = "";
	String content_type = "";
	String file = "";
	String schema_id = "";

	if (!id.equals("")){
		query = dbM.getQueryInfo(id);
		if (query==null) query=new HashMap();

		xml_schema = (String)query.get("xml_schema");
		short_name= (String)query.get("short_name");
		description = (String)query.get("description");
		content_type = (String)query.get("content_type");
		file = (String)query.get("query");
		schema_id = (String)query.get("schema_id");
	}
	
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Queries</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />

    <script type="text/javascript" src="util.js"></script>
</head>
<body>

<jsp:include page="location.jsp" flush='true'>
<jsp:param name="name" value="Query"/>
</jsp:include>
    <%@ include file="menu.jsp" %>
<div id="workarea">
	<div id="operations">
		<ul>
			<%
			if(Utils.isNullStr(schema_id)){%>
				<li><a href="main" title="Back to main page">Back to Queries</a></li>
			<%} else { %>
				<li><a href="<%=Names.QUERIES_JSP%>?ID=<%=schema_id%>" title="Back to XML Schema">Back to XML Schema</a></li>

			<%}%>
			<li><a href="<%=Names.SANDBOX_JSP%>?ID=<%=id%>" title="Run query in Sandbox">Run Query</a></li>
		</ul>	
	</div>

		<% if (err!= null) { %>
			<h1><font color="#FF0000"><%=err%></font></h1>
		<% } %>
		<%
		boolean xquPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "u");
			
		if(xquPrm)
			mode="edit";
		
		if (mode.equals("edit")){%>
			<h1>Edit query</h1>
		<%}
		else{%>
			<h1>View query</h1>
		<%}%>
		<form name="Upload" action="main?query_id=<%=id%>&amp;ACTION=<%=Names.QUERY_UPD_ACTION%>" method="post" enctype="multipart/form-data">
				
			<input type="hidden" size="60" name="SCHEMA_ID" value="<%=schema_id%>"/>
			<input type="hidden" size="60" name="QUERY_ID" value="<%=id%>"/>
			<table cellspacing="0">
				<tr valign="top">
					<td align="left">	
						<label for="schemafield">XML Schema:&#160;</label>
					</td>
					<td align="left"><div id="schemafield"><%=xml_schema%></div>
					</td>
				</tr>
				<tr valign="top">
					<td align="right">
						<label for="shortnamefield">Short Name:&#160;</label>
					</td>
					<td align="left">	
						<%if(!mode.equals("view")){%>
							<input type="text" id="shortnamefield" class="textfield" size="64" name="SHORT_NAME" value="<%=short_name%>" />
						<%}else{%>
							<div id="shortnamefield"><%=short_name%></div>
						<%}%>
					</td>
				</tr>
				<tr valign="top">
					<td align="right">
						<label for="descriptionfield">Description:&#160;</label>
					</td>
					<td align="left">	
						<%if(!mode.equals("view")){%>
							<textarea class="small" rows="2" cols="55" name="DESCRIPTION" id="descriptionfield"><%=description%></textarea>
						<%}else{%>
							<div id="descriptionfield"><%=description%></div>
						<%}%>
					</td>
				</tr>
				<tr valign="top">
					<td align="right">
						<label for="contenttypefield">Content type:&#160;</label>
					</td>
					<td align="left">	
						<%if(!mode.equals("view")){%>
							<select class="small" name="CONTENT_TYPE" id="contenttypefield" title="rrr" alt="jjj">
								<option value="HTML" <% if (content_type.equals("HTML")) %>selected="true"<%;%>>HTML</option>
								<option value="XML" <% if (content_type.equals("XML")) %>selected="true"<%;%>>XML</option>
								<option value="TXT" <% if (content_type.equals("TXT")) %>selected="true"<%;%>>TEXT</option>
							</select>
						<%}else{%>
							<div id="contenttypefield"><%=content_type%></div>
						<%}%>
					</td>
				</tr>
				<tr>
					<td align="right" valign="top">
						<label for="filefield">XQuery file:&#160;</label>
					</td>
					<td align="left">
						<a href="<%=Names.QUERY_FOLDER%><%=file%>" title="View XQuery source"><%=file%>
              			</a>
						<%if(!mode.equals("view")){%>
							<input type="hidden" name="FILE_NAME" value="<%=file%>"/><br/><br/>
							<input type="file" class="textfield" name="FILE_INPUT" id="filefield" size="53" title="Add a new XQuery file"/>
						<% } else {%>
							<div id="filefield"></div>						
						<% }%>
					</td>
				</tr>
				<tr><td colspan="2"></td></tr>
				<%if(!mode.equals("view")){%>
					<tr>
						<td></td>
						<td align="left">
							<input name="SUBMIT" type="SUBMIT" class="mediumbuttonb" value="Save" />&#160;&#160;
						</td>
					</tr>
				<% } %>
			</table>
			<input type="hidden" name="ACTION" value="<%=Names.QUERY_UPD_ACTION%>"/>
		</form>	

	</div>
<%@ include file="footer.jsp" %>
</body></html>
