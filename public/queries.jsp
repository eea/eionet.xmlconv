<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ page import="java.io.File,java.util.Date,java.text.DateFormat,java.util.HashMap, java.util.Vector, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, eionet.gdem.Properties" %>

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
    Vector queries = null;
    String validate = "0";
    String schema_id = "0";

    if (list.size()>0){
    
        schema = (HashMap)list.get(0);
        name = (String)schema.get("xml_schema");
        schema_desc = (String)schema.get("description");
        validate = (String)schema.get("validate");
        schema_id = (String)schema.get("schema_id");
        queries = (Vector)schema.get("queries");
    }
    if (queries==null) queries=new Vector();
%>

<ed:breadcrumbs-push label="Queries" level="1" />
<tiles:insert definition="MainHeader"/>

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




<%@ include file="menu.jsp" %>

    
<div id="workarea">

    <% if (err!= null) { %>
            <h4><%=err%></h4>
    <% } %>

    <%
    boolean ssiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "i");
	boolean xsduPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "u");
	%>
    <div id="operations">
    <ul>
		<% if (ssiPrm){%>
        	<li><a href="<%=Names.ADD_QUERY_JSP%>?ID=<%=id%>" title="Add a new XQuery">Add Query</a></li>
		<%}%>
        <li><a href="<%=Names.SANDBOX_JSP%>?SCHEMA_ID=<%=id%>" title="Run all XQuery scripts for this schema">Run QA Service</a></li>
    </ul>
    </div>
    
    <h1>Queries of <%=name%></h1>
    <a href="do/schemaElemForm?schemaId=<%=id%>">View schema info</a>
    <form name="view_schema_info" action="main" method="post">
        <input type="hidden" name="ID" value="<%=id%>" />
        <input type="hidden" name="ACTION" value="<%=Names.SHOW_SCHEMA_ACTION%>" />
    </form>

	<form name="upd_xsd" action="main" method="post">
		<table cellspacing="0">
			<tr>
				<td align="right" style="padding-right:5">
					<label for="validatefield">XML Schema validation is a part of QA Service for this type of XML files:</label>
				</td>
				<td align="left">
					<%	if (xsduPrm){ %>
						<input type="checkbox" class="textfield" value="1" name="VALIDATE" <%if (validate.equals("1")){%>checked="true"<%}%> id="validatefield" />
					<%} else {
						String str_validate = validate.equals("1")  ? "yes" : "no";
						%>
						<div id="validatefield"><%=str_validate%></div>
						<%
					} %>
				</td>
				<td>
				<%	if (xsduPrm){ %>
					<input name="SUBMIT" type="submit" value="Save" class="smallbutton"></input>
				<%}
				%>
 				</td>
			</tr>
		</table>
		<input type="hidden" name="ACTION" value="<%=Names.XSD_UPDVAL_ACTION%>" />
		<input type="hidden" name="<%=Names.SCHEMA_ID%>" value="<%=schema_id%>" />
	</form>
		
    <div id="main_table">
        <table class="sortable" border="0" cellspacing="1" cellpadding="2" width="100%">
            <thead>
                    
               <%
                boolean ssdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "d");
            %>
                <tr>
                  <th scope="col" align="left" width="180">Short name</th>
                  <th scope="col" align="left">Description</th>
                  <th scope="col" align="left" width="180">Query</th>
                  <th scope="col" align="left" width="140">Last modified</th>
                  <th scope="col" align="center" width="50">&#160;</th>
                </tr>
            </thead>
           <tbody>
               <%
               

                for (int i=0; i<queries.size(); i++){
                    HashMap hash = (HashMap)queries.get(i);
                    String query_id = (String)hash.get("query_id");
                    String query = (String)hash.get("query");
                    String short_name = (String)hash.get("short_name");
                    String description = (String)hash.get("description");
					
                    File f=new File(Properties.queriesFolder + query);	
					String last_modified="";
					
					if (f!=null)
						last_modified = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM).format(new Date(f.lastModified()));

					%>
                    <tr>
                        <td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
                        <a href="<%=Names.QUERY_JSP%>?query_id=<%=query_id%>" title="Edit/View query metadata">&#160;<%=short_name%></a>
                        </td>
                        <td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>><%=description%></td>
                        <td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>><a target="blank" href="<%=Names.QUERY_FOLDER%><%=query%>"><%=query%></a></td>
                        <td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>><%=last_modified%></td>
	                    <td align="center" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
                             <a href="<%=Names.SANDBOX_JSP%>?ID=<%=query_id%>"><img height="15" width="24" src="images/run.png" alt="Run" title="Run this query in XQuery Sandbox"></img></a>
                             <%
                            if (ssdPrm){%>
                                <img onclick="ss_<%=query_id%>.submit();" height="15" width="15" src="images/delete.png" title="Delete query"></img>
                            <%}%>
                          </td>
                          <td>
	                         	<form name="ss_<%=query_id%>" action="main" method="post">
                            	<input type="hidden" name="ACTION" value="<%=Names.QUERY_DEL_ACTION%>"/>
                            	<input type="hidden" name="QUERY_DEL_ID" value="<%=query_id%>"/>
                            	<input type="hidden" name="ID" value="<%=id%>"/>
                        		</form>
                        </td>        
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
<tiles:insert definition="MainFooter"/>