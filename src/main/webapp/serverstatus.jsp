<%@page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="eionet.gdem.Properties, eionet.gdem.Constants"%>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
%>
<ed:breadcrumbs-push label="Server Status" level="1" />
<tiles:insert definition="TmpHeader">
    <tiles:put name="title" value="Server Status"/>
</tiles:insert>

<%@ include file="menu.jsp" %>
    <h1>Server Status</h1>
    
    <%
        boolean wqdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_SERVERSTATUS_PATH, "v");
     if ( wqdPrm == true){            %>
            
    <script type="text/javascript" src="<c:url value="/scripts/jquery-1.9.1.min.js" />"></script>

    <script type="text/javascript">
    // <![CDATA[
    
    function fetchServerData () {
        var request = new XMLHttpRequest();
        request.open('GET', '<c:url value="/webapi/serverstatus" />', true);

        request.onload = function() {
          if (request.status >= 200 && request.status < 400) {
            // Success!
            data = JSON.parse(request.responseText);
            displayServerStatus(data);
          } else {
            // We reached our target server, but it returned an error
            alert('Error parsing the JSON response');
          }
        };

        request.onerror = function() {
          // There was a connection error of some sort
          alert('Error contacting the REST endpoint');
        };

        request.send();
    }

    function displayServerStatus(data) {

	$("#t_serverStatus tbody").empty();	
	displayData = [];
	
	for (var i = 0 ; i < data.serverStatus.length ; i++ ) {
		displayRow = { instanceName : "null" ,
			jobs_by_status_0 : 0,
			jobs_by_status_1 : 0,
			jobs_by_status_2 : 0,
			jobs_by_status_3 : 0,
			health : null
		}
		temp = data.serverStatus[i] ; 
		displayRow.instanceName = temp.instanceName;
		displayRow.health = temp.health;

		for ( j in temp.jobs_by_status ) {
			displayRow[ "jobs_by_status_" + temp.jobs_by_status[j]["job_status"] ] = temp.jobs_by_status[j]["job_count"];
		}
		displayData.push( displayRow );
		f_displayRow(displayRow , i % 2 == 0 );
	}
        
        $("#timestamp").html("Last update : " + new Date (data.timestamp * 1000) );
}

function f_displayRow (data , odd_even ) {
    var tbl_body = "", tbl_row = "";
    var odd_even = odd_even || false;
    for ( i in data ) {
            if (data.hasOwnProperty(i) ) {
                    tbl_row += "<td>"+ data[i] +"</td>";
            }
    }
    the_row = "<tr class=\""+( odd_even ? "odd" : "even")+"\">"+tbl_row+"</tr>";
    $("#t_serverStatus tbody").append(the_row);
}
    
    fetchServerData();
    window.xmlconvHostname = String('<%=Properties.getHostname()%>');

    // ]]>
    </script>
    
<table id="t_serverStatus" class="datatable">
<thead>
    <tr>
            <th>Instance Name</th>
            <th>#Jobs with Status 0<br/>(received)</th>
            <th>#Jobs with Status 1<br/>(downloading)</th>
            <th>#Jobs with Status 2<br/>(working)</th>
            <th>#Jobs with Status 3<br/>(finished)</th>
            <th>Health</th>
</tr>
</thead>
<tbody></tbody>
</table>

    <input type="button" value="Refresh" onclick="return fetchServerData();"/>
    </br>
    <div id="timestamp"></div>
    <div>Current instance name: <%=Properties.getHostname()%></div>
    <% } else { %> 
    <div> You do not have rights for this page. Please contact the administrator. </div>
    <% } %>

<tiles:insert definition="TmpFooter"/>
