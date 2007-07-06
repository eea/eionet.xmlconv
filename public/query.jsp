<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>
<%@ page import="java.io.File,java.util.Date,java.text.DateFormat,java.util.HashMap, java.util.Vector, java.util.Hashtable, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, eionet.gdem.utils.Utils, eionet.gdem.Properties" %>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
response.setHeader("charset","no-store");

%>

<%
	//get schema from parameter
	String id = request.getParameter("query_id");
	//String qText = "";
	String last_modified="";

	id = (id == null ? "" : id);

	HashMap query =null;
	String mode="view";
	String xml_schema = "";
	String short_name= "";
	String description = "";
	String content_type = "";
	String file = "";
	String schema_id = "";
	String checksum = "";
	String qText = "";

    eionet.gdem.services.db.dao.IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
	
	if (!id.equals("")){
		query = queryDao.getQueryInfo(id);
		if (query==null) query=new HashMap();

		xml_schema = (String)query.get("xml_schema");
		short_name= (String)query.get("short_name");
		description = (String)query.get("description");
		content_type = (String)query.get("content_type");
		file = (String)query.get("query");
		schema_id = (String)query.get("schema_id");

		if(!Utils.isNullStr(file)) {
			qText = queryDao.getQueryText(id);
			pageContext.setAttribute("qtext", qText, PageContext.PAGE_SCOPE);
			try{
		        File f=new File(Properties.queriesFolder + file);

				if (f!=null){
					last_modified=Utils.getDateTime(new Date(f.lastModified()));
					checksum = Utils.getChecksumFromFile(Properties.queriesFolder + file);
				}
			}
			catch(Exception e){
			}
		}
	}

%>

<ed:breadcrumbs-push label="Query" level="1" />
<tiles:insert definition="TmpHeader"/>



<%@ include file="menu.jsp" %>


	<div id="operations">
		<ul>
			<%
			if(Utils.isNullStr(schema_id)){%>
				<li><a href="main">Back to Queries</a></li>
			<%} else { %>
				<li><a href="<%=Names.QUERIES_JSP%>?ID=<%=schema_id%>">Back to XML Schema</a></li>

			<%}%>
			<li><a href="<%=Names.SANDBOX_JSP%>?ID=<%=id%>" title="Run query in Sandbox">Run Query</a></li>
		</ul>
	</div>

		<% if (err!= null) { %>
			<h1><font color="#FF0000"><%=err%></font></h1>
		<% } %>
		<%
		boolean xquPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "u");

		if(xquPrm)
			mode="edit";

		if (mode.equals("edit")){%>
			<h1>Edit query</h1>
		<%}
		else{%>
			<h1>View query</h1>
		<%}%>
		
		<form name="Upload" action="main?query_id=<%=id%>&amp;ACTION=<%=Names.QUERY_UPD_ACTION%>" method="post" enctype="multipart/form-data" acceptcharset="utf-8">

			<input type="hidden" size="60" name="SCHEMA_ID" value="<%=schema_id%>"/>
			<input type="hidden" size="60" name="QUERY_ID" value="<%=id%>"/>
			
			<table class="datatable" width="100%">
				<col style="width:16%"/>
				<col style="width:84%"/>
				<tr>
					<th scope="row" class="scope-row">
						<label for="schemafield">XML Schema:</label>
					</th>
					<td>
						<div id="schemafield"><%=xml_schema%></div>
					</td>
				</tr>
				<tr>
					<th scope="row" class="scope-row">
						<label for="shortnamefield">Short Name:</label>
					</th>
					<td>
						<%if(!mode.equals("view")){%>
							<input type="text" id="shortnamefield" class="textfield" size="64" name="SHORT_NAME" value="<%=short_name%>" />
						<%}else{%>
							<div id="shortnamefield"><%=short_name%></div>
						<%}%>
					</td>
				</tr>
				<tr>
					<th scope="row" class="scope-row">
						<label for="descriptionfield">Description:</label>
					</th>
					<td>
						<%if(!mode.equals("view")){%>
							<textarea class="small" rows="2" cols="55" name="DESCRIPTION" id="descriptionfield" style="width: 98%;"><%=description%></textarea>
						<%}else{%>
							<div id="descriptionfield"><%=description%></div>
						<%}%>
					</td>
				</tr>
				<tr>
					<th scope="row" class="scope-row">
						<label for="contenttypefield">Content type:</label>
					</th>
					<td>
						<%if(!mode.equals("view")){%>
							<select class="small" name="CONTENT_TYPE" id="contenttypefield" title="rrr" alt="jjj">
								<option value="HTML" <% if (content_type.equals("HTML")) %>selected="selected"<%;%>>HTML</option>
								<option value="XML" <% if (content_type.equals("XML")) %>selected="selected"<%;%>>XML</option>
								<option value="TXT" <% if (content_type.equals("TXT")) %>selected="selected"<%;%>>TEXT</option>
							</select>
						<%}else{%>
							<div id="contenttypefield"><%=content_type%></div>
						<%}%>
					</td>
				</tr>
				<tr>
					<th scope="row" class="scope-row">
						<label for="filefield">XQuery file:</label>
					</th>
					<td>
						<a href="<%=Names.QUERY_FOLDER%><%=file%>" title="View XQuery source"><%=file%></a>
						<%if(!Utils.isNullStr(last_modified)){%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Last modified: <%=last_modified%>)<%}%>
					</td>
				</tr>
				<%if(!mode.equals("view")){%>
					<%if(!Utils.isNullStr(file)){%>
						<tr>
							<th scope="row" class="scope-row"></th>
							<td>
								<textarea name="FILEDATA" style="width: 98%;" rows="20" wrap="off"><c:out value="${qtext}" escapeXml="true" /></textarea> 
								<%-- checksum:<%=checksum --%>
								<input type="hidden" name="CHECKSUM" value="<%=checksum%>"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td>
								<input name="SAVE" type="SUBMIT" class="mediumbuttonb" value="Save changes" />
							</td>
						</tr>
					<% }%>
					<tr>
						<th scope="row" class="scope-row"></th>
						<td>
							<input type="file" class="textfield" name="FILE_INPUT" id="filefield" size="53" title="Add a new XQuery file"/>
							<input type="hidden" name="FILE_NAME" value="<%=file%>"/>
						</td>
					</tr>
				<% }%>
				<!--tr><td colspan="2"></td></tr-->
				<%if(!mode.equals("view")){%>
					<tr>
						<td></td>
						<td>
							<input name="UPLOAD" type="SUBMIT" class="mediumbuttonb" value="Upload" />
						</td>
					</tr>
				<% } %>
			</table>
			<input type="hidden" name="ACTION" value="<%=Names.QUERY_UPD_ACTION%>"/>
		</form>

	</div>
<tiles:insert definition="TmpFooter"/>

