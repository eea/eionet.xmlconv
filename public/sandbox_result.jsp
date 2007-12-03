<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c"%>
<%@ page import="eionet.gdem.Constants, eionet.gdem.utils.Utils, eionet.gdem.Properties, eionet.gdem.conversion.ssr.Names"%>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
%>

<%
	String result_text = request.getAttribute(Constants.XQ_RESULT_ATT)==null ? "":(String)request.getAttribute(Constants.XQ_RESULT_ATT);
	String error_msg = (request.getAttribute(Names.ERROR_ATT)==null) ? null : (String)request.getAttribute(Names.ERROR_ATT);

	String q_id = request.getParameter("ID")==null? null:(String)request.getParameter("ID");
	String schema_id = request.getParameter("SCHEMA_ID")==null ? null:(String)request.getParameter("SCHEMA_ID");
	String source_url = request.getParameter("source_url")==null ? null:(String)request.getParameter("source_url");
	String findscripts = request.getParameter("findscripts")==null ? null:(String)request.getParameter("findscripts");
	String xqscript = request.getParameter("XQSCRIPT")==null ? "":(String)request.getParameter("XQSCRIPT");

	pageContext.setAttribute("XQscript", xqscript, PageContext.PAGE_SCOPE);
%>

<ed:breadcrumbs-push label="XQuery Sandbox" level="1" />
<tiles:insert definition="TmpHeader">
	<tiles:put name="title" value="XQuery Sandbox Result"/>
</tiles:insert>



<%@ include file="menu.jsp" %>


<h1>XQuery Sandbox Result</h1>
<br/>
<form id="f" action="sandbox" method="post" accept-charset="utf-8">
<div id="main_table">
	<% if (error_msg!= null) { %>
		<div class="error-msg"><%=error_msg%></div>
	<% } %>
	<div id="sanbox_result">
		<%=result_text%>
	</div>
	<br/>
	<input type="hidden" name="ID" value="<%=(q_id==null) ? "" : q_id%>" />
	<input type="hidden" name="source_url" value="<%=(source_url==null) ? "" : source_url%>" />
	<input type="hidden" name="findscripts" value="<%=(findscripts==null) ? "" : findscripts%>" />
	<% if (Utils.isNullStr(xqscript) && !Utils.isNullStr(schema_id)){%>
		<input type="hidden" name="SCHEMA_ID" value="<%=(schema_id==null) ? "" : schema_id%>" />
	<%} else{ %>
		<textarea name="XQSCRIPT" rows="25" cols="100" style="display:none"><c:out value="${XQscript}" escapeXml="true" /></textarea>
	<%} %>
		
	<input type="submit" name="backToSandbox" value=" Back to sandbox " class="button" />
</div>
</form>
<tiles:insert definition="TmpFooter"/>
