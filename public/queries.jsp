<%@page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ page import="java.io.File,java.util.Date,java.text.DateFormat,java.util.HashMap, java.util.Vector, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, eionet.gdem.Properties,eionet.gdem.Properties,eionet.gdem.utils.Utils" %>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
%>

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

    eionet.gdem.services.db.dao.ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
    Vector list = schemaDao.getSchemas(id);
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

<ed:breadcrumbs-push label="XML Schema QA Scripts" level="2" />

<tiles:insert definition="TmpHeader">
	<tiles:put name="title" value="QA Scripts"/>
</tiles:insert>

<script type="text/javascript" src="util.js"></script>

<%@ include file="menu.jsp" %>


    <%
    boolean ssiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "i");
	boolean xsduPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SCHEMA_PATH, "u");

	String schemaPage = "/do/viewSchemaForm?schemaId=" + id;
	String xslPage = "/do/schemaStylesheets?schema=" + name;

	%>
		<div id="tabbedmenu">   	
  			<ul>
      			<li>
           			<html:link page="<%=schemaPage%>"   titleKey="label.tab.title.schema" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
               			<bean:message key="label.tab.title.schema" />
           			</html:link>
       			</li>
       			<li>
                <html:link page="<%=xslPage %>"   titleKey="label.tab.title.xsl" onclick="return submitTab(this);"    style="color: black; text-decoration: none;">
                    <bean:message key="label.tab.title.xsl" />
                </html:link>
       			</li>
       			<li id="currenttab">
       				<span style="color: black; text-decoration: none;" title='<bean:message key="label.tab.title.scripts"/>'><bean:message key="label.tab.title.scripts" /></span>
       			</li>
   			</ul>
		</div>
				
	<div id="operations">
    <ul>
		<% if (ssiPrm){%>
        	<li><a href="<%=Names.ADD_QUERY_JSP%>?ID=<%=id%>" title="Add a new XQuery">Add Query</a></li>
		<%}%>
        <li><a href="<%=Names.SANDBOX_JSP%>?SCHEMA_ID=<%=id%>" title="Run all XQuery scripts for this schema">Run QA Service</a></li>
    </ul>
    </div>

    <h1>QA Scripts of <%=name%></h1>

    <% if (err!= null) { %>
            <div class="error-msg"><%=err%></div>
    <% } %>
	
	<form id="view_schema_info" action="main" method="post">
		<div>
		    <input type="hidden" name="ID" value="<%=id%>" />
	        <input type="hidden" name="ACTION" value="<%=Names.SHOW_SCHEMA_ACTION%>" />
		</div>
    </form>

	<form id="upd_xsd" action="main" method="post">
		<table cellspacing="0">
			<tr>
				<td align="right" style="padding-right:5">
					<label for="validatefield">XML Schema validation is a part of QA Service for this type of XML files:</label>
				</td>
				<td align="left">
					<%	if (xsduPrm){ %>
						<input type="checkbox" class="textfield" value="1" name="VALIDATE" <%if (validate.equals("1")){%>checked="checked"<%}%> id="validatefield" />
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
		<div>
			<input type="hidden" name="ACTION" value="<%=Names.XSD_UPDVAL_ACTION%>" />
			<input type="hidden" name="<%=Names.SCHEMA_ID%>" value="<%=schema_id%>" />
		</div>
	</form>

    <div id="main_table">
        <table class="datatable" width="100%">
			<col width="180"/>
			<col width="*"/>
			<col width="180"/>
			<col width="140"/>
			<col width="30"/>
			<col width="30"/>
            <thead>

               <%
                boolean ssdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "d");
            %>
                <tr>
                  <th scope="col">Short name</th>
                  <th scope="col">Description</th>
                  <th scope="col">Script</th>
                  <th scope="col">Last modified</th>
                  <th scope="col">&#160;</th>
                  <th scope="col">&#160;</th>
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
						last_modified=Utils.getDateTime(new Date(f.lastModified()));
						//last_modified = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM).format(new Date(f.lastModified()));

					%>
                    <tr <% if (i % 2 != 0) %>class="zebraeven"<% else %>class="zebraodd"<%;%> valign="top">
                        <td>
                        <a href="<%=Names.QUERY_JSP%>?query_id=<%=query_id%>" title="Edit/View query metadata">&#160;<%=short_name%></a>
                        </td>
                        <td><%=description%></td>
                        <td><a  href="<%=Names.QUERY_FOLDER%><%=query%>"><%=query%></a></td>
                        <td><%=last_modified%></td>
	                    <td>
                             <a href="<%=Names.SANDBOX_JSP%>?ID=<%=query_id%>"><img src="images/execute.gif" alt="Run" title="Run this query in XQuery Sandbox"></img></a>
						</td>
						<td>
                             <%
                            if (ssdPrm){%>
								<a href="javascript:if (confirm('Are you sure you want to delete the query?')) document.getElementById('ss_<%=query_id%>').submit();">
									<img src="images/delete.gif" title="Delete query" alt="Delete"></img>
								</a>
                            <%}%>
                          </td>
                          <td>
                         	<form id="ss_<%=query_id%>" action="main" method="post">
								<div>
		                        	<input type="hidden" name="ACTION" value="<%=Names.QUERY_DEL_ACTION%>"/>
			                    	<input type="hidden" name="QUERY_DEL_ID" value="<%=query_id%>"/>
				                	<input type="hidden" name="ID" value="<%=id%>"/>
								</div>
                       		</form>
                        </td>
                    </tr>
                    <%
                   }
               %>
			</tbody>
		</table>
  </div>

<form id="f" action="main" method="post">
	<div>
	    <input type="hidden" name="ACTION" value=""/>
		<input type="hidden" name="PARAM" value=""/>
	</div>
</form>

<tiles:insert definition="TmpFooter"/>
