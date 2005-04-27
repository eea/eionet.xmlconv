<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.HashMap, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, java.util.Vector" %>



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
<jsp:param name="name" value="MS Excel to XML "/>
</jsp:include>
<%@ include file="menu.jsp" %>
<div id="workarea">

	<div id="tabbedmenu">
    <ul>
	    <li><a href="list_conversions.jsp">XML converters</a></li>
    	<li id="currenttab"><a href="excel2xml_conversion.jsp">MS Excel to XML</a></li>
  	</ul>
	</div>
			<% if (err!=null){
				%>
				<span id="errormessage"><%=err%></span>
			<%
			}
			%>
  	  
	  <h1>MS Excel to XML conversion</h1>

	  <br/>	

		<form name="Excel2XML"	action="<%=Names.TEST_CONVERSION_SERVLET%>"	method="post">
				
			<input type="radio" name="split" value="all" onclick="sheet_name.disabled=true" checked="checked">Convert all Excel sheets</input>
			<br/>
			<input type="radio" name="split" value="split" onclick="sheet_name.disabled=false">Convert only one Excel sheet. Insert the sheet name:</input>
			<input type="text" id="sheet" name="sheet_name" onfocus="split[1].checked=true"/>
			<br/><br/>
			<table cellspacing="0">
				<tr><td colspan ="2">Insert the url of source MS Excel file</td></tr>
				<tr>
					<td align="right" style="padding-right:5">
						<label for="excelurlfield">URL of Excel File</label>
					</td>
					<td align="left">
						<input type="text" class="textfield" name="url" size="53" id="excelurlfield" />
					</td>
				</tr>

				<tr><td colspan="2"></td></tr>
				<tr>
					<td></td>
					<td align="left">
						<input name="Convert" type="submit" class="mediumbuttonb" value="Convert" />&#160;&#160;
					</td>
				</tr>
			</table>
			<br/>
			<div>NB! The MS Excel file should be generated from Data Dictionary and DO_NOT_DELETE_THIS_SHEET should be available with original data.</div>
			<input type="hidden" name="format" value="<%=Names.EXCEL2XML_CONV_PARAM%>"/>
			<input type="hidden" name="ACTION" value="<%=Names.SHOW_TESTCONVERSION_ACTION%>"/>
		</form>	

		 
		</div>
<%@ include file="footer.jsp" %>
</body>
</html>
