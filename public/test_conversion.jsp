<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/loose.dtd">
<%@ page import="java.util.HashMap, java.util.Vector, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices"%>
<%@ page import="eionet.gdem.conversion.ssr.Names, eionet.gdem.utils.Utils, eionet.gdem.conversion.ssr.InputAnalyser, eionet.gdem.validation.ValidationService, eionet.gdem.GDEMException" %>



<%!
private String validate(String url){
	
	try {

	    ValidationService v = new ValidationService();
		return v.validate(url);
   
	} catch (Exception e) {
		return "Error occured while validating the file: " + e.toString();
	}
}
private String validateSchema(String url, String schema){
	
	try {

	    ValidationService v = new ValidationService();
		return v.validateSchema(url, schema);
   
	} catch (Exception e) {
		return "Error occured while validating the file: " + e.toString();
	}
}
%>

<%	//get stylesheet id from parameter
	
	String xml_url = request.getParameter("XML_URL");
	String schema_id = request.getParameter("SCHEMA_ID");
	String xsl_id = request.getParameter("ID");
	String validation = request.getParameter("VALIDATE");

		
	DbModuleIF dbM= GDEMServices.getDbModule();

	
	Vector schemas=new Vector();
	HashMap schema=new HashMap();
	Vector stylesheets = new Vector();
	String err_mess = null;
	String valid=null;
	String schemaOrDTD=null;
	String root_elem = null;
	String namespace = null;

		// we know schema id - thereare several possible conversions matching only one schema
	if (!Utils.isNullStr(schema_id)){
		schemas = dbM.getSchemas(schema_id, Utils.isNullStr(xsl_id));
		if (schemas==null) schemas = new Vector();
		if (schemas.size()==0){
			%>
			<b>Couldn't find XML schema!</b> <%
			return;
		}
		schema = (HashMap)schemas.get(0);
		
		if (!Utils.isNullStr(xsl_id)){
			// we know xls id - there is only 1 possible conversion
			HashMap ss = dbM.getStylesheetInfo(xsl_id);	
			stylesheets.add(ss);
			schema.put("stylesheets", stylesheets);
		}
	}
		// we have to detect the schema
	else{
		if (!Utils.isNullStr(xml_url)){
			InputAnalyser analyser = new InputAnalyser();
			try{
				analyser.parseXML(xml_url);
			}
			catch(GDEMException e){
				err_mess = e.toString();
			}
			// schema or dtd found from header
			schemaOrDTD = analyser.getSchemaOrDTD();
			if (schemaOrDTD!=null){
				String sch_id = dbM.getSchemaID(schemaOrDTD);
				schemas = dbM.getSchemas(sch_id);
			}
			// did not find schema or dtd from xml header
			else{
				root_elem = analyser.getRootElement();
				namespace = analyser.getNamespace();
				
				schemas = dbM.getRootElemMatching(root_elem, namespace);
			}
		}
	}
	if (validation!=null && schemas!=null && xml_url!=null){
		for (int k=0; k<schemas.size();k++){
			schema = (HashMap)schemas.get(0);

			if (schemaOrDTD!=null){			//schema defined in header
				valid = validate(xml_url);
			}
			else{
				valid = validateSchema(xml_url, (String)schema.get("xml_schema"));
			}
			
			// put validation result to the hashmap
			schema.put("validation", valid);
		}
	}
	
%>
<html lang=en>
<head>
	<title>Stylesheets</title>
   	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <link type="text/css" rel="stylesheet" href="eionet.css">
	<script type="text/javascript" src="util.js"></script>
	<script type="text/javascript">
		
		detectBrowser();	
	
		function convert(){
			var file = document.forms["TestConversion"].elements["XML_FILE"].value;
			var format_buttons = document.forms["TestConversion"].elements["FORMAT"];
			
			if (file == ""){
				alert("File location is not specified!");
				return;
			}
			var format="";
			for (i=0; i<format_buttons.length;i++){
				_format = format_buttons[i];
				if (_format.checked==true)
					format = _format.value;
			}
			
			if (format == ""){
				alert("Conversion type is not selected!");
				return;
			}
			sUrl = "main?ACTION=<%=Names.EXECUTE_TESTCONVERSION_ACTION%>&format=" + format + "&url=" + file;
			//alert(sUrl);
			window.open(sUrl,"conversion","height=600,width=800,status=yes,toolbar=yes,scrollbars=yes,resizable=yes,menubar=yes,location=yes");
		}

			
	</script>

