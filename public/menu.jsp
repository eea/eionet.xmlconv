<%@ page import="eionet.gdem.ssr.Names, eionet.gdem.ssr.SecurityUtil,com.tee.uit.security.AppUser" %>
<%

	AppUser user = SecurityUtil.getUser(request);
	String user_name=null;
	if (user!=null)
		user_name = user.getUserName();
	String err = (String)request.getAttribute(Names.ERROR_ATT);

%>

	<table cellSpacing="0" cellPadding="0" border="0">
        <tr>
          <td align="center"><span class="head0">Contents</span></tr>
		</tr>
		<tr>
			<td align="right">
				<a onmouseover="Over('img1')" onmouseout="Out('img1')" href="main" onclick="Click('img1')">
					<img height="13" alt="" src="images/off.gif" width="16" border="0" name="img1"><img height="13" alt="Show Conversions" src="images/button_Conversions.gif" width="84" border="0">
				</a>
			</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<%
			//AppUser user = SecurityUtil.getUser(request);
		%>
		<tr>
			<td align="right">
				<%
				if (user!=null ){
				%>
		        	<form name="logout" action="main" method="POST">
						<input name="ACTION" type="hidden" value="<%=Names.LOGOUT_ACTION%>"></input>
						<a onMouseOut="Out('img3')" onMouseOver="Over('img3')" href="javascript:logout()">
		        			<img alt="" border="0" src="images/off.gif" name="img3" width="16" height="13"><img alt="Login" height="13" width="84" border="0" src="images/button_logout.gif">
				 		</a>
				 	</form>
				<%
				}
				else{
				%>
			    	<a onMouseOut="Out('img3')" onMouseOver="Over('img3')" href="javascript:login()">
						<img alt="" border="0" src="images/off.gif" name="img3" width="16" height="13"><img alt="Login" height="13" width="84" border="0" src="images/button_login.gif">
					</a>
   			    <%
		    	}
		    	%>
			</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
	</table>

