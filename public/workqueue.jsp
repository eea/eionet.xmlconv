<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="eionet.gdem.Constants, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices"%>
<%
	DbModuleIF dbM= GDEMServices.getDbModule();
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Jobs in the QA Service Workqueue</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
    <script type="text/javascript" src="util.js"></script>
    
</head>
<body>

<jsp:include page="location.jsp" flush='true'>
	<jsp:param name="name" value="Workqueue"/>
</jsp:include>
<%@ include file="menu.jsp" %>
<div id="workarea">
		<%
			

			String[][] list = dbM.getJobData();
			String tmpFolder = Constants.TMP_FOLDER;
		%>
			<% if (err!=null){
				%>
				<span id="errormessage"><%=err%></span>
			<%
			}
			%>
		<h1>Jobs</h1>
		<div id="operations">
			<!--ul>
				<li><a href="<%=Names.HOST_JSP%>" title="Add a new host">Add host</a></li>
 			</ul-->
		</div>
		<br/>
		<span>Currently there are following jobs in the queue...</span>
		<div id="main_table">
		<table border="0" cellspacing="1" cellpadding="2" width="100%">
		<thead>
			<tr>
				<th width="10%">job ID</th>
				<th width="30%">Document URL</th>
				<th width="15%">XQuery script</th>
				<th width="15%">Job Result</th>
				<th width="10%">status</th>
				<th	width="20%">Started at</th>
				<%
			    boolean wqdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_WQ_PATH, "d");
			    if (wqdPrm){%>
					<th>&#160;</th>
			    <%}%>
			</tr>
		</thead>
		<tbody>
		<%		
				for (int i=0; i<list.length; i++){
					String jobId = list[i][0]; 
					String url = list[i][1];
					String xqFile = list[i][2].substring(list[i][2].lastIndexOf("/")+1);
					String resultFile = list[i][3].substring(list[i][3].lastIndexOf("/")+1);
					int status = Integer.parseInt(list[i][4]); 
					String timeStamp = list[i][5]; 

					if (status==Constants.XQ_RECEIVED || status==Constants.XQ_DOWNLOADING_SRC || status==Constants.XQ_PROCESSING) 
						resultFile = null;
					//TODO change this constant (100)
					String urlName = (url.length() > 100 ? url.substring(0, 100) + "..." : url);

					//TODO Status name, maybe better to move to some Java common class
					String statusName="-- Unknown --";

					if (status==Constants.XQ_RECEIVED)
						statusName="JOB RECEIVED";
					if (status==Constants.XQ_DOWNLOADING_SRC)
						statusName="JOB RECEIVED";
					if (status==Constants.XQ_RECEIVED)
						statusName="JOB RECEIVED";
					if (status==Constants.XQ_READY)
						statusName="READY";
					if (status==Constants.XQ_FATAL_ERR)
						statusName="FATAL ERROR";
					if (status==Constants.XQ_LIGHT_ERR)
						statusName="RECOVERABLE ERROR";



   		%>
					<tr height="5">
						<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<%=jobId%>
						</td>
						<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<a title="<%=url%>" href="<%=url%>" target="_blank"><%=urlName%></a>
						</td>
  					<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<a href="<%=tmpFolder + xqFile%>" target="_blank">Show script</a>
						</td>
						<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<% if (resultFile != null) { %>
							<a href="<%=tmpFolder + resultFile%>" target="_blank">Show result</a>
							<% } else { out.println("*** Not ready ***"); } %>
						</td>
						<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<%=statusName%>
						</td>
						<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<%=timeStamp%>
						</td>
						<%
					    if (wqdPrm){%>
						<td align="middle" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
	   					<img onclick="job_<%=jobId%>.submit();" height="15" width="15" src="images/delete.png" title="Delete job"></img>
						<form name="job_<%=jobId%>" action="main" method="post">
                            <input type="hidden" name="ACTION" value="<%=Names.WQ_DEL_ACTION%>"/>
							<input type="hidden" name="ID" value="<%=jobId%>" />
						</form>		
						</td>
					    <%}%>
					</tr>
					<%
    	   		}
    	   	%>
				</tbody>
		 	</table>
		 	<br/>
		 	<br/>
	</div>					
	</div>
<%@ include file="footer.jsp" %>
</body>
</html>
