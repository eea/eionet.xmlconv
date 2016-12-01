<%@ page import="eionet.gdem.Constants, eionet.gdem.utils.SecurityUtil,eionet.acl.AppUser, eionet.gdem.Properties" %>
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
    AppUser user = SecurityUtil.getUser(request, Constants.USER_ATT);
    String user_name=null;
    if (user!=null)
        user_name = user.getUserName();
    String err = (String)request.getAttribute(Constants.ERROR_ATT);
  boolean hovPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_HOST_PATH, "v");

%>

