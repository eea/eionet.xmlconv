<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.HashMap, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, java.util.Vector" %>



<%
	
	DbModuleIF dbM= GDEMServices.getDbModule();
	Vector schemas = dbM.getSchemas(null);
	if (schemas==null) schemas=new Vector();
	
	
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Stylesheets</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
    <script type="text/javascript" src="util.js"></script>
    <script type="text/javascript">
		
		function convert(){
			var file = document.forms["TestConversion"].elements["XML_FILE"].value;
			if (file == ""){
				alert("File location is not specified!");
				return;
			}
		
			//alert(sUrl);
			window.open(sUrl,"conversion","height=600,width=800,status=yes,toolbar=yes,scrollbars=yes,resizable=yes,menubar=yes,location=yes");
		}

			
	</script>

</head>
<body>
<jsp:include page="location.jsp" flush='true'>
<jsp:param name="name" value="Find conversion"/>
</jsp:include>
<%@ include file="menu.jsp" %>
<div id="workarea">

	  <% if (err!= null) { %>
 	  	<h4><%=err%></h4>
	  <% } %>
  	  
	  <h2>Find converters</h2>

	  <br/>	

		<form name="FindConversion" action="main" method="post">
				
			<table cellspacing="0">
				<tr><td colspan ="2">Insert the url of source xml file</td></tr>
				<tr>
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>URL of XML File</b>&#160;</span>
					</td>
					<td align="left">
						<input type="text" class="smalltext" name="XML_URL" size="53"/>
					</td>
				</tr>
				<%
				%>
				<tr height="10"><td colspan="2"></td></tr>
				<tr><td colspan ="2">Select an XML schema from the list or leave it blank to find out all possible conversion types</td></tr>
				<tr valign="top">
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>XML Schema</b>&#160;</span>
					</td>
					<td align="left">
						<select name="SCHEMA_ID" rows="5">
								<option value="" selected="selected">--</option>
						<%
						for (int i=0; i<schemas.size();i++){
							HashMap schema = (HashMap)schemas.get(i);
							String xsd_id = (String)schema.get("schema_id");
							String xsd_name = (String)schema.get("xml_schema");
							Vector stylesheets = (Vector)schema.get("stylesheets");
							if (stylesheets.size() ==0) continue;
					
							if (xsd_name==null) continue;
							
							%>
								<option value="<%=xsd_id%>"><%=xsd_name%></option>
							<%
						}
						%>
						</select>
					</td>
				</tr>
				<tr height="10"><td colspan="2"></td></tr>
				<tr><td colspan="2">If you want to validate the specified XML file against found XML Schemas, please check the following box. (It might take some more time, if you choose to validate!)</td></tr>
				<tr>
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>Validate</b>&#160;</span>
					</td>
					<td align="left">
						<input type="checkbox" name="VALIDATE">
					</td>
				</tr>
				<tr height="10"><td colspan="2"></td></tr>
				<tr>
					<td></td>
					<td align="left">
						<input name="LIST" type="submit" class="mediumbuttonb" value="List"></input>&#160;&#160;
					</td>
				</tr>
			</table>
			<input type="hidden" name="ACTION" value="<%=Names.SHOW_TESTCONVERSION_ACTION%>"/>
		</form>	

		 
		</div>
<form name="f" action="main" method="post">
	<input type="hidden" name="ACTION" value=""/>
	<input type="hidden" name="PARAM" value=""/>
</form>
<%@ include file="footer.jsp" %>
</body>
</html>
