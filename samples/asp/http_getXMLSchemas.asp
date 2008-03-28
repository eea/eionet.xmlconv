<%
'/
'	ASP page for testing XMLCONV Conversion Service methods.
'
'	Author Enriko Käsper, TietoEnator
'/ 

	Dim server_url
	Dim xmlhttp, getUrl, method_path

	server_url = "http://80.235.29.171:8080/xmlconv/api"
	method_path = "/getXMLSchemas"
		
	getUrl = server_url & method_path

	Set xmlhttp = CreateObject("MSXML2.ServerXMLHTTP.3.0")
	xmlhttp.Open "GET", getUrl, False
	xmlhttp.Send

	if xmlhttp.status = 200 Then 
	 
		sXML = xmlhttp.responseText   ' Retrieve from server.

		Set oXML = CreateObject("MSXML2.DOMDocument.3.0")
		oXML.loadXML(sXML)
		Set objNodeList = oXML.getElementsByTagName("schema")
			for each x in objNodeList
				Response.write x.text
			next
		Set objNodeList = nothing
		set xmlhttp = nothing
	End If

	set xmlhttp = nothing
%>