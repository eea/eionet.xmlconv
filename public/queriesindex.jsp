<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, eionet.gdem.utils.SecurityUtil,com.tee.uit.security.AppUser" %>

<%
	//AppUser user = SecurityUtil.getUser(request);


	DbModuleIF dbM= GDEMServices.getDbModule();
	Vector list = dbM.getSchemas(null);
	if (list==null) list=new Vector();

%>


<ed:breadcrumbs-push label="Queries" level="1" />
<tiles:insert definition="TmpHeader">
	<tiles:put name="title" value="Queries"/>
</tiles:insert>


    <script type="text/javascript" src="util.js"></script>
    <script type="text/javascript">
// <![CDATA[
		function openPage(action) {
			document.forms["f"].ACTION.value=action;
			document.forms["f"].submit();
		}
		function openXSD(action, id) {
			document.forms["f"].ACTION.value=action;
			document.forms["f"].ID.value=id;
			document.forms["f"].submit();
		}
// ]]>
		</script>




<%@ include file="menu.jsp" %>



<div id="workarea">
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


		<% if (err!= null) { %>
			<h4><%=err%></h4>
	  	<% } %>
			<h1>Queries</h1>

		<div id="main_table">
			<table class="sortable" width="100%">
            <%
			boolean ssdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "d");
			%>
				<thead>
            <tr>
		      <th  scope="col" align="left" width="*">XML Schema</th>
              <th  scope="col" align="left" width="50%">Queries</th>
			  <%
				if (ssdPrm){%>
     			  <th align="center" width="5%">&#160;</th>
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
						<td align="left" style="padding-left:5;padding-right:10" >
							<a href="javascript:openXSD('<%=Names.SHOW_QUERIES_ACTION%>', <%=id%>)" title="<%=schema_descr%>"><%=name%></a>
						</td>
						<td align="left" style="padding-left:5;padding-right:10" >
						<%
						for (int j=0; j<queries.size(); j++){
							HashMap q = (HashMap)queries.get(j);
							String query = (String)q.get("query");
							String short_name = (String)q.get("short_name");
							String query_descr = (String)q.get("description");

							if (j>0) %>,&#160;<%
							%><a target="blank" href="<%=Names.QUERY_FOLDER%><%=query%>" title="<%=query_descr%>"><%=short_name%></a><%

						}
						%>
						</td>
 	         				<%
							if (ssdPrm){%>
 	         			<td align="center" >
								<img onclick="ss_<%=id%>.submit();" height="15" width="15" src="images/delete.png" title="Delete schema and all it's queries"></img>
						<form name="ss_<%=id%>" action="main" method="post">
							<input type="hidden" name="ACTION" value="<%=Names.XSDQ_DEL_ACTION%>" />
							<input type="hidden" name="XSD_DEL_ID" value="<%=id%>" />
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
	</div>

	<form name="f" action="main" method="get">
		<input type="hidden" name="ACTION" value=""/>
		<input type="hidden" name="ID" value=""/>
	</form>
<tiles:insert definition="TmpFooter"/>
