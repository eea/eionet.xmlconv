<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ page import="java.util.Vector,java.util.HashMap,java.util.Hashtable"%>
<%@ page import="eionet.gdem.Constants, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.utils.Utils, eionet.gdem.Properties, eionet.gdem.conversion.ssr.InputAnalyser"%>
<%
	String q_id = (String)request.getParameter("ID");
	String schema_id = (String)request.getParameter("SCHEMA_ID");
	String source_url = (String)request.getParameter("SOURCE_URL");
	
	String qText = "";
	Vector queries = null;
	String query_file = null;
	String schema_url ="";
	boolean bValidate = false;
	DbModuleIF dbM= GDEMServices.getDbModule();

	if (!Utils.isNullStr(source_url)){
		InputAnalyser analyser = new InputAnalyser();
		try{
			analyser.parseXML(source_url);
			String schemaOrDTD = analyser.getSchemaOrDTD();
			schema_id = dbM.getSchemaID(schemaOrDTD);
		}
		catch(Exception e){
			//do nothoing - did not find XML Schema
			//handleError(request, response, e);
		}
	}
	if(schema_id != null) {
      queries = dbM.getSchemaQueries(schema_id);
      if (queries == null) queries = new Vector();
      
      //checks if the validation is a part of QA Service. If yes, then add it to work queue
      HashMap _oSchema = dbM.getSchema(schema_id);
      String validate = (String)_oSchema.get("validate");
      schema_url = (String)_oSchema.get("xml_schema");
      bValidate = validate.equals("1") ? true: false;
	}
	if(q_id != null) {
		qText = dbM.getQueryText(q_id);
		HashMap queryInfo = dbM.getQueryInfo(q_id);
		if (queryInfo!=null){
			query_file = Properties.queriesFolder + (String)queryInfo.get("query");
		}
	}
	
%>

<ed:breadcrumbs-push label="XQuery Sandbox" level="1" />
<tiles:insert definition="TmpHeader">
	<tiles:put name="title" value="XQuery Sandbox"/>
</tiles:insert>



<%@ include file="menu.jsp" %>

<div id="workarea">
<h1>XQuery Sandbox</h1>
<div id="main_table">
	<!--table border="0" cellspacing="1" cellpadding="2" width="100%"><tr><td-->
	<form name="f" action="sandbox" method="post">
		<label for="dataurlfield">URL to data file </label>
			<input type="text" class="textfield" name="source_url" size="75" id="dataurlfield" value="<%=Utils.isNullStr(source_url) ? "" : source_url%>"/>
			<input type="submit" name="findscripts" value=" Find scripts " title="Reads the XML Schema from the header of XML file and search the scripts from the repository" class="button"/>
		<br/><br/>
		<%
		// Run all scripts for one schema.
		if (schema_id!=null){
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
        				<input type="radio" name="script" value="<%=query_id%>" <% if (j == 0) %>checked="checked"<%;%>/>
        				<%=name%> - <a target="blank" href="<%=Names.QUERY_FOLDER%><%=query_file%>"><%=query_file%></a><br/>
        			<%
				}
				if (bValidate){
					%><input type="radio" name="script" value="-1" <% if (queries.size() == 0) %>checked="checked"<%;%>/>
							XML Schema validation<%
				}
        		%>
        		<%
    		}
		}
		else{
		%>
			<input type="hidden" name="sandboxtype" value="SCRIPT"/>
			<label for="scriptarea">XQuery script</label>
			<textarea name="XQSCRIPT" rows="25" cols="100" style="width:99%" id="scriptarea"><%=qText%></textarea>
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
			<input type="hidden" name="file_name" value="<%=query_file%>" />
			<input type="submit" name="save" value=" Save changes to file " />
		<% } %>
		<input type="hidden" name="ID" value="<%=q_id%>" />
	</form>
	<!--/td></tr></table-->
</div>
</div>
<tiles:insert definition="TmpFooter"/>
