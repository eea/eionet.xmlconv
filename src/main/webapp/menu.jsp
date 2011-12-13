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

