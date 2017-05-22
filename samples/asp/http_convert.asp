<%
'/
'	ASP page for testing XMLCONV Conversion Service methods.
'
'	The script requires seed-general-report.xml and seed-general-report.zip 
'	files stored in the same directory	as asp file.
'
'	Author Enriko Käsper, TietoEnator
'/ 

	Dim server_url, method_path
	Dim xmlhttp, DataToSend, postUrl, headers

	server_url = "http://80.235.29.171:8080/xmlconv/api"
	method_path = "/convert"
		
	convert_url="https://svn.eionet.europa.eu/repositories/Reportnet/Dataflows/HabitatsDirectiveArticle17/xmlfiles/general-instancefile.xml"
	convert_id="26"

	postData="url=" & convert_url
	postData = postData & "&convert_id=" & convert_id


	postUrl = server_url & method_path
	Set xmlhttp = CreateObject("MSXML2.ServerXMLHTTP.3.0")
	xmlhttp.Open "POST", postUrl, False
	xmlhttp.setRequestHeader "Content-Type", "application/x-www-form-urlencoded"
	xmlhttp.Send postData

								
	Response.ContentType =xmlhttp.getResponseHeader("Content-type")

	if xmlhttp.status = 200 Then 
		Response.AddHeader "Content-Disposition", xmlhttp.getResponseHeader("Content-Disposition") 
	End If
	Response.BinaryWrite  xmlhttp.responseBody
	Response.End

	set xmlhttp = nothing
%>