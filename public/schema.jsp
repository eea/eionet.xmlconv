<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.HashMap, java.util.Vector, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names" %>

<%!private HashMap schema=null;%>


<%
	//get schema id from parameter
	String id = (String)request.getParameter(Names.SCHEMA_ID);
	if (id == null || id.length()==0){ 
		id="0";
	}
	DbModuleIF dbM= GDEMServices.getDbModule();

	Vector list = dbM.getSchemas(id, false);
	if (list==null) list=new Vector();
	
	String name = "";
	String schema_desc = null;
	String dtd_public_id = null;
	boolean isDTD = false;
	
	if (list.size()>0){
	
		schema = (HashMap)list.get(0);
		name = (String)schema.get("xml_schema");
		schema_desc = (String)schema.get("description");
		dtd_public_id = (String)schema.get("dtd_public_id");
		int name_len = name.length();
		String schema_end = name.substring((name_len-3), (name_len)).toLowerCase();
		if (schema_end.equals("dtd")) isDTD=true;
	}
	Vector root_elems = (Vector)dbM.getSchemaRootElems(id);
	if (root_elems==null) root_elems=new Vector();
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<title>XML Schema or DTD</title>
   	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
	<script type="text/javascript" src="util.js"></script>
	<script type="text/javascript">
		
	
		function openApp(appName) {
			document.forms["f"].app.value=appName;
			document.forms["f"].ACTION.value="";
			document.forms["f"].ACL.value="/";
			document.forms["f"].submit();
		}
		function doLogin(appName) {
			window.open("appLogin.jsp?app=" + appName,"login","height=200,width=300,status=no,toolbar=no,scrollbars=no,resizable=no,menubar=no,location=no");
		}

				
	</script>
</head>
<body>
<jsp:include page="location.jsp" flush='true'>
<jsp:param name="name" value="XML Schema or DTD"/>
</jsp:include>
<%@ include file="menu.jsp" %>
<div id="workarea">

		<% if (err!= null) { %>
			<h4><%=err%></h4>
		<% } %>
	
		<h2>XML Schema or DTD information</h2>

		<%
		boolean xsduPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "u");
		%>		
		<br/>

		<form name="upd_xsd" action="main" method="post">
			<table cellspacing="0">
				<%	if (xsduPrm){ %>
					<tr height="10"><td colspan="2" align="right">
						<img onclick="upd_xsd.submit();" height="15" width="15" src="images/edit.png" title="Save changes"></img>
					</td></tr>
				<% } %>
				<tr>
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>Location</b>&#160;</span>
					</td>
					<td align="left">
						<input type="text" class="smalltext" name="XML_SCHEMA" size="70" value="<%=name%>"/>
					</td>
				</tr>
				<tr>
					<td align="right" style="padding-right:5">
						<span class="smallfont"><b>Description</b>&#160;</span>
					</td>
					<td align="left">
						<input type="text" class="smalltext" name="DESCRIPTION" size="70" value="<%=schema_desc%>"/>
					</td>
				</tr>
				<% if (isDTD) {%>
					<tr>
						<td align="right" style="padding-right:5">
							<span class="smallfont"><b>DTD Public Id</b>&#160;</span>
						</td>	
						<td align="left">
							<input type="text" class="smalltext" name="DTD_PUBLIC_ID" size="70" value="<%=dtd_public_id%>"/>
						</td>
					</tr>
				<% }%>
				<tr height="10"><td colspan="2"></td></tr>
				<tr>
					<td align="left" style="padding-right:5" colspan="2">
						<span class="smallfont"><b>Root elements</b></span>
					</td>
				</tr>
			</table>
			<input type="hidden" name="ACTION" value="<%=Names.XSD_UPD_ACTION%>"></input>
			<input type="hidden" name="<%=Names.SCHEMA_ID%>" value="<%=id%>"></input>
		</form>		
	    <table cellSpacing="5">
    	   	<head>
					
        		<tr>
			  		<th align="middle" width="100">Element name</th>
          			<th align="left" width="355">Namespace</th>
       				<%
					if (xsduPrm){%>
    	     			<th align="middle">&#160;</th>
					<%}%>    	     			
				</tr>
    	   	</thead>
    	   	<tbody>
    	   	<%
    	   	

				for (int i=0; i<root_elems.size(); i++){
					HashMap hash = (HashMap)root_elems.get(i);
					String elem_id = (String)hash.get("rootelem_id");
					String ns = (String)hash.get("namespace");
					String elem_name = (String)hash.get("elem_name");
    	   			%>
					<tr height="5">
						<td align="middle" style="padding-left:5;padding-right:10"  <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<%=elem_name%>
						</td>
						<td align="left" style="padding-left:5;padding-right:10"  <% if (i % 2 != 0) %>class="zebradark"<%;%>><%=ns%></td>
 	         			<td align="middle">
	         				<%
							if (xsduPrm){%>
								<img onclick="del_elem_<%=elem_id%>.submit();" height="15" width="15" src="images/delete.png" title="Delete root element"></img>
							<%}%>
 	         			</td>
						<form name="del_elem_<%=elem_id%>" action="main" method="post">
							<input type="hidden" name="ACTION" value="<%=Names.ELEM_DEL_ACTION%>"></input>
							<input type="hidden" name="ELEM_DEL_ID" value="<%=elem_id%>"></input>
							<input type="hidden" name="<%=Names.SCHEMA_ID%>" value="<%=id%>"></input>
						</form>		
					</tr>
					<%
    	   		}
	    	   	%>
  				<%
				if (xsduPrm){
				%>
					<form name="add_elem" action="main" method="post">
						<tr height="10"><td colspan="3"></td></tr>
						<tr>
							<td>
								<input type="text" name="ELEM_NAME" size="20"></input>
							</td>
							<td>
								<input type="text" name="NAMESPACE" size="65"></input>
							</td>
							<td>
								<img onclick="add_elem.submit();" height="15" width="15" src="images/edit.png" title="Add a new root element"></img>
							</td>
						</tr>
						<input type="hidden" name="ACTION" value="<%=Names.ELEM_ADD_ACTION%>"></input>
						<input type="hidden" name="<%=Names.SCHEMA_ID%>" value="<%=id%>"></input>
					</form>
				<%
				}
				%>
					
			</tbody>
		 </table>
		 
  	  </div>

<form name="f" action="main" method="post">
	<input type="hidden" name="ACTION" value=""/>
	<input type="hidden" name="PARAM" value=""/>
</form>
<%@ include file="footer.jsp" %>
</body>
</html>
