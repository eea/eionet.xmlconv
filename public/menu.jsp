<%@ page import="eionet.gdem.conversion.ssr.Names, eionet.gdem.utils.SecurityUtil,com.tee.uit.security.AppUser" %>
<%

	AppUser user = SecurityUtil.getUser(request, Names.USER_ATT);
	String user_name=null;
	if (user!=null)
		user_name = user.getUserName();
	String err = (String)request.getAttribute(Names.ERROR_ATT);

%>
<div id="globalnav">
 <h2>Contents</h2>
  <ul>
  <li><a href="javascript:openPage('<%=Names.SHOW_SCHEMAS_ACTION%>">Show Conversions</a></li>
  <li><a href="javascript:openPage('<%=Names.SHOW_LISTCONVERSION_ACTION%>">Conversions</a></li>
  </ul>

 <h2>Manage</h2>
        <%
                //AppUser user = SecurityUtil.getUser(request);
        %>
        <%
        if (user!=null ){
        %>
        <form name="logout" action="main" method="post">
            <input name="ACTION" type="hidden" value="<%=Names.LOGOUT_ACTION%>"></input>
            <ul>
            <li><a href="javascript:logout()">Logout</a></li>
            </ul>
        </form>
        <%
        }
        else{
        %>
        <ul>
        <li><a href="javascript:login()">Login</a></li>
        </ul>
    <%
}
%>

</div>
