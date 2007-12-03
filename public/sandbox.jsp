<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>
<%@ page import="java.util.Vector,java.util.HashMap,java.util.Hashtable"%>
<%@ page import="eionet.gdem.Constants, eionet.gdem.services.GDEMServices, eionet.gdem.utils.Utils, eionet.gdem.Properties, eionet.gdem.conversion.ssr.InputAnalyser, eionet.gdem.conversion.ssr.Names"%>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
%>

<%
	String q_id = request.getParameter("ID")==null? null:(String)request.getParameter("ID");
	String schema_id = request.getParameter("SCHEMA_ID")==null ? null:(String)request.getParameter("SCHEMA_ID");
	String source_url = request.getParameter("source_url")==null ? null:(String)request.getParameter("source_url");
	String findscripts = request.getParameter("findscripts")==null ? null:(String)request.getParameter("findscripts");
	String error_msg = (request.getAttribute(Names.ERROR_ATT)==null) ? null : (String)request.getAttribute(Names.ERROR_ATT);
	String success_msg = (request.getAttribute(Names.SUCCESS_ATT)==null) ? null : (String)request.getAttribute(Names.SUCCESS_ATT);
	String inserted_src_url = (request.getAttribute(Constants.XQ_INS_SOURCE_PARAM_NAME)==null) ? "" : (String)request.getAttribute(Constants.XQ_INS_SOURCE_PARAM_NAME);
	String xqscript = request.getParameter("XQSCRIPT")==null ? null:(String)request.getParameter("XQSCRIPT");

	String qText = "";
	Vector queries = null;
	String query_file = null;
	String schema_url ="";
	boolean bValidate = false;

	eionet.gdem.services.db.dao.ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();
	eionet.gdem.services.db.dao.IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();	
	if (!Utils.isNullStr(source_url) && !Utils.isNullStr(findscripts)){
		InputAnalyser analyser = new InputAnalyser();
		try{
			analyser.parseXML(source_url);
			String schemaOrDTD = analyser.getSchemaOrDTD();
			schema_id = schemaDao.getSchemaID(schemaOrDTD);
		}
		catch(Exception e){
			//do nothoing - did not find XML Schema
			//handleError(request, response, e);
		}
	}
	if(!Utils.isNullStr(schema_id)) {
      queries = schemaDao.getSchemaQueries(schema_id);
      if (queries == null) queries = new Vector();

      //checks if the validation is a part of QA Service. If yes, then add it to work queue
      HashMap _oSchema = schemaDao.getSchema(schema_id);
      String validate = (String)_oSchema.get("validate");
      schema_url = (String)_oSchema.get("xml_schema");
      bValidate = validate.equals("1") ? true: false;
	}
	if(!Utils.isNullStr(q_id)) {
		qText = queryDao.getQueryText(q_id);
		HashMap queryInfo = queryDao.getQueryInfo(q_id);
		if (queryInfo!=null){
			query_file = Properties.queriesFolder + (String)queryInfo.get("query");
		}
	}

	source_url = Utils.isNullStr(source_url)?inserted_src_url:source_url;
	qText = !Utils.isNullStr(xqscript)? xqscript: qText;
	pageContext.setAttribute("qtext", qText, PageContext.PAGE_SCOPE);
%>

<ed:breadcrumbs-push label="XQuery Sandbox" level="1" />
<tiles:insert definition="TmpHeader">
	<tiles:put name="title" value="XQuery Sandbox"/>
</tiles:insert>



<%@ include file="menu.jsp" %>


<h1>XQuery Sandbox</h1>
	<% if (success_msg!= null) { %>
		<div class="system-msg"><%=success_msg%></div>
	<% } %>
	<% if (error_msg!= null) { %>
		<div class="error-msg"><%=error_msg%></div>
	<% } %>
	<form id="f" action="sandbox" method="post" accept-charset="utf-8">
	  <div id="main_table">
		<label for="dataurlfield">URL to data file </label>
			<input type="text" class="textfield" name="source_url" size="75" id="dataurlfield" value="<%=Utils.isNullStr(source_url) ? "" : source_url%>"/>
			<input type="submit" name="findscripts" value=" Find scripts " title="Reads the XML Schema from the header of XML file and search the scripts from the repository" class="button"/>
		<br/><br/>
		<%
		// Run all scripts for one schema.
		if (!Utils.isNullStr(schema_id)){
			%>
			<input type="hidden" name="sandboxtype" value="SCHEMA"/>
			<input type="hidden" name="xml_schema" value="<%=schema_url%>"/>
			<p><label>XML Schema: </label><a href="<%=schema_url%>"><%=schema_url%></a></p>
			<%if (Utils.isNullVector(queries) && !bValidate){ %>
				<div>No QA scripts found for this XML Schema</div>
			<%} else { %>
				<div>QA Service for selected XML Schema contains the following scripts: </div>
				<%
      			for (int j=0;j<queries.size();j++){
        			HashMap querie = (HashMap)queries.get(j);
        			String query_id = (String)querie.get("query_id");
        			query_file = (String)querie.get("query");
        			String name = (String)querie.get("short_name");
        			%>
        				<input type="radio" id="opt<%=query_id%>" name="script" value="<%=query_id%>" <% if (j == 0) %>checked="checked"<%;%>/>
        				<label for="opt<%=query_id%>"><%=name%> - </label><a  href="<%=Names.QUERY_FOLDER%><%=query_file%>"><%=query_file%></a><br/>
        			<%
				}
				if (bValidate){
					%><input type="radio" id="optValidate" name="script" value="-1" <% if (queries.size() == 0) %>checked="true"<%;%>/>
							<label for="optValidate">XML Schema validation</label><%
				}
        		%>
        		<%
    		}
		}
		else{
		%>
			<input type="hidden" name="sandboxtype" value="SCRIPT"/>
			<label for="scriptarea">XQuery script</label>
			<textarea name="XQSCRIPT" rows="25" cols="100" style="width:99%" id="scriptarea"><c:out value="${qtext}" escapeXml="true" /></textarea>
		<%}%>
		<br/><br/>
		<input type="submit" name="runnow" value=" Run now " class="button" />
		<%
		boolean wqPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_WQ_PATH, "i");
		boolean wquPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_WQ_PATH, "u");
		if(wqPrm) {
		%>
			<input type="submit" name="queue" value=" Add to workqueue " class="button"/>
		<% }
		if(wquPrm && !Utils.isNullStr(q_id)) {
		%>
			<input type="hidden" name="file_name" value="<%=query_file==null ? "" : query_file%>" />

			<input type="submit" name="save" value=" Save changes to file " />
		<% } %>
		<input type="hidden" name="ID" value="<%=(q_id==null) ? "" : q_id%>" />
		<input type="hidden" name="SCHEMA_ID" value="<%=(schema_id==null) ? "" : schema_id%>" />
		</div>
	</form>
<tiles:insert definition="TmpFooter"/>
