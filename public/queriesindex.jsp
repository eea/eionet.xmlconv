<%@page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, eionet.gdem.utils.SecurityUtil,com.tee.uit.security.AppUser" %>

<%
	//AppUser user = SecurityUtil.getUser(request);


	eionet.gdem.services.db.dao.ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
	Vector list = schemaDao.getSchemas(null);
	if (list==null) 
		list=new Vector();


%>
<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
%>

<ed:breadcrumbs-push label="Queries" level="1" />
<tiles:insert definition="TmpHeader">
	<tiles:put name="title" value="Queries"/>
</tiles:insert>


    <script type="text/javascript" src="util.js"></script>
    <script type="text/javascript">
// <![CDATA[
		function openPage(action) {
			document.getElementById("submit_action").value=action;
			document.getElementById("f").submit();
		}
		function openXSD(action, id) {
			document.getElementById("submit_action").value=action;
			document.getElementById("submit_id").value=id;
			document.getElementById("f").submit();
		}
// ]]>
		</script>


<%@ include file="menu.jsp" %>




    <%
    boolean ssiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "i");
    if (ssiPrm){
    %>
    <div id="operations">
				<ul>
					<li><a href="<%=Names.ADD_QUERY_JSP%>" title="Add a new XQuery">Add Query</a></li>
 				</ul>
    </div>
    <%}%>


		<h1>Queries</h1>

		<% if (err!= null) { %>
				<div class="error-msg"><%=err%></div>
	  	<% } %>

		<div id="main_table">
			<table class="datatable" width="100%">
			<%
			boolean ssdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "d");
			%>
			  <%if (ssdPrm){%>
				<col style="width:45%"/>
				<col style="width:50%"/>
				<col style="width:5%"/>
				<% } else {%>
				<col style="width:50%"/>
				<col style="width:50%"/>
				<%}%>
				<thead>
            <tr>
							<th  scope="col" align="left">XML Schema</th>
              <th  scope="col" align="left">Queries</th>
			  <%if (ssdPrm){%>
     			  <th align="center">&#160;</th>
     		  <%}%>
		    </tr>
		   </thead>
		   <tbody>
				<%

				int r = 0;

				for (int i=0; i<list.size(); i++){
					HashMap schema = (HashMap)list.get(i);
					String name = (String)schema.get("xml_schema");
					String id = (String)schema.get("schema_id");
					String schema_descr = (String)schema.get("description");
					if (schema_descr==null) schema_descr="";
					if (!schema.containsKey("queries")) continue;
					Vector queries = (Vector)schema.get("queries");

					if (queries.size() ==0) continue;

					%>
					<tr <% if (i % 2 != 0) %>class="zebraeven"<% else %>class="zebraodd"<%;%>>
						<td align="left">
							<a href="javascript:openXSD('<%=Names.SHOW_QUERIES_ACTION%>', <%=id%>)" title="<%=schema_descr%>"><%=name%></a>
						</td>
						<td align="left">
						<%
						for (int j=0; j<queries.size(); j++){
							HashMap q = (HashMap)queries.get(j);
							String query = (String)q.get("query");
							String short_name = (String)q.get("short_name");
							String query_descr = (String)q.get("description");

							if (j>0) %>,&#160;<%
							%><a  href="<%=Names.QUERY_FOLDER%><%=query%>" title="<%=query_descr%>"><%=short_name%></a><%

						}
						%>
						</td>
 	         				<%
							if (ssdPrm){%>
 	         			<td align="center" >
								<a href="javascript:if (confirm('Are you sure you want to delete the schema and all its queries?')) document.getElementById('ss_<%=id%>').submit();"><img src="images/delete.gif" title="Delete schema and all its queries" alt="Delete"></img></a>
						<form id="ss_<%=id%>" action="main" method="post">
							<div>
								<input type="hidden" name="ACTION" id="delete_action_<%=id%>" value="<%=Names.XSDQ_DEL_ACTION%>" />
								<input type="hidden" name="XSD_DEL_ID" value="<%=id%>" />
							</div>
						</form>
 	         			</td>
							<%}%>
					</tr>
					<%
					r++;
				}
				%>
			</tbody>
		</table>
	</div>

	<form id="f" action="main" method="get">
		<div>
			<input type="hidden" name="ACTION" id="submit_action" value=""/>
			<input type="hidden" name="ID" id="submit_id" value=""/>
		</div>
	</form>
<tiles:insert definition="TmpFooter"/>
