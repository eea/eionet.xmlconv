<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.HashMap, java.util.Vector, java.util.Hashtable, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names,eionet.gdem.utils.Utils" %>



<%
	//get schema from parameter
	String id = request.getParameter("ID");
    id = (id == null ? "" : id);
	
	

	DbModuleIF dbM= GDEMServices.getDbModule();

	HashMap xsl =null;
	String mode="view";
	String xml_schema = "";
	String description = "";
	String content_type = "";
	String file = "";
	String schema_id = "";

	if (!id.equals("")){
		xsl = dbM.getStylesheetInfo(id);
		if (xsl==null) xsl=new HashMap();

		xml_schema = (String)xsl.get("xml_schema");
		description = (String)xsl.get("description");
		content_type = (String)xsl.get("content_type_out");
		file = (String)xsl.get("xsl");
		schema_id = (String)xsl.get("schema_id");
	}

	Vector convTypes = dbM.getConvTypes();
	if (convTypes==null) convTypes = new Vector();
	
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Stylesheets</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />

    <script type="text/javascript" src="util.js"></script>

</head>
<body>

<jsp:include page="location.jsp" flush='true'>
<jsp:param name="name" value="Stylesheet"/>
</jsp:include>
    <%@ include file="menu.jsp" %>
<div id="workarea">
	<div id="operations">
		<ul>
			<%
			if(Utils.isNullStr(schema_id)){%>
				<li><a href="main" title="Back to main page">Back to Stylesheets</a></li>
			<%} else { %>
				<li><a href="stylesheets.jsp?ID=<%=schema_id%>" title="Back to XML Schema">Back to XML Schema</a></li>

			<%}%>
			<li><a href="main?ACTION=<%=Names.SHOW_TESTCONVERSION_ACTION%>&amp;ID=<%=id%>&amp;SCHEMA_ID=<%=schema_id%>" title="Run conversion">Run conversion</a></li>
		</ul>	
	</div>

			<% if (err!=null){
				%>
				<span id="errormessage"><%=err%></span>
			<%
			}
		boolean ssuPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "u");
			
		if(ssuPrm)
			mode="edit";
		
		if (mode.equals("edit")){%>
			<h1>Edit styleheet</h1>
		<%}
		else{%>
			<h1>View styleheet</h1>
		<%}%>
		<form name="Upload" action="main?ID=<%=id%>&amp;ACTION=<%=Names.XSL_UPD_ACTION%>" method="post" enctype="multipart/form-data">
								
			<input type="hidden" size="60" name="SCHEMA_ID" value="<%=schema_id%>"/>
			<input type="hidden" size="60" name="XSL_ID" value="<%=id%>"/>
			<table cellspacing="0">
				<tr valign="top">
					<td align="left">	
						<label for="schemafield">XML Schema:&#160;</label>
					</td>
					<td align="left"><div id="schemafield"><%=xml_schema%></div></td>
				</tr>
				<tr valign="top">
					<td align="right">
						<label for="contenttypefield">Output type:&#160;</label>
					</td>
					<td align="left">	
						<%if(!mode.equals("view")){%>
							<select class="small" name="CONTENT_TYPE" id="contenttypefield" title="rrr" alt="jjj">
								<%
								for (int j=0;j<convTypes.size();j++){
									Hashtable convtype=(Hashtable)convTypes.get(j);
									String conv_type=(String)convtype.get("conv_type");
									%>
									<option value="<%=conv_type%>" <% if (conv_type.equals(content_type)) %>selected="true"<% ; %>><%=conv_type%></option>
									<%
								}
								%>
							</select>
						<%}else{%>
							<div id="contenttypefield"><%=content_type%></div>
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
				<tr>
					<td align="right" valign="top">
						<label for="filefield">XSL File:&#160;</label>
					</td>
					<td align="left">
						<a href="<%=Names.XSL_FOLDER%><%=file%>" title="View stylesheet source"><%=file%>
              			</a>
						<%if(!mode.equals("view")){%>
							<input type="hidden" name="FILE_NAME" value="<%=file%>"/><br/><br/>
							<input type="file" class="textfield" name="FILE_INPUT" id="filefield" size="53" title="Add a new stylesheet file"/>
						<% } else {%>
							<div id="filefield"></div>
						<%} %>
					</td>
				</tr>
				<tr><td colspan="2"></td></tr>
				<%if(!mode.equals("view")){%>
					<tr>
						<td></td>
						<td align="left">
							<input name="SUBMIT" type="submit" class="mediumbuttonb" value="Save"/>&#160;&#160;
						</td>
					</tr>
				<% } %>
			</table>
			<input type="hidden" name="ACTION" value="<%=Names.XSL_UPD_ACTION%>"/>
		</form>	

	</div>
<%@ include file="footer.jsp" %>
</body></html>
