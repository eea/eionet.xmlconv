<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names,java.util.Vector,java.util.Hashtable"%>
<%!private String mode=null;%>



<%
	String host_id = request.getParameter("ID");
	host_id = (host_id == null ? "" : host_id);
    
	String host_name = "";
	String user_n = "";
	String pwd = "";
	
	if (host_id.equals(""))
		mode="add";
	else{
		mode="edit";
	}		

		


	
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Hosts</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
    <script type="text/javascript" src="util.js"></script>
    
</head>
<body>

<jsp:include page="location.jsp" flush='true'>
<jsp:param name="name" value="Host"/>
</jsp:include>
<%@ include file="menu.jsp" %>
<div id="workarea">
	<%
	if (mode.equals("edit")){ 
		DbModuleIF dbM= GDEMServices.getDbModule();

		Vector hosts=null;
		if (hovPrm)
			hosts = dbM.getHosts(host_id);
		else
			err="You are not allowed to view host information!";

		if (hosts!=null){
			Hashtable host = (Hashtable)hosts.get(0);
		
			host_id = (String)host.get("host_id");
			host_name = (String)host.get("host_name");
			user_n = (String)host.get("user_name");	
			pwd = (String)host.get("pwd");
		}
	}
	%>

			<% if (err!=null){
				%>
				<span id="errormessage"><%=err%></span>
			<%
			}
			%>
		<%
		if(mode.equals("add")){%>
			<h1>Add a new host and credentials</h1>
		<%}
		else if (mode.equals("edit")){%>
			<h1>Edit host credentials</h1>
		<%}%>

		<br/>
		<%
			boolean hodPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_HOST_PATH, "d");
			boolean hoiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_HOST_PATH, "i");
			boolean houPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_HOST_PATH, "u");
			
		%>		
		

		<form name="save_host" action="main" method="post">
				
			<table cellspacing="0">
				<tr>
					<td align="right" style="padding-right:5">
						<label for="hostnamefield">Host name</label>
					</td>
					<td align="left">
							<input type="text" class="textfield" name="HOST_NAME" size="70" id="hostnamefield" value="<%=host_name%>"/>
					</td>
				</tr>		
				<tr>
					<td align="right" style="padding-right:5">
						<label for="usenamefield">User name</label>
					</td>
					<td align="left">
						<input type="text" class="textfield" name="USER_NAME" size="70" id="usenamefield" value="<%=user_n%>"/>
					</td>
				</tr>		
				<tr>
					<td align="right" style="padding-right:5">
						<label for="passwordfield">Password</label>
					</td>
					<td align="left">
						<input type="text" class="textfield" name="PASSWORD" size="70" id="passwordfield" value="<%=pwd%>"/>
					</td>
				</tr>		
				<tr><td>&#160;</td><td>&#160;</td></tr>
				<tr>
					<td>&#160;</td>
					<td><input name="SUBMIT" type="submit" value="Save" class="mediumbuttonb" /></td>
				</tr>
			</table>
			<input type="hidden" name="HOST_ID" value="<%=host_id%>"/>

			<%if(mode.equals("edit")){%>
				<input type="hidden" name="ACTION" value="<%=Names.HOST_UPD_ACTION%>"/>
			<%}else{%>
				<input type="hidden" name="ACTION" value="<%=Names.HOST_ADD_ACTION%>"/>
			<%}%>		    		
		</form>	
		 <br/><br/>
	</div>
<%@ include file="footer.jsp" %>
</body>
</html>
