<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page contentType="text/html" import="eionet.gdem.Constants"%>
<%

    String err = (String)request.getAttribute(Constants.ERROR_ATT);

%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <script type="text/javascript">
// <![CDATA[
    function setFocus(){
            var t;
            t=document.getElementById("j_username");
            t.focus();
    }
// ]]>
    </script>
</head>
<body bgcolor="#f0f0f0" onload="setFocus()">
        <form name="f" action="main" method="post">
            <input name="ACTION" type="hidden" value="<%=Constants.LOGIN_ACTION%>" />
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
                <tr>
                    <td colspan="3"></td>
                </tr>
                <tr>
                    <td width="200"></td>
                    <td>
                        <input type="submit" name="ok_btn" value="  OK  " />
                    </td>
                    <td><input type="button" name="cancel_btn" value="  Cancel  " onclick="javascript:window.close()" /></td>
                </tr>
            </table>
        </form>
</body>
</html>