</head>
<body>
<%@ include file="header.jsp" %>
<table cellSpacing="0" cellPadding="0" border="0">
  <tbody>
  <tr valign="top">
	<td nowrap="true" width="130">
      	<p><center>
  	      <%@ include file="menu.jsp" %>
        </center></P>
	</td>
    <td width="100%">
	   <jsp:include page="location.jsp" flush='true'>
          <jsp:param name="name" value="Test conversion"/>
       </jsp:include>

      <div style="MARGIN-LEFT: 13px">

	  <br/>

	  <% if (err!= null) { %>
 	  	<h4><%=err%></h4>
	  <% } %>
  	  
	  <h2>Test conversion</h2>

	  <br/>	

		<form name="TestConversion" action="<%=Names.SHOW_TESTCONVERSION_ACTION%>" method="POST">
			<% if (err_mess!=null){
				%>
				<span class="error"><%=err_mess%></span>
			<%
			}
			%>
			<table cellspacing="0">
				<%
				if (Utils.isNullStr(xml_url)){
				%>
					<tr><td colspan ="2">Insert the url of source xml file</td></tr>
					<tr>
						<td align="right" style="padding-right:5">
							<span class="smallfont"><b>URL of XML File</b>&#160;</span>
						</td>
						<td align="left">
							<input type="text" class="smalltext" name="XML_FILE" size="53"/>
						</td>
					</tr>
				<%
				}
				else{%>
					<tr>
						<td align="right" style="padding-right:5">
							<span class="smallfont"><b>URL of XML File</b>&#160;</span>
						</td>
						<td align="left">
							<span class="smallfont"><a target="blank" href="<%=xml_url%>"><%=xml_url%></a></span>
							<input type="hidden" class="smalltext" name="XML_FILE" size="53" value="<%=xml_url%>"/>
						</td>
					</tr>
				<% 
				} 
				%>
				<tr height="10"><td colspan="2"></td></tr>
				<tr height="10"><td colspan="2">Select one of the conversion types and click Convert button</td></tr>
				<%
				for (int i=0;i<schemas.size();i++){
					HashMap xsd = (HashMap)schemas.get(i);
					Vector xsl = (Vector)xsd.get("stylesheets");
					String schema_name = (String)xsd.get("xml_schema");
					//if (xsl==null) continue;
					
				%>
					<tr valign="top">
						<td align="right" style="padding-right:5">
							<span class="smallfont"><b>XML Schema</b>&#160;</span>
						<td align="left">	
							<a target="blank" href="<%=schema_name%>"><%=schema_name%></a>
						</td>
					</tr>
					<%
					
					if (xsl==null) {
					%>
						<tr valign="top">
							<td align="right" style="padding-right:5">&#160;</td>
							<td align="left">
								No stylesheets found for this XML schema
							</td>
						</tr>
						<%
						
					}
					else{
						for (int j=0;j<xsl.size();j++){
							HashMap _xsl = (HashMap)xsl.get(j);
							String output_type= (String)_xsl.get("content_type_out");
							String description=(String)_xsl.get("description");
							String xsl_file=(String)_xsl.get("xsl");
							String convert_id = (String)_xsl.get("convert_id");
						%>
							<tr valign="top">
								<td align="right" style="padding-right:5">
									<input type="radio" name="FORMAT" value="<%=convert_id%>"/>
								</td>
								<td align="left">
									<a target="blank" href="<%=Names.XSL_FOLDER%><%=xsl_file%>" title="open XSL file"><%=output_type%></a> - <%=description%>
								</td>
							</tr>
						<%
						}
					}
					%>
					<%
					if (validation!=null && xml_url!=null){
						String validation_result = (String)xsd.get("validation");
						if (validation_result!=null){
							if (validation_result.equals("OK")) validation_result="The source file is valid";
							%>
							<tr valign="top">
								<td align="right" style="padding-right:5">Validation results:</td>
								<td align="left"><%=validation_result%></td>
							</tr>
							<%
						}
					}
					%>
					<tr height="10"><td colspan="2"></td></tr>
					<%
				}
				%>
				<tr height="10"><td colspan="2"></td></tr>
				<%
					boolean convPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_TESTCONVERSION_PATH, "x");
					if (convPrm){
						%>
						<tr>
							<td></td>
							<td align="left">
								<input name="TEST" type="button" class="mediumbuttonb" value="Convert" onclick="convert()"></input>&#160;&#160;
							</td>
						</tr>
					<%}%>
			</table>
			<input type="hidden" name="ACTION" value="<%=Names.SHOW_TESTCONVERSION_ACTION%>"/>
		</form>	

		 
		</div>
	  </td>
	</tr>
	</tbody>
</table>
<form name="f" action="main" method="POST">
	<input type="hidden" name="ACTION" value=""/>
	<input type="hidden" name="PARAM" value=""/>
</form>

</body>
</html>
