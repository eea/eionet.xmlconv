<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.HashMap, java.util.Vector, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names" %>

<%!private HashMap schema=null;%>


<%
    //get schema id from parameter
    String id = (String)request.getParameter(Names.SCHEMA_ID);
    if (id == null || id.length()==0){ 
        id = (String)request.getAttribute(Names.SCHEMA_ID); //if stylesheet is added
        if (id == null || id.length()==0){ 
            id="0";
        }
        
    }
    DbModuleIF dbM= GDEMServices.getDbModule();

    Vector list = dbM.getSchemas(id);
    if (list==null) list=new Vector();
    
    String name = "";
    String schema_desc = null;
    Vector stylesheets = null;

    if (list.size()>0){
    
        schema = (HashMap)list.get(0);
        name = (String)schema.get("xml_schema");
        schema_desc = (String)schema.get("description");
        stylesheets = (Vector)schema.get("stylesheets");
    }
    if (stylesheets==null) stylesheets=new Vector();
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Stylesheets of <%=name%></title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
    <script type="text/javascript" src="util.js"></script>
    <script type="text/javascript">
        
        function openApp(appName) {
            document.forms["f"].app.value=appName;
            document.forms["f"].ACTION.value="";
            document.forms["f"].ACL.value="/";
            document.forms["f"].submit();
        }
        function doLogin(appName) {
            window.open("appLogin.jsp?app=" + appName,"login","height=200,width=300,status=no,toolbar=no,scrollbars=no,resizable=no,menubar=no,location=no");
        }

    </script>
</head>
<body>
<jsp:include page="location.jsp" flush='true'>
<jsp:param name="name" value="Stylesheets"/>
</jsp:include>
<%@ include file="menu.jsp" %>
<div id="workarea">

    <% if (err!= null) { %>
            <h4><%=err%></h4>
    <% } %>

    <%
    boolean ssiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "i");
    if (ssiPrm){%>
    <div id="operations">
    <ul>
        <li><a href="<%=Names.ADD_XSL_JSP%>?ID=<%=id%>"
        title="Add a new Stylesheet">Add Stylesheet</a></li>
    </ul>
    </div>
    <%}%>
    
    <h1>Stylesheets of <%=name%></h1>
    <a href="schema.jsp?ID=<%=id%>">View schema info</a>
    <form name="view_schema_info" action="main" method="post">
        <input type="hidden" name="ID" value="<%=id%>" />
        <input type="hidden" name="ACTION" value="<%=Names.SHOW_SCHEMA_ACTION%>" />
    </form>

    <br/><br/>

    <div id="main_table">
        <table border="0" cellspacing="1" cellpadding="2" width="100%">
            <thead>
                    
               <%
                boolean ssdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "d");
                boolean convPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_TESTCONVERSION_PATH, "x");
            %>
                <tr>
                  <th align="middle" width="40">Type</th>
                  <th align="left">Description</th>
                  <th align="left" width="200">Stylesheet</th>
                       <%
                    if (ssdPrm){%>
                         <th align="middle" width="20">&#160;</th>
                    <%}%>                         
                </tr>
            </thead>
           <tbody>
               <%
               

                for (int i=0; i<stylesheets.size(); i++){
                    HashMap hash = (HashMap)stylesheets.get(i);
                    String convert_id = (String)hash.get("convert_id");
                    String xsl = (String)hash.get("xsl");
                    String type = (String)hash.get("content_type_out");
                       String description = (String)hash.get("description");
                       %>
                    <tr height="5">
                        <td align="middle" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
                             <%
                            if (convPrm){%>
                                <a title="Test conversion" href="main?ACTION=<%=Names.SHOW_TESTCONVERSION_ACTION%>&amp;ID=<%=convert_id%>&amp;SCHEMA_ID=<%=id%>"><%=type%></a>
                            <%}else{%>
                                <%=type%>
                            <%}%>
                        </td>
                        <td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>><%=description%></td>
                        <td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>><a target="blank" href="<%=Names.XSL_FOLDER%><%=xsl%>"><%=xsl%></a></td>
                          <td align="middle" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
                             <%
                            if (ssdPrm){%>
                                <img onclick="ss_<%=convert_id%>.submit();" height="15" width="15" src="images/delete.png" title="Delete stylesheet"></img>
                            <%}%>
                          </td>
                        <form name="ss_<%=convert_id%>" action="main" method="post">
                            <input type="hidden" name="ACTION" value="<%=Names.XSL_DEL_ACTION%>"/>
                            <input type="hidden" name="XSL_DEL_ID" value="<%=convert_id%>"/>
                            <input type="hidden" name="ID" value="<%=id%>"/>
                        </form>        
                    </tr>
                    <%
                   }
               %>
                </tbody>
             </table>
        </div> 
  </div>

<form name="f" action="main" method="post">
    <input type="hidden" name="ACTION" value=""/>
    <input type="hidden" name="PARAM" value=""/>
</form>
<%@ include file="footer.jsp" %>
</body>
</html>
