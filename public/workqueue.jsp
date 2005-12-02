<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ page import="eionet.gdem.Constants, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices"%>
<%
	DbModuleIF dbM= GDEMServices.getDbModule();
%>

<ed:breadcrumbs-push label="Workqueue" level="1" />
<tiles:insert definition="TmpHeader">
	<tiles:put name="title" value="QA Jobs"/>
</tiles:insert>



<%@ include file="menu.jsp" %>


<div id="workarea">
		<%
			

			String[][] list = dbM.getJobData();
			String tmpFolder = Constants.TMP_FOLDER;
			String queriesFolder = Constants.QUERIES_FOLDER;
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
		<table class="sortable" border="0" cellspacing="1" cellpadding="2" width="100%">
		<thead>
			<tr>
				<th scope="col" width="10%">job ID</th>
				<th scope="col" width="30%">Document URL</th>
				<th scope="col" width="15%">XQuery script</th>
				<th scope="col" width="15%">Job Result</th>
				<th scope="col" width="10%">status</th>
				<th scope="col" width="20%">Started at</th>
				<%
			    boolean wqdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_WQ_PATH, "d");
			    if (wqdPrm){%>
					<th scope="col" >&#160;</th>
			    <%}%>
			</tr>
		</thead>
		<tbody>
		<%		
				for (int i=0; i<list.length; i++){
					String jobId = list[i][0]; 
					String url = list[i][1];
					String xqLongFileName = list[i][2];
					String xqFile = list[i][2].substring(list[i][2].lastIndexOf("/")+1);
					String resultFile = list[i][3].substring(list[i][3].lastIndexOf("/")+1);
					int status = Integer.parseInt(list[i][4]); 
					String timeStamp = list[i][5]; 
					String xqStringID = list[i][6]; 
					
			       	int xqID =0;
          			try { 
            			xqID=Integer.parseInt(xqStringID);
          			} catch(NumberFormatException n) {
            			xqID = 0;
          			}           
					
					String xqFileURL = "";
					String xqText = "Show script";
					if (xqID == Constants.JOB_VALIDATION){
						xqText = "Show XML Schema";
						xqFileURL = xqLongFileName;
					}
					else if (xqID == Constants.JOB_FROMSTRING){
						xqFileURL = tmpFolder + xqFile;
					}
					else{
						xqFileURL = queriesFolder + xqFile;
					}
					
					
					if (status==Constants.XQ_RECEIVED || status==Constants.XQ_DOWNLOADING_SRC || status==Constants.XQ_PROCESSING) 
						resultFile = null;
					//TODO change this constant (100)
					String urlName = (url.length() > 100 ? url.substring(0, 100) + "..." : url);

					//TODO Status name, maybe better to move to some Java common class
					String statusName="-- Unknown --";

					if (status==Constants.XQ_RECEIVED)
						statusName="JOB RECEIVED";
					if (status==Constants.XQ_DOWNLOADING_SRC)
						statusName="DOWNLOADING SOURCE";
					if (status==Constants.XQ_PROCESSING)
						statusName="PROCESSING";
					if (status==Constants.XQ_READY)
						statusName="READY";
					if (status==Constants.XQ_FATAL_ERR)
						statusName="FATAL ERROR";
					if (status==Constants.XQ_LIGHT_ERR)
						statusName="RECOVERABLE ERROR";



   		%>
					<tr>
						<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<%=jobId%>
						</td>
						<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<a title="<%=url%>" href="<%=url%>"><%=urlName%></a>
						</td>
  					<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<a href="<%=xqFileURL%>"><%=xqText%></a>
						</td>
						<td align="left" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<% if (resultFile != null) { %>
							<a href="<%=tmpFolder + resultFile%>">Show result</a>
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
<tiles:insert definition="TmpFooter"/>