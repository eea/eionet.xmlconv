<%@ page import="eionet.gdem.ssr.Names, eionet.gdem.ssr.SecurityUtil,com.tee.uit.security.AppUser" %>
<%

	AppUser user = SecurityUtil.getUser(request);
	String user_name=null;
	if (user!=null)
		user_name = user.getUserName();
	String err = (String)request.getAttribute(Names.ERROR_ATT);

%>

<SCRIPT language="JavaScript" src="util.js"></SCRIPT>
<SCRIPT language=JavaScript>
		Net=1;

		if ((navigator.appName.substring(0,5) == "Netsc"
			&& navigator.appVersion.charAt(0) > 2)
			|| (navigator.appName.substring(0,5) == "Micro"
			&& navigator.appVersion.charAt(0) > 3)) {
		 Net=0;

		 over = new Image;
		 out = new Image;
		 gammel = new Image;

		 over.src = "images/on.gif";
		 out.src = "images/off.gif";
		 
		 gTarget = 'img1';
		}

		//var browser = document.all ? 'E' : 'N';
		//var picklist = new Array();

				
		function logout() {
			document.forms["logout"].submit();		
		}
		function login() {
			window.open("<%=Names.LOGIN_JSP%>","login","height=200,width=300,status=no,toolbar=no,scrollbars=no,resizable=no,menubar=no,location=no");
		}
</SCRIPT>
			<TABLE cellSpacing=0 cellPadding=0 border=0>
        <TR>
          <TD align=center><SPAN class=head0>Contents</SPAN>
					</TD>
				</TR>
				<TR>
					<TD align=right><A onmouseover="Over('img1')" onmouseout="Out('img1')" href="main" onclick="Click('img1')">
						<IMG height=13 alt="" src="images/off.gif" width=16 border=0 name="img1"><IMG height=13 alt="Show Conversions" src="images/button_Conversions.gif" width=84 border=0></A></TD>
				</TR>
				<TR><TD>&nbsp;</TD></TR>
				<%
				//AppUser user = SecurityUtil.getUser(request);
				%>
				<tr>
					<td align="right">
						<%
						if (user!=null ){
							%>
		        			<FORM name="logout" action="main" method="POST">
								<INPUT name="ACTION" type="hidden" value="<%=Names.LOGOUT_ACTION%>"></INPUT>
								<a onMouseOut="Out('img3')" onMouseOver="Over('img3')" href="javascript:logout()">
		        					<img alt="" border="0" src="images/off.gif" name="img3" width="16" height="13"><img alt="Login" height="13" width="84" border="0" src="images/button_logout.gif">
				 				</a>
				 			</FORM>
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
				<TR><TD>&nbsp;</TD></TR>
	
		</TABLE>

