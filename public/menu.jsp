<%@ page import="eionet.gdem.conversion.ssr.Names, eionet.gdem.utils.SecurityUtil,com.tee.uit.security.AppUser, eionet.gdem.Properties" %>
<%!
	//
    private boolean serviceInstalled(int service){

		int services_installed = Properties.services_installed;

        // we divide displayWhen with the type's weight
        // and if the result is an odd number, we return true
        // if not, we return false
        int div = services_installed/service;
        
        if (div % 2 != 0)
            return true;
        else
            return false;
    }
%>
<%
	AppUser user = SecurityUtil.getUser(request, Names.USER_ATT);
	String user_name=null;
	if (user!=null)
		user_name = user.getUserName();
	String err = (String)request.getAttribute(Names.ERROR_ATT);
  boolean hovPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_HOST_PATH, "v");

%>
<div id="globalnav">
 <h2>Contents</h2>
  <ul>
  <%if (serviceInstalled(Properties.CONV_SERVICE)){%>
  	<li><a href="<%=Names.INDEX_JSP%>">Stylesheets</a></li>
  	<li><a href="<%=Names.LIST_CONVERSION_JSP%>">Converter</a></li>
  <%}%>
  <%if (serviceInstalled(Properties.QA_SERVICE)){%>
	<li><a href="<%=Names.LIST_WORKQUEUE_JSP%>">QA Jobs</a></li>
 	<li><a href="<%=Names.QUERIESINDEX_JSP%>">Queries</a></li>
	<li><a href="<%=Names.SANDBOX_JSP%>">XQ Sandbox</a></li>
  <%}%>
  <%
  if (hovPrm){
	  %>
  	<li><a href="<%=Names.HOSTS_JSP%>">Hosts</a></li>
  	<%
  	}
  %>
  

    </ul>

 <h2>Manage</h2>
        <%
                //AppUser user = SecurityUtil.getUser(request);
        %>
        <%
        if (user!=null ){
        %>
        <form name="logout" action="main" method="post">
            <input name="ACTION" type="hidden" value="<%=Names.LOGOUT_ACTION%>" />
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
