<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page contentType="text/html" import="eionet.gdem.conversion.ssr.Names"%>
<%

	String err = (String)request.getAttribute(Names.ERROR_ATT);

%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <script type="text/javascript">
    function setFocus(){
            var t;
            t=document.getElementById("j_username");
            t.focus();
    }
    </script>
</head>
<body bgcolor="#f0f0f0" leftmargin="0" topmargin="0" marginheight="0" marginwidth="0" onload="setFocus()">
		<form name="f" action="main" method="post">
			<input name="ACTION" type="hidden" value="<%=Names.LOGIN_ACTION%>" />
			<% if (err!= null) { %>
		  		<h4><%=err%></h4>
			<% } 
			else{
			%>
				<br/><br/><br/><br/>
			<% } %>
			<table>
				<tr>
					<td width="200"></td><td><b>UserName:</b></td>			
					<td><input type="text" name="j_username" id="j_username" /></td>
				</tr>
				<tr>
					<td width="200"></td><td><b>Password:</b></td><td><input type="password" name="j_passwd" /></td>
				</tr>
				<tr height="30"><td colspan="3"></td>
				</tr>
				<tr>
					<td width="200"></td><td>
						<input type="submit" name="ok_btn" value="  OK  " /></td>
					</td>
					<td><input type="button" name="cancel_btn" value="  Cancel  " onclick="javascript:window.close()" /></td></td>
				</tr>
			</table>
		</form>
</body>
</html>
