<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
<%@page contentType="text/html" import="eionet.gdem.ssr.Names"%>
<%

	String err = (String)request.getAttribute(Names.ERROR_ATT);

%>

<HTML lang=en>
	<HEAD>
		<TITLE>Login</TITLE>
		<META content="MSHTML 5.50.4522.1800" name=GENERATOR>
		<SCRIPT language=JavaScript>
			function setFocus(){
				var t;
				t=document.getElementById("j_username");
				t.focus();
			}
		</SCRIPT>
	</HEAD>
	<BODY bgColor=#f0f0f0 leftMargin=0 topMargin=0 marginheight="0" marginwidth="0" onload="setFocus()">
	<br/><br/><br/><br/>
	<FORM name="f" action="main" method="POST">
		<INPUT name="ACTION" type="hidden" value="<%=Names.LOGIN_ACTION%>"></INPUT>
		<table>
		<% if (err!= null) { %>
	  		<tr>
				<td width="200"></td>
				<td colspan=2>
	  				<table width=500>
						<tr>
							<td></td>
							<td align=left><b><font color="#ff0000"><%=err%></font></b></td>
						</tr>
	 				</table>
	 			</td>
	 		</tr>
		<% } %>
			<tr>
				<td width="200"></td><td><b>UserName:</b></td>			<td><input type=text name="j_username" id="j_username"></input></td>
			</tr>
			<tr>
				<td width="200"></td><td><b>Password:</b></td><td><input type=password name="j_passwd"></input></td>
			</tr>
			<tr height=30><td colspan=3></td>
			</tr>
			<tr>
				<td width="200"></td><td>
					<input type=submit name="ok_btn" value="  OK  "></input></td>
				</td>
				<td><input type=button name="cancel_btn" value="  Cancel  " onClick="javascript:window.close()"></input></td></td>
			</tr>
		</table>
	</FORM>
	</BODY>
</HTML>
